package net.sweenus.simplyskills.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.paladins.effect.PaladinEffects;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.List;
import java.util.Random;

public class CrusaderAbilities {

    // Retribution
    public static void passiveCrusaderRetribution(Player player, LivingEntity attacker) {
        int random = new Random().nextInt(100);
        int retributionChance = SimplySkills.crusaderConfig.passiveCrusaderRetributionChance;
        if (random < retributionChance)
            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:paladins_judgement", 32, attacker, null);
    }


    //Exhaustive Recovery
    public static void passiveCrusaderExhaustiveRecovery(Player player, LivingEntity attacker) {
        int random = new Random().nextInt(100);
        int recoveryChance = SimplySkills.crusaderConfig.passiveCrusaderExhaustiveRecoveryChance;
        int exhaustStacks = SimplySkills.crusaderConfig.passiveCrusaderExhaustiveRecoveryExhaustionStacks;
        if (random < recoveryChance) {
            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:paladins_flash_heal", 32, player, null);
            HelperMethods.incrementStatusEffect(player, EffectRegistry.EXHAUSTION, 300, exhaustStacks, 99);
        }
    }

    //Aegis
    public static void passiveCrusaderAegis(Player player) {
        int frequency = SimplySkills.crusaderConfig.passiveCrusaderAegisFrequency;
        int stacksRemoved = SimplySkills.crusaderConfig.passiveCrusaderAegisStacksRemoved;
        if (player.hasEffect(EffectRegistry.EXHAUSTION)) {
            int exhaustionStacks = player.getEffect(EffectRegistry.EXHAUSTION).getAmplifier() + 1;
            if (player.tickCount % frequency == 0 && exhaustionStacks >= stacksRemoved) {
                HelperMethods.incrementStatusEffect(player, PaladinEffects.DIVINE_PROTECTION.entry, 200, 1, 5);
                HelperMethods.decrementStatusEffects(player, EffectRegistry.EXHAUSTION, stacksRemoved);
            }
        }
    }


    //------- SIGNATURE ABILITIES --------

    // Heavensmith's Call
    public static boolean signatureHeavensmithsCall(String crusaderSkillTree, Player player) {
        BlockPos blockpos = null;
        Entity target = null;
        boolean success = false;
        int heavensmithsCallRange = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallRange;
        int duration = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDADuration;

        if (HelperMethods.getTargetedEntity(player, heavensmithsCallRange) != null)
            blockpos = HelperMethods.getTargetedEntity(player, heavensmithsCallRange).blockPosition();

        if (blockpos == null)
            blockpos = HelperMethods.getBlockLookingAt(player, heavensmithsCallRange);

        if (blockpos != null) {

            if ((target instanceof LivingEntity le) && !HelperMethods.checkFriendlyFire(le, player))
                target = null;

            if (HelperMethods.isUnlocked(crusaderSkillTree,
                        SkillReferencePosition.crusaderSpecialisationDivineAdjudication, player))
                    player.addEffect(new MobEffectInstance(EffectRegistry.DIVINEADJUDICATION, duration, 0, false, false, true));

            SignatureAbilities.castSpellEngineIndirectTarget(player,
                    "simplyskills:physical_heavensmiths_call",
                    heavensmithsCallRange, target, blockpos);
            success = true;
        }
        return success;
    }

    public static void signatureHeavensmithsCallImpact(String crusaderSkillTree, List<Entity> targets,
                                                       ResourceLocation spellId, Player player) {
        int tauntDuration = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallTauntMarkDuration;
        if (spellId != null && spellId.toString().equals("simplyskills:physical_heavensmiths_call")) {
        Entity target = targets.get(0);
        AABB box = HelperMethods.createBox(target, 3);
            for (Entity entities : target.level().getEntities(target, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities instanceof LivingEntity le && HelperMethods.checkFriendlyFireAOE(le, player)) {
                    if (HelperMethods.isUnlocked(crusaderSkillTree, SkillReferencePosition.crusaderSpecialisationHeavensmithsCallMark, player)) {
                        le.addEffect(new MobEffectInstance(EffectRegistry.DEATHMARK, tauntDuration));
                    }

                    if ((le instanceof Mob me) && HelperMethods.isUnlocked(crusaderSkillTree, SkillReferencePosition.crusaderSpecialisationHeavensmithsCallTaunt, player)) {
                        SimplyStatusEffectInstance tauntEffect = new SimplyStatusEffectInstance(
                                EffectRegistry.TAUNTED, tauntDuration, 0, false,
                                false, true);
                        tauntEffect.setSourceEntity(player);
                        me.addEffect(tauntEffect);
                    }
                }
            }
        }
    }

    // Sacred Onslaught
    public static boolean signatureCrusaderSacredOnslaught(String crusaderSkillTree, Player player) {

        int divineProtectionDuration = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtDPDuration;
        int dashDuration = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtDashDuration;

        player.addEffect(new MobEffectInstance(EffectRegistry.SACREDONSLAUGHT, dashDuration, 0, false, false, true));

        if (HelperMethods.isUnlocked(crusaderSkillTree,
                SkillReferencePosition.crusaderSpecialisationSacredOnslaughtDefend, player)) {
            player.addEffect(new MobEffectInstance(PaladinEffects.DIVINE_PROTECTION.entry, divineProtectionDuration, 0 , false, false, true));
            player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT15,
                    SoundSource.PLAYERS, 0.5f, 1.1f);
        }
        if (HelperMethods.isUnlocked(crusaderSkillTree,
                SkillReferencePosition.crusaderSpecialisationSacredOnslaughtMighty, player)) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, divineProtectionDuration, 3, 5);
        }
        SignatureAbilities.playCastingGesture(player, "spell_engine:one_handed_projectile_release");
        return true;
    }

    // Consecration
    public static boolean signatureCrusaderConsecration(String crusaderSkillTree, Player player) {

        int consecrationExtendDuration = SimplySkills.crusaderConfig.signatureCrusaderConsecrationExtendDuration;
        int consecrationDuration = SimplySkills.crusaderConfig.signatureCrusaderConsecrationDuration;

        if (HelperMethods.isUnlocked(crusaderSkillTree, SkillReferencePosition.crusaderSpecialisationConsecrationDuration, player))
            consecrationDuration = SimplySkills.crusaderConfig.signatureCrusaderConsecrationDuration + consecrationExtendDuration;

        player.addEffect(new MobEffectInstance(EffectRegistry.CONSECRATION, consecrationDuration, 0 , false, false, true));

        SignatureAbilities.playCastingGesture(player, "spell_engine:dual_handed_ground_release");
        return true;
    }

















    // ------- EFFECTS --------

    // Heavensmith's Call - Divine Adjudication
    public static void effectDivineAdjudication(Player player) {
        int frequency = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDAFrequency;

        if (HelperMethods.isUnlocked("simplyskills:crusader",
                SkillReferencePosition.crusaderSpecialisationHeavensmithsCall, player) &&
                player.hasEffect(EffectRegistry.DIVINEADJUDICATION) && player.tickCount % frequency == 0) {
            int chance = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDAChance;
            int radius = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDARadius;
            int exhaustStacksRemoved = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDAExhaustStacks;
            int mightDuration = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDAMightDuration;
            int mightStacksMax = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallDAMightStacksMax;
            String spellIdentifier = "simplyskills:paladins_judgement";


            if (SignatureAbilities.castSpellEngineAOE(player, spellIdentifier, radius, chance, true, false)) {
                if (HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationHeavensmithsCallExhaust, player)) {
                    HelperMethods.decrementStatusEffects(player, EffectRegistry.EXHAUSTION, exhaustStacksRemoved);
                }
                if (HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationHeavensmithsCallMighty, player))
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, mightDuration, 1, mightStacksMax);
            }
        }

    }



}
