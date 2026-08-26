package net.sweenus.simplyskills.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class RighteousShieldEffect extends MobEffect {
    public RighteousShieldEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.RIGHTEOUSSHIELD)) {
                MobEffectInstance righteousShield = player.getEffect(EffectRegistry.RIGHTEOUSSHIELD);
                if (righteousShield == null)
                    return true;

                if (righteousShield.getDuration() == 10) {
                    player.level().playSound(null, player, SoundEvents.PLAYER_ATTACK_STRONG,
                            SoundSource.PLAYERS, 1f, 1.1f);
                    MobEffectInstance effect = player.getEffect(EffectRegistry.GOLDENAEGIS);
                    if (effect != null) {
                        int aegisStacks = effect.getAmplifier();

                        if (aegisStacks > 14) {
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:righteous_shield_projectile_4", 3, player, null);
                            HelperMethods.decrementStatusEffects(player, EffectRegistry.GOLDENAEGIS, 15);
                        } else if (aegisStacks > 9) {
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:righteous_shield_projectile_3", 3, player, null);
                            HelperMethods.decrementStatusEffects(player, EffectRegistry.GOLDENAEGIS, 10);
                        } else if (aegisStacks > 4) {
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:righteous_shield_projectile_2", 3, player, null);
                            HelperMethods.decrementStatusEffects(player, EffectRegistry.GOLDENAEGIS, 5);
                        } else {
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:righteous_shield_projectile", 3, player, null);
                            player.removeEffect(EffectRegistry.GOLDENAEGIS);
                        }
                    }
                }

            }
        }
        super.applyEffectTick(livingEntity, amplifier);
        return true;
}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
