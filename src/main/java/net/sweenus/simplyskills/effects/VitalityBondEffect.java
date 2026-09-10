package net.sweenus.simplyskills.effects;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.spell_engine.fx.SpellEngineParticles;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class VitalityBondEffect extends MobEffect {

    public LivingEntity target;

    public VitalityBondEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    public void setTarget(LivingEntity livingEntity) {
        target = livingEntity;
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.getEffect(EffectRegistry.VITALITYBOND) instanceof SimplyStatusEffectInstance statusEffect) {
                target = statusEffect.getSourceEntity();
            }


            //Target isn't null & isn't us
            float incrementFrequency = 10;
            if (target != null && target != livingEntity && livingEntity.tickCount %incrementFrequency == 0) {
                LivingEntity healingEntity = null;
                LivingEntity sacrificeEntity = null;
                float incrementAmount = 1;
                float livingEntityHealthPercent = ((livingEntity.getHealth() / livingEntity.getMaxHealth()) * 100);
                float targetHealthPercent = ((target.getHealth() / target.getMaxHealth()) * 100);

                if (amplifier > 0)
                    amplifier = 0;

                //Sacrifice attack speed to grant recipient attack & movespeed
                if (HelperMethods.isUnlocked("simplyskills:cleric", SkillReferencePosition.clericSpecialisationSacredOrbSpeed, target)) {
                    HelperMethods.incrementStatusEffect(livingEntity, MobEffects.DIG_SPEED, (int) incrementFrequency+5, 1, 6);
                    HelperMethods.incrementStatusEffect(livingEntity, MobEffects.MOVEMENT_SPEED, (int) incrementFrequency+5, 1, 2);
                    HelperMethods.incrementStatusEffect(target, MobEffects.DIG_SLOWDOWN, (int) incrementFrequency+5, 1, 3);
                }

                // Take debuffs from recipient
                if (HelperMethods.isUnlocked("simplyskills:cleric", SkillReferencePosition.clericSpecialisationSacredOrbDebuffs, target)) {
                    HelperMethods.buffSteal(target, livingEntity, true, true, true, false);
                }

                // Copy buffs to recipient
                if (HelperMethods.isUnlocked("simplyskills:cleric", SkillReferencePosition.clericSpecialisationSacredOrbBuffs, target)) {
                    HelperMethods.buffSteal(livingEntity, target, false, false, false, false);
                }

                if ( Math.abs(livingEntityHealthPercent - targetHealthPercent) > 15 && (livingEntityHealthPercent < 85 || targetHealthPercent < 85)) {
                    if (livingEntityHealthPercent > targetHealthPercent) {
                        healingEntity = target;
                        sacrificeEntity = livingEntity;
                    } else if (livingEntityHealthPercent < targetHealthPercent) {
                        healingEntity = livingEntity;
                        sacrificeEntity = target;
                    }

                    HelperMethods.spawnParticlesPlane(livingEntity.level(), SpellEngineParticles.magic_heal.type(),
                            healingEntity.blockPosition(), 1, 0.01, 0.9, 0.03);
                    HelperMethods.spawnParticlesPlane(livingEntity.level(), SpellEngineParticles.magic_holy.type(),
                            sacrificeEntity.blockPosition(), 1, 0.01, 0.9, 0.03);

                    sacrificeEntity.level().playSound(null, sacrificeEntity, SoundRegistry.SOUNDEFFECT28,
                            SoundSource.PLAYERS, 0.1f, 1.1f);
                    healingEntity.level().playSound(null, healingEntity, SoundRegistry.SOUNDEFFECT25,
                            SoundSource.PLAYERS, 0.1f, 1.0f);

                    if (sacrificeEntity != null && healingEntity != null && sacrificeEntity.getHealth() > 4 + incrementAmount) {
                        sacrificeEntity.setHealth(sacrificeEntity.getHealth() - incrementAmount);
                        healingEntity.heal(incrementAmount);
                    }
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
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.SPELL_RADIANT_HIT,
                SoundSource.PLAYERS, 0.1f, 1.5f);
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.SPELL_RADIANT_EXPIRE,
                SoundSource.PLAYERS, 0.4f, 1);
    }

}
