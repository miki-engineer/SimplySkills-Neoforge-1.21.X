package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class RageEffect extends MobEffect {
    public RageEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity.hasEffect(EffectRegistry.EXHAUSTION)
                    && livingEntity.tickCount % 10 == 0) {
                if (livingEntity.getEffect(EffectRegistry.RAGE).getAmplifier() > 25)
                    HelperMethods.decrementStatusEffects(livingEntity, EffectRegistry.EXHAUSTION, 1);
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
