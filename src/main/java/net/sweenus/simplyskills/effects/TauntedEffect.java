package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;

public class TauntedEffect extends MobEffect {

    public TauntedEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            LivingEntity target = null;
            if (livingEntity.getEffect(EffectRegistry.TAUNTED) instanceof SimplyStatusEffectInstance statusEffect) {
                target = statusEffect.getSourceEntity();
            }


            if (target != null && (livingEntity instanceof Mob mobEntity)) {
                if (mobEntity.getTarget() != target)
                    mobEntity.setTarget(target);
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
