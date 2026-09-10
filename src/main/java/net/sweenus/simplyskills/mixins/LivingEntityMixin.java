package net.sweenus.simplyskills.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.effects.UndyingEffect;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.DynamicDamage;
import net.sweenus.simplyskills.util.HelperMethods;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {


    @Shadow protected float lastHurt;

    @Shadow private long lastDamageStamp;

    @Shadow public abstract float getMaxHealth();

    @Shadow @Nullable public abstract LivingEntity getLastHurtByMob();

    @Shadow public abstract float getHealth();

    @Shadow public abstract boolean hurt(DamageSource source, float amount);

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void simplyskills$barrier(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity.hasEffect(EffectRegistry.BARRIER)) {
            HelperMethods.decrementStatusEffect(livingEntity, EffectRegistry.BARRIER);
            livingEntity.level().playSound(null, livingEntity, SoundRegistry.FX_SKILL_BACKSTAB,
                    SoundSource.PLAYERS, 1, 1);
            cir.setReturnValue(false);
        }
    }

    //Prevent detection when stealthed
    @Inject(at = @At("HEAD"), method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    public void simplyskills$canTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target.hasEffect(EffectRegistry.STEALTH))
            cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "isDeadOrDying", cancellable = true)
    public void simplyskills$tick(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object)this;
        if (livingEntity.getHealth() <= 0.0F && livingEntity.hasEffect(EffectRegistry.UNDYING)) {
            livingEntity.setHealth(1.0F);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("RETURN"), cancellable = true)
    protected void simplyskills$modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity attacker = this.getLastHurtByMob();
        LivingEntity livingEntity = (LivingEntity) (Object)this;

        float newAmount = DynamicDamage.dynamicDamageReduction(attacker, livingEntity, amount, lastHurt, cir.getReturnValue(), lastDamageStamp);

        if (newAmount != amount)
            cir.setReturnValue(newAmount);

    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void simplyskills$readCustomDataFromNbt(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.level().isClientSide && SimplySkills.generalConfig.enableDAS && !(livingEntity instanceof Player)) {
            DynamicDamage.dynamicPlayerCountScaling(livingEntity);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void simplyskills$onEntityTick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.level().isClientSide)
            UndyingEffect.applyPendingDeath(livingEntity);
        if (!livingEntity.level().isClientSide && SimplySkills.generalConfig.enableDAS && !(livingEntity instanceof Player)
                && (livingEntity.tickCount % (SimplySkills.generalConfig.DASUpdateFrequency * 20) == 0 || livingEntity.tickCount == 20)) {
            DynamicDamage.dynamicPlayerCountScaling(livingEntity);
        }
    }

}
