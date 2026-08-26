package net.sweenus.simplyskills.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.sweenus.simplyskills.registry.EffectRegistry;

public class MagicCircleEffect extends MobEffect {
    public MagicCircleEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.MAGICCIRCLE)) {
                MobEffectInstance magicCircle = player.getEffect(EffectRegistry.MAGICCIRCLE);
                if (magicCircle == null)
                    return true;

                if (magicCircle.getDuration() % 20 == 0) {
                    player.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZE, 25, 0, false, false, true));
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
