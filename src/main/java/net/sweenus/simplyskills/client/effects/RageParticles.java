package net.sweenus.simplyskills.client.effects;

import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.fx.ParticleHelper;

public class RageParticles implements CustomParticleStatusEffect.Spawner {

    private final ParticleGroup particles;

    public RageParticles(int particleCount) {
        this.particles = ParticleGroupBuilder.of("minecraft:crimson_spore")
                .batch(batch -> batch.shape(ParticleGroup.Shape.PIPE)
                        .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                        .count(particleCount)
                        .speed(0.01F, 0.03F));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = particles.copy();
        scaledParticles.batch.count += Math.max((amplifier / 10), 1);
        ParticleHelper.play(livingEntity.level(), livingEntity, scaledParticles);
    }

}
