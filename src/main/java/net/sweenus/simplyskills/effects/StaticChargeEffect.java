package net.sweenus.simplyskills.effects;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_engine.fx.SpellEngineParticles;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class StaticChargeEffect extends MobEffect {

    public Player ownerEntity;
    public StaticChargeEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            int leapFrequency = SimplySkills.wizardConfig.signatureWizardStaticChargeLeapFrequency;
            int leapChance = SimplySkills.wizardConfig.signatureWizardStaticChargeLeapChance;
            int weaknessDuration = SimplySkills.wizardConfig.signatureWizardStaticChargeWeaknessDuration;
            int weaknessAmplifier = SimplySkills.wizardConfig.signatureWizardStaticChargeWeaknessAmplifier;

            if (livingEntity.tickCount % leapFrequency == 0) {


                AABB box = HelperMethods.createBox(livingEntity, 9);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null && ownerEntity != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, ownerEntity)
                        && le.getRandom().nextInt(100) < leapChance) {
                            SignatureAbilities.castSpellEngineIndirectTarget(ownerEntity,
                                    "simplyskills:static_charge",
                                    3, le, null);
                            HelperMethods.spawnWaistHeightParticles((ServerLevel) livingEntity.level(), SpellEngineParticles.lightning_arc_A.type(), livingEntity, le, 6);
                            le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, weaknessAmplifier, false, false, true));
                            Holder<MobEffect> sc = EffectRegistry.STATICCHARGE;
                            HelperMethods.decrementStatusEffect(livingEntity, sc);
                            if (livingEntity.hasEffect(sc)) {
                                le.addEffect(new MobEffectInstance(sc,
                                        livingEntity.getEffect(sc).getDuration(),
                                        livingEntity.getEffect(sc).getAmplifier(), false, false, true));
                                livingEntity.removeEffect(sc);
                            }
                            onHitEffects(ownerEntity, calculateSpeedChance(ownerEntity), le);

                            break;
                        }
                    }
                }
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }

    public static int calculateSpeedChance(Player ownerEntity) {

        int speedBaseChance = SimplySkills.wizardConfig.signatureWizardStaticDischargeBaseSpeedChance;
        int speedChancePerTier = SimplySkills.wizardConfig.signatureWizardStaticDischargeSpeedChancePerTier;

        int speedChance = speedBaseChance;
        if (HelperMethods.isUnlocked("simplyskills:wizard",
                SkillReferencePosition.wizardSpecialisationStaticDischargeSpeedThree, ownerEntity))
            speedChance = speedChance + (speedChancePerTier * 2);
        else if (HelperMethods.isUnlocked("simplyskills:wizard",
                SkillReferencePosition.wizardSpecialisationStaticDischargeSpeedTwo, ownerEntity))
            speedChance = speedChance + speedChancePerTier;

        return speedChance;
    }

    public static void onHitEffects(Player ownerEntity, int speedChance, LivingEntity le) {

        int dischargeSpeedDuration = SimplySkills.wizardConfig.signatureWizardStaticDischargeSpeedDuration;
        int staticDischargeSpeedStacks = SimplySkills.wizardConfig.signatureWizardStaticDischargeSpeedStacks;
        int staticDischargeSpeedMaxAmplifier = SimplySkills.wizardConfig.signatureWizardStaticDischargeSpeedMaxAmplifier;

        if (HelperMethods.isUnlocked("simplyskills:wizard",
                SkillReferencePosition.wizardSpecialisationStaticDischargeSpeed, ownerEntity)
                && ownerEntity.getRandom().nextInt(100) < speedChance)
            HelperMethods.incrementStatusEffect(ownerEntity, MobEffects.MOVEMENT_SPEED,
                    dischargeSpeedDuration,
                    staticDischargeSpeedStacks,
                    staticDischargeSpeedMaxAmplifier);

        if (HelperMethods.isUnlocked("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationStaticDischargeLightningOrb, ownerEntity)
                && ownerEntity.getRandom().nextInt(100) < speedChance / 2)
            SignatureAbilities.castSpellEngineIndirectTarget(ownerEntity,
                    "simplyskills:lightning_ball_homing",
                    3, le, HelperMethods.getBlockLookingAt(ownerEntity, 256));
    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (!entity.level().isClientSide()) {
            AABB box = HelperMethods.createBox(entity, 80);
            for (Entity entities : entity.level().getEntities(entity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                if (entities != null) {
                    if (entities instanceof Player pe) {
                        if (HelperMethods.isUnlocked("simplyskills:wizard",
                                SkillReferencePosition.wizardSpecialisationStaticDischargeLeap, pe)) {
                            ownerEntity = pe;
                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
