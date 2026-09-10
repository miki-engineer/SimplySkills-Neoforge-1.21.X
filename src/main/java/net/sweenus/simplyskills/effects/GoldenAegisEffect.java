package net.sweenus.simplyskills.effects;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.sweenus.simplyskills.registry.SoundRegistry;

public class GoldenAegisEffect extends MobEffect {
    public GoldenAegisEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

        }
        super.applyEffectTick(livingEntity, amplifier);
        return true;
}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.SPELL_GAIN_BARRIER,
                SoundSource.PLAYERS, 0.4f, 1 + ((float) amplifier /10));
    }

}
