package net.sweenus.simplyskills.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class UndyingEffect extends MobEffect {
    public static final String PENDING_DEATH = "simplyskills_undying_pending_death";

    public UndyingEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.hasEffect(EffectRegistry.UNDYING)) {
                int durationRemaining = livingEntity.getEffect(EffectRegistry.UNDYING).getDuration();
                if (durationRemaining == 35 && ((livingEntity.getHealth() / livingEntity.getMaxHealth()) * 100) < 60)
                    livingEntity.level().playSound(null, livingEntity, SoundRegistry.SOUNDEFFECT11,
                            SoundSource.PLAYERS, 0.3f, 1f);
            }

        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (((entity.getHealth() / entity.getMaxHealth()) * 100) < 60) {
            entity.getPersistentData().putBoolean(PENDING_DEATH, true);
        } else {
            entity.level().playSound(null, entity, SoundRegistry.SPELL_RADIANT_EXPIRE,
                    SoundSource.PLAYERS, 0.4f, 1);
        }
    }

    public static void applyPendingDeath(LivingEntity entity) {
        if (entity.getPersistentData().getBoolean(PENDING_DEATH)
                && !entity.hasEffect(EffectRegistry.UNDYING)) {
            entity.getPersistentData().remove(PENDING_DEATH);
            entity.hurt(entity.damageSources().magic(), entity.getMaxHealth());
            entity.level().playSound(null, entity, SoundRegistry.SOUNDEFFECT36,
                    SoundSource.PLAYERS, 0.4f, 1.3f);
            HelperMethods.spawnParticlesPlane(
                    entity.level(),
                    ParticleTypes.SOUL,
                    entity.blockPosition(),
                    2, 0, 0.4, 0);
            HelperMethods.spawnParticlesPlane(
                    entity.level(),
                    ParticleTypes.SCULK_SOUL,
                    entity.blockPosition(),
                    2, 0, 0.6, 0);
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.SPELL_CELESTIAL_HIT,
                SoundSource.PLAYERS, 0.1f, 1.4f);
    }

}
