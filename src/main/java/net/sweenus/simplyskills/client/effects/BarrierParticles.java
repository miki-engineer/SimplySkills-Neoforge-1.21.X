package net.sweenus.simplyskills.client.effects;

import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.fx.ParticleHelper;
import net.sweenus.simplyskills.registry.EffectRegistry;

public class BarrierParticles implements CustomParticleStatusEffect.Spawner {

    private final ParticleGroup particles;

    public BarrierParticles(int particleCount) {
        this.particles = ParticleGroupBuilder.of("spell_engine:arcane_spell")
                .batch(batch -> batch.shape(ParticleGroup.Shape.PIPE)
                        .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                        .count(particleCount)
                        .speed(0.1F, 0.4F));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.getBbHeight() > 1 && !livingEntity.hasEffect(EffectRegistry.STEALTH)) {
            var scaledParticles = particles.copy();
            scaledParticles.batch.count *= (float) ((amplifier * 0.15) + 1);
            ParticleHelper.play(livingEntity.level(), livingEntity, scaledParticles);
        }
    }

}
