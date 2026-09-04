package net.sweenus.simplyskills.abilities;

import net.neoforged.fml.ModList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellExecution;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.Random;

public class RogueAbilities {

    public static void passiveRogueBackstab(Entity target, Player player) {
        if (target instanceof LivingEntity livingTarget) {
            int weaknessDuration = SimplySkills.rogueConfig.passiveRogueBackstabWeaknessDuration;
            int weaknessAmplifier = SimplySkills.rogueConfig.passiveRogueBackstabWeaknessAmplifier;
            if (livingTarget.getVisualRotationYInDegrees() < (player.getVisualRotationYInDegrees() + 32) &&
                    livingTarget.getVisualRotationYInDegrees() > (player.getVisualRotationYInDegrees() - 32)) {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                        weaknessDuration, weaknessAmplifier, false, false, true));
            }
        }
    }

    public static void passiveRogueSmokeBomb(Player player) {
        int radius = SimplySkills.rogueConfig.passiveRogueSmokeBombRadius;
        int chance = SimplySkills.rogueConfig.passiveRogueSmokeBombChance;
        int auraDuration = SimplySkills.rogueConfig.passiveRogueSmokeBombAuraDuration;
        int blindnessDuration = SimplySkills.rogueConfig.passiveRogueSmokeBombBlindnessDuration;
        int blindnessAmplifier = SimplySkills.rogueConfig.passiveRogueSmokeBombBlindnessAmplifier;
        if (player.getRandom().nextInt(100) < chance) {
            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {

                        le.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                                blindnessDuration, blindnessAmplifier, false, false, true));

                    }
                }
            }
            player.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZINGAURA, auraDuration, 0, false, false, true));
            player.level().playSound(
                    null, player, SoundRegistry.SOUNDEFFECT32,
                    SoundSource.PLAYERS, 0.4f, 1.2f);
        }
    }

    public static boolean passiveRogueEvasionMastery(Player player) {

        int baseEvasionChance = SimplySkills.rogueConfig.passiveRogueEvasionMasteryChance;
        int extraEvasionChance = SimplySkills.rogueConfig.passiveRogueDeflectionIncreasedChance;
        int evasionChanceIncreasePerTier = SimplySkills.rogueConfig.passiveRogueEvasionMasteryChanceIncreasePerTier;
        int masteryEvasionMultiplier = SimplySkills.rogueConfig.passiveRogueEvasionMasterySignatureMultiplier;
        int mastery = 0;

        if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueDeflection, player)
                && player.getMainHandItem().getItem() instanceof SwordItem
                && player.getOffhandItem().getItem() instanceof SwordItem)
            mastery = baseEvasionChance + extraEvasionChance;
        else
            mastery = baseEvasionChance;

        int evasionMultiplier = 1;

        if (player.hasEffect(EffectRegistry.EVASION))
            evasionMultiplier = masteryEvasionMultiplier;

        if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueEvasionMasterySkilled, player))
            mastery = mastery + (evasionChanceIncreasePerTier * 2);
        else if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueEvasionMasteryProficient, player))
            mastery = mastery + evasionChanceIncreasePerTier;

        MobEffectInstance statusEffect = player.getEffect(EffectRegistry.BLADESTORM);
        if (statusEffect != null) {
            mastery = mastery + (statusEffect.getAmplifier()+1);
        }

        if (player.getRandom().nextInt(100) < (mastery * evasionMultiplier)) {
            if (player.hasEffect(EffectRegistry.AGILE)) {

                if (ModList.get().isLoaded("simplyswords"))
                    SimplySwordsGemEffects.deception(player);
                player.level().playSound(null, player, SoundRegistry.FX_SKILL_BACKSTAB,
                        SoundSource.PLAYERS, 1, 1);
                return false;
            }
        }
        return true;
    }

    public static void passiveRogueOpportunisticMastery(Entity target, Player player) {
        int basePoisonDuration = SimplySkills.rogueConfig.passiveRogueOpportunisticMasteryPoisonDuration;
        int basePoisonAmplifier = SimplySkills.rogueConfig.passiveRogueOpportunisticMasteryPoisonAmplifier;
        int poisonDurationIncreasePerTier = SimplySkills.rogueConfig.passiveRogueOpportunisticMasteryPoisonDurationIncreasePerTier;

        int mastery = basePoisonDuration;

        if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueOpportunisticMasterySkilled, player))
            mastery = mastery + (poisonDurationIncreasePerTier * 2);
        else if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueOpportunisticMasteryProficient, player))
            mastery = mastery + poisonDurationIncreasePerTier;

        if (target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, mastery, basePoisonAmplifier, false, false, true));
        }

    }

    public static void passiveRogueBackstabStealth(Player player) {
        int stealthChance = SimplySkills.rogueConfig.passiveRogueBackstabStealthChancePerEnemy;
        int stealthDuration = SimplySkills.rogueConfig.passiveRogueBackstabStealthDuration;
        if (player.tickCount % 20 == 0) {
            AABB box = HelperMethods.createBox(player, 8);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null) {
                    if ((entities instanceof LivingEntity le) &&
                            le.hasEffect(MobEffects.WEAKNESS)
                            && HelperMethods.checkFriendlyFireAOE(le, player)) {
                        if (player.getRandom().nextInt(100) < stealthChance) {
                            player.addEffect(new MobEffectInstance(EffectRegistry.STEALTH, stealthDuration, 0, false, false, true));
                            player.level().playSound(
                                    null, player, SoundRegistry.SOUNDEFFECT39,
                                    SoundSource.PLAYERS, 0.6f, 1.6f);
                        }
                    }
                }
            }
        }
    }

    public static void passiveRoguePreparationShadowstrike(Player player) {
        if (player instanceof ServerPlayer) {
            if (HelperMethods.isUnlocked("simplyskills:rogue",
                    SkillReferencePosition.rogueSpecialisationPreparationShadowstrike, player)) {
                int dashRange = SimplySkills.rogueConfig.signatureRoguePreparationShadowstrikeRange;
                int dashRadius = SimplySkills.rogueConfig.signatureRoguePreparationShadowstrikeRadius;
                int dashDamageModifier = SimplySkills.rogueConfig.signatureRoguePreparationShadowstrikeDamageModifier;
                int dashDamage = (int) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                DamageSource dashSource = player.level().damageSources().playerAttack(player);
                BlockPos blockPos = player.blockPosition().relative(player.getMotionDirection(), dashRange);

                AABB box = HelperMethods.createBoxBetween(player.blockPosition(), blockPos, dashRadius);
                for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {
                            le.hurt(dashSource, dashDamage * dashDamageModifier);

                            if (HelperMethods.isUnlocked("simplyskills:rogue",
                                    SkillReferencePosition.rogueSpecialisationPreparationShadowstrikeVampire, player)) {
                                HelperMethods.buffSteal(player, le, true, true, false, false);
                                le.addEffect(new MobEffectInstance(EffectRegistry.DEATHMARK, 120, 0, false, false, true));
                            }

                        }
                    }
                }
                if (!player.onGround())
                    dashRange = dashRange /5;
                player.setDeltaMovement(player.getLookAngle().scale(+dashRange));
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.hurtMarked = true;

                player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT15,
                        SoundSource.PLAYERS, 0.6f, 1.3f);

            }
        }
    }


    //------- SIGNATURE ABILITIES --------


    // Evasion
    public static boolean signatureRogueEvasion(String rogueSkillTree, Player player) {
        int evasionDuration = SimplySkills.rogueConfig.signatureRogueEvasionDuration;
        int fanOfBladesDuration = SimplySkills.rogueConfig.signatureRogueFanOfBladesDuration;
        int fanOfBladesStacks = SimplySkills.rogueConfig.signatureRogueFanOfBladesStacks;

        player.addEffect(new MobEffectInstance(EffectRegistry.EVASION, evasionDuration, 0, false, false, true));

        if (HelperMethods.isUnlocked(rogueSkillTree,
                SkillReferencePosition.rogueSpecialisationEvasionFanOfBladesAssault, player))
            fanOfBladesStacks = fanOfBladesStacks*2;

        if (HelperMethods.isUnlocked(rogueSkillTree,
                SkillReferencePosition.rogueSpecialisationEvasionFanOfBlades, player))
            player.addEffect(new MobEffectInstance(EffectRegistry.FANOFBLADES,
                    fanOfBladesDuration, fanOfBladesStacks - 1, false, false, true));

        return true;
    }

    // Preparation
    public static boolean signatureRoguePreparation(String rogueSkillTree, Player player) {
        int preparationDuration = SimplySkills.rogueConfig.signatureRoguePreparationDuration;
        int speedAmplifier = SimplySkills.rogueConfig.signatureRoguePreparationSpeedAmplifier;

        player.addEffect(new MobEffectInstance(EffectRegistry.STEALTH,
                preparationDuration, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                preparationDuration, speedAmplifier, false, false, true));

        if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueSpecialisationPreparationShadowstrikeShield, player)) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.BARRIER, 20, 0, 5);
            if (player.hasEffect(EffectRegistry.REVEALED))
                player.removeEffect(EffectRegistry.REVEALED);
        }

        player.level().playSound(
                null, player, SoundRegistry.SOUNDEFFECT39,
                SoundSource.PLAYERS, 0.6f, 1.6f);

        if (HelperMethods.isUnlocked(rogueSkillTree,
                SkillReferencePosition.rogueSpecialisationPreparationShadowstrike, player)) {

            RogueAbilities.passiveRoguePreparationShadowstrike(player);
        }

        return true;
    }

    // Siphoning Strikes
    public static boolean signatureRogueSiphoningStrikes(String rogueSkillTree, Player player) {

        int siphoningStrikesduration = SimplySkills.rogueConfig.signatureRogueSiphoningStrikesDuration;
        int siphoningStrikesStacks = SimplySkills.rogueConfig.signatureRogueSiphoningStrikesStacks;
        int siphoningStrikesMightyStacks = SimplySkills.rogueConfig.signatureRogueSiphoningStrikesMightyStacks;

        player.addEffect(new MobEffectInstance(EffectRegistry.SIPHONINGSTRIKES,
                siphoningStrikesduration, siphoningStrikesStacks - 1, false, false, true));

        if (HelperMethods.isUnlocked(rogueSkillTree,
                SkillReferencePosition.rogueSpecialisationSiphoningStrikesMighty, player))
            HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, siphoningStrikesduration,
                    siphoningStrikesMightyStacks, 5);

        if (HelperMethods.isUnlocked(rogueSkillTree,
                SkillReferencePosition.rogueSpecialisationSiphoningStrikesAura, player))
            HelperMethods.incrementStatusEffect(player, EffectRegistry.IMMOBILIZINGAURA, siphoningStrikesduration,
                    1, 2);

        return true;
    }

    // Rogue Dagger Storm summon UNUSED
    public static void daggerstormSummon(Player player) {
        AABB box = HelperMethods.createBox(player, 8);
        int count = 12;
        int chance = 95;
        int location = -3;
        for (Entity entities : player.level().getEntities(player, box, EntitySelector.ENTITY_STILL_ALIVE)) {

            if (entities != null) {
                if ((entities instanceof SpellProjectile spe) && spe.getOwner() == player) {
                    count--;
                }

                if ((entities instanceof LivingEntity le) && count > 0) {
                    if (player.getRandom().nextInt(100) < chance && HelperMethods.checkFriendlyFireAOE(le, player)) {
                        //SignatureAbilities.castSpellEngineIndirectTarget(player,
                        //"simplyskills:physical_dagger_homing",
                        //9, player);

                        SpellExecution.ImpactContext context = new SpellExecution.ImpactContext();
                        SpellProjectile projectile = new SpellProjectile(player.level(),
                                player, player.getX() + location, player.getY(), player.getZ() + location,
                                SpellProjectile.Behaviour.FLY,
                                SpellRegistry.from(player.level()).getHolder(ResourceLocation.parse("simplyskills:physical_dagger_homing")).orElseThrow(),
                                context, null);
                        Random random = new Random();
                        projectile.setDeltaMovement(player.getDeltaMovement().scale(5).zRot(random.nextInt(280)).xRot(random.nextInt(280)));
                        projectile.range = 356;
                        ProjectileUtil.rotateTowardsMovement(projectile, 0.2F);
                        projectile.setFollowedTarget(le);
                        player.level().addFreshEntity(projectile);
                        location ++;
                    }
                }

            }
        }
    }


}
