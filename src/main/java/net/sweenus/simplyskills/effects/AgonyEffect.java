package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;

public class AgonyEffect extends MobEffect {

    public LivingEntity target;

    public AgonyEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    public void setTarget(LivingEntity livingEntity) {
        target = livingEntity;
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

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
