package net.sweenus.simplyskills.abilities;

import net.neoforged.fml.ModList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class BerserkerAbilities {

    public static void passiveBerserkerSwordMastery(Player player) {
        int frequency = SimplySkills.berserkerConfig.passiveBerserkerSwordMasteryFrequency;
        int baseSpeedAmplifier = SimplySkills.berserkerConfig.passiveBerserkerSwordMasteryBaseSpeedAmplifier;
        int speedAmplifierPerTier = SimplySkills.berserkerConfig.passiveBerserkerSwordMasterySpeedAmplifierPerTier;
        if (player.tickCount % frequency == 0) {
            if (player.getMainHandItem() != null) {
                if ((player.getMainHandItem().getItem() instanceof SwordItem)
                        && !HelperMethods.stringContainsAny(player.getMainHandItem().getItem().getDescription().toString(),
                        new String[] {"Axe", "axe", "molten_edge", "livyatan", "soulpyre"})) {
                    int mastery = baseSpeedAmplifier;

                    if (HelperMethods.isUnlocked("simplyskills:berserker",
                            SkillReferencePosition.berserkerSwordMasterySkilled, player)
                            && player.getOffhandItem().isEmpty())
                        player.addEffect(new MobEffectInstance(EffectRegistry.MIGHT,
                                frequency + 5, 0, false, false, true));
                    if (HelperMethods.isUnlocked("simplyskills:berserker",
                            SkillReferencePosition.berserkerSwordMasteryProficient, player))
                        mastery = mastery + speedAmplifierPerTier;

                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                            frequency + 5, mastery, false, false, true));
                }
            }
        }
    }

    public static void passiveBerserkerAxeMastery(Player player) {
        int frequency = SimplySkills.berserkerConfig.passiveBerserkerAxeMasteryFrequency;
        int baseStrengthAmplifier = SimplySkills.berserkerConfig.passiveBerserkerAxeMasteryBaseStrengthAmplifier;
        int strengthAmplifierPerTier = SimplySkills.berserkerConfig.passiveBerserkerAxeMasteryStrengthAmplifierPerTier;
        if (player.tickCount % frequency == 0) {
            if (player.getMainHandItem() != null) {
                if ((player.getMainHandItem().getItem() instanceof AxeItem)
                        || HelperMethods.stringContainsAny(player.getMainHandItem().getItem().getDescription().toString(),
                        new String[] {"Axe", "axe", "molten_edge", "livyatan", "soulpyre"})) {

                    int mastery = baseStrengthAmplifier;

                    if (HelperMethods.isUnlocked("simplyskills:berserker",
                            SkillReferencePosition.berserkerAxeMasterySkilled, player))
                        mastery = mastery + (strengthAmplifierPerTier * 2);
                    else if (HelperMethods.isUnlocked("simplyskills:berserker",
                            SkillReferencePosition.berserkerAxeMasteryProficient, player))
                        mastery = mastery + strengthAmplifierPerTier;

                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                            frequency + 5, mastery, false, false, true));
                }
            }
        }
    }

    public static void passiveBerserkerIgnorePain(Player player) {
        int frequency = SimplySkills.berserkerConfig.passiveBerserkerIgnorePainFrequency;
        double healthThreshold = SimplySkills.berserkerConfig.passiveBerserkerIgnorePainHealthThreshold;
        int baseResistanceAmplifier = SimplySkills.berserkerConfig.passiveBerserkerIgnorePainBaseResistanceAmplifier;
        int resistanceAmplifierPerTier = SimplySkills.berserkerConfig.passiveBerserkerIgnorePainResistanceAmplifierPerTier;
        if (player.tickCount % frequency == 0) {
            int resistanceStacks = baseResistanceAmplifier;
            if (player.getHealth() <= (healthThreshold * player.getMaxHealth())) {

                if (HelperMethods.isUnlocked("simplyskills:berserker",
                        SkillReferencePosition.berserkerIgnorePainSkilled, player))
                    resistanceStacks = resistanceStacks + (resistanceAmplifierPerTier * 2);
                else if (HelperMethods.isUnlocked("simplyskills:berserker",
                        SkillReferencePosition.berserkerIgnorePainProficient, player))
                    resistanceStacks = resistanceStacks + resistanceAmplifierPerTier;

                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                        frequency + 5, resistanceStacks, false, false, true));
            }
        }
    }

    public static void passiveBerserkerRecklessness(Player player) {
        int frequency = SimplySkills.berserkerConfig.passiveBerserkerRecklessnessFrequency;
        double healthThreshold = SimplySkills.berserkerConfig.passiveBerserkerRecklessnessHealthThreshold;
        int weaknessAmplifier = SimplySkills.berserkerConfig.passiveBerserkerRecklessnessWeaknessAmplifier;
        if (player.tickCount % frequency == 0) {
            if (player.getHealth() >= (healthThreshold * player.getMaxHealth())) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                        frequency + 5, weaknessAmplifier, false, false, true));
            }
        }
    }

    public static void passiveBerserkerChallenge(Player player) {
        int frequency = SimplySkills.berserkerConfig.passiveBerserkerChallengeFrequency;
        int radius = SimplySkills.berserkerConfig.passiveBerserkerChallengeRadius;
        int count = 0;
        int countMax = SimplySkills.berserkerConfig.passiveBerserkerChallengeMaxAmplifier;
        if (player.tickCount % frequency == 0) {

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                        count++;
                    }
                }
            }
            if (count > countMax)
                count = countMax;
            if (count > 1)
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, frequency + 5, count -1, false, false, true));
        }
    }

    public static void passiveBerserkerExploit(Entity target) {
        if (target instanceof LivingEntity livingTarget) {
            if (livingTarget.hasEffect(EffectRegistry.IMMOBILIZE)) {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1, false, false, true));
            }
        }
    }


    //------- SIGNATURE ABILITIES --------


    // Rampage
    public static boolean signatureBerserkerRampage(String berserkerSkillTree, Player player) {

        int rampageDuration = SimplySkills.berserkerConfig.signatureBerserkerRampageDuration;
        int bullrushDuration = SimplySkills.berserkerConfig.signatureBerserkerBullrushDuration;
        player.addEffect(new MobEffectInstance(EffectRegistry.RAMPAGE, rampageDuration, 0, false, false, true));
        if (HelperMethods.isUnlocked(berserkerSkillTree,
                SkillReferencePosition.berserkerSpecialisationRampageCharge, player)) {
            player.addEffect(new MobEffectInstance(EffectRegistry.BULLRUSH, bullrushDuration, 0 , false, false, true));
            player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT15,
                    SoundSource.PLAYERS, 0.5f, 1.1f);
        }
        return true;
    }

    // Bloodthirsty
    public static boolean signatureBerserkerBloodthirsty(String berserkerSkillTree, Player player) {
        int bloodthirstyDuration = SimplySkills.berserkerConfig.signatureBerserkerBloodthirstyDuration;
        int bloodthirstyMightyStacks = SimplySkills.berserkerConfig.signatureBerserkerBloodthirstyMightyStacks;
        player.addEffect(new MobEffectInstance(EffectRegistry.BLOODTHIRSTY, bloodthirstyDuration, 0, false, false, true));
        if (HelperMethods.isUnlocked(berserkerSkillTree,
                SkillReferencePosition.berserkerSpecialisationBloodthirstyMighty, player))
            HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, bloodthirstyDuration,
                    bloodthirstyMightyStacks, 5);
        return true;
    }

    // Berserking
    public static boolean signatureBerserkerBerserking(String berserkerSkillTree, Player player) {
        double sacrificeAmountModifier = SimplySkills.berserkerConfig.signatureBerserkerBerserkingSacrificeAmount;
        int secondsPerSacrifice = SimplySkills.berserkerConfig.signatureBerserkerBerserkingSecondsPerSacrifice;
        int leapSlamDuration = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamDuration;
        float sacrificeAmount = (float) (player.getHealth() * sacrificeAmountModifier);
        if (!ModList.get().isLoaded("simplyswords") || !SimplySwordsGemEffects.doSignatureGemEffects(player, "accelerant")) {
            player.hurt(player.damageSources().generic(), sacrificeAmount);
            player.addEffect(new MobEffectInstance(EffectRegistry.BERSERKING,
                    (int) ((sacrificeAmount * secondsPerSacrifice) * 20), 0, false, false, true));
        }
        if (HelperMethods.isUnlocked(berserkerSkillTree,
                SkillReferencePosition.berserkerSpecialisationBerserkingLeap, player)) {
            player.addEffect(new MobEffectInstance(EffectRegistry.LEAPSLAM, leapSlamDuration, 0 , false, false, true));
            player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT15,
                    SoundSource.PLAYERS, 0.5f, 1.1f);
        }
        return true;
    }



}
