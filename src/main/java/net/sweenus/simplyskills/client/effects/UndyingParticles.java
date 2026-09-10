package net.sweenus.simplyskills.client.effects;

import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.fx.ParticleHelper;

public class UndyingParticles implements CustomParticleStatusEffect.Spawner {

    private final ParticleGroup particles;

    public UndyingParticles(int particleCount) {
        this.particles = ParticleGroupBuilder.of("spell_engine:holy_spark_mini")
                .batch(batch -> batch.shape(ParticleGroup.Shape.PIPE)
                        .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                        .count(particleCount)
                        .speed(0.1F, 0.7F));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = particles.copy();
        scaledParticles.batch.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.level(), livingEntity, scaledParticles);
    }

}
