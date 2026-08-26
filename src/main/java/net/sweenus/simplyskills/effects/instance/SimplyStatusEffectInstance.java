package net.sweenus.simplyskills.effects.instance;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class SimplyStatusEffectInstance extends MobEffectInstance {

    public LivingEntity sourceEntity;

    public SimplyStatusEffectInstance(Holder<MobEffect> type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(type, duration, amplifier, ambient, showParticles, showIcon);
    }

    public LivingEntity getSourceEntity() {
        if (sourceEntity != null)
            return sourceEntity;

        return null;
    }

    public void setSourceEntity(LivingEntity entity) {
        if (entity != null)
            sourceEntity = entity;
    }


}
