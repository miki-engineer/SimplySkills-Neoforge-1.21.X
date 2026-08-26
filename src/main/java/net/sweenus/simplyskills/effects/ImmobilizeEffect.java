package net.sweenus.simplyskills.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.sweenus.simplyskills.util.HelperMethods;

import static java.lang.Math.min;

public class ImmobilizeEffect extends MobEffect {
    private BlockPos blockPos;
    public ImmobilizeEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            float damage = min((float)(livingEntity.getMaxHealth() * 0.1), 10);
            if (!livingEntity.blockPosition().equals(blockPos)) {
                if (livingEntity.tickCount % 5 == 0) {
                    blockPos = livingEntity.blockPosition();
                    livingEntity.hurt(livingEntity.damageSources().generic(), damage);
                    HelperMethods.incrementStatusEffect(livingEntity, MobEffects.MOVEMENT_SLOWDOWN, 80, 1, 9);
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
