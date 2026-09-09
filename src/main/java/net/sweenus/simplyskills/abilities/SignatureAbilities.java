package net.sweenus.simplyskills.abilities;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.Platform;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.internals.SpellExecution;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCaster;
import net.spell_engine.internals.delivery.SpellDelivery;
import net.spell_engine.internals.target.SpellIntents;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.network.CooldownPacket;
import net.sweenus.simplyskills.network.KeybindPacket;
import net.sweenus.simplyskills.network.ModPacketHandler;
import net.sweenus.simplyskills.registry.EntityRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SignatureAbilities {

    private static void performSpell(Player player, ResourceLocation spellId, List<Entity> targets,
                                     SpellCast.Action action, float progress) {
        SpellRegistry.from(player.level()).getHolder(spellId).ifPresent(spell -> {
            SpellExecution.ImpactContext impactContext = new SpellExecution.ImpactContext(
                    1, 1, null,
                    SpellPower.getSpellPower(spell.value().school, player),
                    SpellIntents.focusMode(spell.value()), 0);
            SpellDelivery.resolveAndDeliver(player.level(), player, spell,
                    new SpellTarget.SearchResult(targets, null), impactContext, completion -> {
                        // Direct delivery bypasses SpellExecution's release-animation callback.
                        if (completion.success() && action == SpellCast.Action.RELEASE
                                && spell.value().release != null && !player.level().isClientSide()) {
                            AnimationHelper.sendAnimation(player, Platform.tracking(player),
                                    SpellCast.Animation.RELEASE, spell.value().release.animation, 1.0F);
                        }
                    });
            AbilityLogic.onSpellCastEffects(player, targets, spellId, null);
        });
    }

    /** Release gesture for direct-effect abilities, called once on successful activation. */
    public static void playCastingGesture(Player player, String animationId) {
        if (!player.level().isClientSide()) {
            AnimationHelper.sendAnimation(player, Platform.tracking(player), SpellCast.Animation.RELEASE,
                    PlayerAnimation.of(animationId), 1.0F);
        }
    }

    public static void signatureAbilityManager(Player player, String abilityType) {

        String wizardSkillTree = "simplyskills:wizard";
        String berserkerSkillTree = "simplyskills:berserker";
        String rogueSkillTree = "simplyskills:rogue";
        String rangerSkillTree = "simplyskills:ranger";
        String spellbladeSkillTree = "simplyskills:spellblade";
        String crusaderSkillTree = "simplyskills:crusader";
        String clericSkillTree = "simplyskills:cleric";
        String necromancerSkillTree = "simplyskills:necromancer";
        String baseTree = "simplyskills:tree";
        String ascendancyTree = "simplyskills:ascendancy";
        boolean ability_success = false;
        String ability = "none";

        if (ModList.get().isLoaded("prominent"))
            ascendancyTree = "puffish_skills:prom";



        // - WIZARD -
        if (abilityType.contains("signature")) {
            if (HelperMethods.isUnlocked(wizardSkillTree, null, player)) {

                // Meteor Shower
                if (HelperMethods.isUnlocked(wizardSkillTree,
                        SkillReferencePosition.wizardSpecialisationMeteorShower, player)) {
                    ability_success = WizardAbilities.signatureWizardMeteorShower(wizardSkillTree, player);
                    ability = "MeteorShower";
                }
                // Ice Comet
                if (HelperMethods.isUnlocked(wizardSkillTree,
                        SkillReferencePosition.wizardSpecialisationIceComet, player)) {
                    ability_success = WizardAbilities.signatureWizardIceComet(wizardSkillTree, player);
                    ability = "IceComet";
                }
                // Static Discharge
                if (HelperMethods.isUnlocked(wizardSkillTree,
                        SkillReferencePosition.wizardSpecialisationStaticDischarge, player)) {
                    ability_success = WizardAbilities.signatureWizardStaticDischarge(wizardSkillTree, player);
                    ability = "StaticDischarge";
                }
                // Arcane Bolt
                if (HelperMethods.isUnlocked(wizardSkillTree,
                        SkillReferencePosition.wizardSpecialisationArcaneBolt, player)) {
                    ability_success = WizardAbilities.signatureWizardArcaneBolt(wizardSkillTree, player);
                    ability = "ArcaneBolt";
                }
            }

            // - BERSERKER -
            if (HelperMethods.isUnlocked(berserkerSkillTree, null, player)) {

                // Rampage
                if (HelperMethods.isUnlocked(berserkerSkillTree,
                        SkillReferencePosition.berserkerSpecialisationRampage, player)) {
                    ability_success = BerserkerAbilities.signatureBerserkerRampage(berserkerSkillTree, player);
                    ability = "Rampage";
                }
                // Bloodthirsty
                if (HelperMethods.isUnlocked(berserkerSkillTree,
                        SkillReferencePosition.berserkerSpecialisationBloodthirsty, player)) {
                    ability_success = BerserkerAbilities.signatureBerserkerBloodthirsty(berserkerSkillTree, player);
                    ability = "Bloodthirsty";
                }
                //Berserking
                if (HelperMethods.isUnlocked(berserkerSkillTree,
                        SkillReferencePosition.berserkerSpecialisationBerserking, player)) {
                    ability_success = BerserkerAbilities.signatureBerserkerBerserking(berserkerSkillTree, player);
                    ability = "Berserking";
                }
            }

            // - ROGUE -
            if (HelperMethods.isUnlocked(rogueSkillTree, null, player)) {

                // Evasion
                if (HelperMethods.isUnlocked(rogueSkillTree,
                        SkillReferencePosition.rogueSpecialisationEvasion, player)) {
                    ability_success = RogueAbilities.signatureRogueEvasion(rogueSkillTree, player);
                    ability = "Evasion";
                }
                // Preparation
                if (HelperMethods.isUnlocked(rogueSkillTree,
                        SkillReferencePosition.rogueSpecialisationPreparation, player)) {
                    ability_success = RogueAbilities.signatureRoguePreparation(rogueSkillTree, player);
                    ability = "Preparation";
                }
                // Siphoning Strikes
                if (HelperMethods.isUnlocked(rogueSkillTree,
                        SkillReferencePosition.rogueSpecialisationSiphoningStrikes, player)) {
                    ability_success = RogueAbilities.signatureRogueSiphoningStrikes(rogueSkillTree, player);
                    ability = "SiphoningStrikes";
                }
            }

            // - Ranger -
            if (HelperMethods.isUnlocked(rangerSkillTree, null, player)) {

                // Elemental Arrows
                if (HelperMethods.isUnlocked(rangerSkillTree,
                        SkillReferencePosition.rangerSpecialisationElementalArrows, player)) {
                    ability_success = RangerAbilities.signatureRangerElementalArrows(rangerSkillTree, player);
                    ability = "ElementalArrows";
                }
                // Disengage
                if (HelperMethods.isUnlocked(rangerSkillTree,
                        SkillReferencePosition.rangerSpecialisationDisengage, player)) {
                    ability_success = RangerAbilities.signatureRangerDisengage(rangerSkillTree, player);
                    ability = "Disengage";
                }
                // Arrow Rain
                if (HelperMethods.isUnlocked(rangerSkillTree,
                        SkillReferencePosition.rangerSpecialisationArrowRain, player)) {
                    ability_success = RangerAbilities.signatureRangerArrowRain(rangerSkillTree, player);
                    ability = "ArrowRain";
                }
            }

            // - Spellblade -
            if (HelperMethods.isUnlocked(spellbladeSkillTree, null, player)) {

                // Elemental Surge
                if (HelperMethods.isUnlocked(spellbladeSkillTree,
                        SkillReferencePosition.spellbladeSpecialisationElementalSurge, player)) {
                    ability_success = SpellbladeAbilities.signatureSpellbladeElementalSurge(spellbladeSkillTree, player);
                    ability = "ElementalSurge";
                }
                // Elemental Impact
                if (HelperMethods.isUnlocked(spellbladeSkillTree,
                        SkillReferencePosition.spellbladeSpecialisationElementalImpact, player)) {
                    ability_success = SpellbladeAbilities.signatureSpellbladeElementalImpact(spellbladeSkillTree, player);
                    ability = "ElementalImpact";
                }
                if (HelperMethods.isUnlocked(spellbladeSkillTree,
                        SkillReferencePosition.spellbladeSpecialisationSpellweaver, player)) {
                    //Spell Weaver
                    ability_success = SpellbladeAbilities.signatureSpellbladeSpellweaver(spellbladeSkillTree, player);
                    ability = "Spellweaver";
                }
            }

            // - Crusader -
            if (HelperMethods.isUnlocked(crusaderSkillTree, null, player)
                    && ModList.get().isLoaded("paladins")) {

                // Heavensmith's Call
                if (HelperMethods.isUnlocked(crusaderSkillTree,
                        SkillReferencePosition.crusaderSpecialisationHeavensmithsCall, player)) {
                    ability_success = CrusaderAbilities.signatureHeavensmithsCall(crusaderSkillTree, player);
                    ability = "HeavensmithsCall";
                }
                // Sacred Onslaught
                if (HelperMethods.isUnlocked(crusaderSkillTree,
                        SkillReferencePosition.crusaderSpecialisationSacredOnslaught, player)) {
                    ability_success = CrusaderAbilities.signatureCrusaderSacredOnslaught(crusaderSkillTree, player);
                    ability = "SacredOnslaught";
                }
                // Consecration
                if (HelperMethods.isUnlocked(crusaderSkillTree,
                        SkillReferencePosition.crusaderSpecialisationConsecration, player)) {
                    ability_success = CrusaderAbilities.signatureCrusaderConsecration(crusaderSkillTree, player);
                    ability = "Consecration";
                }
            }

            // - Cleric -
            if (HelperMethods.isUnlocked(clericSkillTree, null, player)
                    && ModList.get().isLoaded("paladins")) {

                // Divine Intervention
                if (HelperMethods.isUnlocked(clericSkillTree,
                        SkillReferencePosition.clericSpecialisationDivineIntervention, player)) {
                    ability_success = ClericAbilities.signatureClericDivineIntervention(clericSkillTree, player);
                    ability = "DivineIntervention";
                }
                // Sacred Orb
                if (HelperMethods.isUnlocked(clericSkillTree,
                        SkillReferencePosition.clericSpecialisationSacredOrb, player)) {
                    ability_success = ClericAbilities.signatureClericSacredOrb(clericSkillTree, player);
                    ability = "SacredOrb";
                }
                // Anoint Weapon
                if (HelperMethods.isUnlocked(clericSkillTree,
                        SkillReferencePosition.clericSpecialisationAnointWeapon, player)) {
                    ability_success = ClericAbilities.signatureClericAnointWeapon(player);
                    ability = "AnointWeapon";
                }
            }
            // - Necromancer -
            if (HelperMethods.isUnlocked(necromancerSkillTree, null, player)) {

                // Divine Intervention
                if (HelperMethods.isUnlocked(necromancerSkillTree,
                        SkillReferencePosition.necromancerSpecialisationSummoningRitual, player)) {
                    ability_success = NecromancerAbilities.signatureNecromancerSummoningRitual(necromancerSkillTree, player);
                    ability = "SummoningRitual";
                }
            }
        }
        else if (abilityType.contains("ascendancy")) {
            // --- ASCENDANCY ---
            if (HelperMethods.isUnlocked(ascendancyTree, null, player)) {

                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyRighteousHammers, player)) {
                    ability_success = AscendancyAbilities.righteousHammers(player);
                    ability = "RighteousHammers";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyBoneArmor, player)) {
                    if (ModList.get().isLoaded("prominent"))
                        ability_success = ProminenceAbilities.boneArmor(player);
                    else ability_success = AscendancyAbilities.boneArmor(player);
                    ability = "BoneArmor";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyCyclonicCleave, player)) {
                    ability_success = AscendancyAbilities.cyclonicCleave(player);
                    ability = "CyclonicCleave";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyMagicCircle, player)) {
                    ability_success = AscendancyAbilities.magicCircle(player);
                    ability = "MagicCircle";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyArcaneSlash, player)) {
                    ability_success = AscendancyAbilities.arcaneSlash(player);
                    ability = "ArcaneSlash";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyAgony, player)) {
                    ability_success = AscendancyAbilities.agony(player);
                    ability = "Agony";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyTorment, player)) {
                    ability_success = AscendancyAbilities.torment(player);
                    ability = "Torment";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyRapidfire, player)) {
                    ability_success = AscendancyAbilities.rapidfire(player);
                    ability = "Rapidfire";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyCataclysm, player)) {
                    ability_success = AscendancyAbilities.cataclysm(player);
                    ability = "Cataclysm";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyGhostwalk, player)) {
                    ability_success = AscendancyAbilities.ghostwalk(player);
                    ability = "Ghostwalk";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancySkywardSunder, player)) {
                    ability_success = AscendancyAbilities.skywardSunder(player);
                    ability = "SkywardSunder";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyRighteousShield, player)) {
                    ability_success = AscendancyAbilities.righteousShield(player);
                    ability = "RighteousShield";
                }
                if (HelperMethods.isUnlocked(ascendancyTree,
                        SkillReferencePosition.ascendancyChainbreaker, player)) {
                    if (ascendancyTree.equals("puffish_skills:prom")) {
                        ability_success = ProminenceAbilities.promDissonance(player);
                    }
                    else {ability_success = AscendancyAbilities.chainbreaker(player);}
                    ability = "Chainbreaker";
                }
            }
        }

        // Trigger bonus gem effects
        if (ability_success && ModList.get().isLoaded("simplyswords"))
            SimplySwordsGemEffects.doGenericAbilityGemEffects(player);


        //Return cooldown to client
        if (!player.level().isClientSide) {
            SignatureAbilities.signatureAbilityCooldownManager(ability, ability_success, player);
            //System.out.println("Using ability: " + ability);
        }


    }

    // COOLDOWN MANAGEMENT

    public static void signatureAbilityCooldownManager(String ability, boolean useSuccess, Player player) {
        float spellHasteCDReduce = SimplySkills.generalConfig.spellHasteCooldownReductionModifier;
        int minimumCD = SimplySkills.generalConfig.minimumAchievableCooldown * 1000;
        int useDelay = (int) SimplySkills.generalConfig.minimumTimeBetweenAbilityUse * 1000;
        int cooldown = 500;
        double sendCooldown;
        String type = "";
        String cooldownType = "none";

        switch (ability) {
            case "ArcaneBolt" -> {
                cooldown = SimplySkills.wizardConfig.signatureWizardArcaneBoltCooldown * 1000;
                type = "magic, arcane";
                cooldownType = "signature";
            }
            case "IceComet" -> {
                cooldown = SimplySkills.wizardConfig.signatureWizardIceCometCooldown * 1000;
                type = "magic, elemental, debuff";
                cooldownType = "signature";
            }
            case "MeteorShower" -> {
                cooldown = SimplySkills.wizardConfig.signatureWizardMeteorShowerCooldown * 1000;
                type = "magic, elemental";
                cooldownType = "signature";
            }
            case "StaticDischarge" -> {
                cooldown = SimplySkills.wizardConfig.signatureWizardStaticDischargeCooldown * 1000;
                type = "magic, elemental, debuff";
                cooldownType = "signature";
            }
            case "Berserking" -> {
                cooldown = SimplySkills.berserkerConfig.signatureBerserkerBerserkingCooldown * 1000;
                type = "physical, melee, buff, sacrificial";
                cooldownType = "signature";
            }
            case "Bloodthirsty" -> {
                cooldown = SimplySkills.berserkerConfig.signatureBerserkerBloodthirstyCooldown * 1000;
                type = "physical, melee, buff, recovery";
                cooldownType = "signature";
            }
            case "Rampage" -> {
                cooldown = SimplySkills.berserkerConfig.signatureBerserkerRampageCooldown * 1000;
                type = "physical, melee, buff";
                cooldownType = "signature";
            }
            case "SiphoningStrikes" -> {
                cooldown = SimplySkills.rogueConfig.signatureRogueSiphoningStrikesCooldown * 1000;
                type = "physical, melee, buff, debuff, recovery";
                cooldownType = "signature";
            }
            case "Evasion" -> {
                cooldown = SimplySkills.rogueConfig.signatureRogueEvasionCooldown * 1000;
                type = "physical, buff";
                cooldownType = "signature";
            }
            case "Preparation" -> {
                cooldown = SimplySkills.rogueConfig.signatureRoguePreparationCooldown * 1000;
                type = "physical, buff";
                cooldownType = "signature";
            }
            case "ArrowRain" -> {
                cooldown = SimplySkills.rangerConfig.effectRangerArrowRainCooldown * 1000;
                type = "physical, arrow, buff";
                cooldownType = "signature";
            }
            case "Disengage" -> {
                cooldown = SimplySkills.rangerConfig.signatureRangerDisengageCooldown * 1000;
                type = "physical, debuff";
                cooldownType = "signature";
            }
            case "ElementalArrows" -> {
                cooldown = SimplySkills.rangerConfig.effectRangerElementalArrowsCooldown * 1000;
                type = "magic, arrow, elemental, buff";
                cooldownType = "signature";
            }
            case "ElementalImpact" -> {
                cooldown = SimplySkills.spellbladeConfig.signatureSpellbladeElementalImpactCooldown * 1000;
                type = "magic, melee, charge, elemental";
                cooldownType = "signature";
            }
            case "ElementalSurge" -> {
                cooldown = SimplySkills.spellbladeConfig.signatureSpellbladeElementalSurgeCooldown * 1000;
                type = "magic, elemental, buff";
                cooldownType = "signature";
            }
            case "Spellweaver" -> {
                cooldown = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverCooldown * 1000;
                type = "magic, buff, elemental, melee";
                cooldownType = "signature";
            }
            case "HeavensmithsCall" -> {
                cooldown = SimplySkills.crusaderConfig.signatureCrusaderHeavensmithsCallCooldown * 1000;
                type = "physical, debuff";
                cooldownType = "signature";
            }
            case "SacredOnslaught" -> {
                cooldown = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtCooldown * 1000;
                type = "physical, melee, charge, buff, recovery";
                cooldownType = "signature";
            }
            case "Consecration" -> {
                cooldown = SimplySkills.crusaderConfig.signatureCrusaderConsecrationCooldown * 1000;
                type = "magic, buff, recovery";
                cooldownType = "signature";
            }
            case "DivineIntervention" -> {
                cooldown = SimplySkills.clericConfig.signatureClericDivineInterventionCooldown * 1000;
                type = "magic, healing, buff";
                cooldownType = "signature";
            }
            case "SacredOrb" -> {
                cooldown = SimplySkills.clericConfig.signatureClericSacredOrbCooldown * 1000;
                type = "magic, healing, buff";
                cooldownType = "signature";
            }
            case "AnointWeapon" -> {
                cooldown = SimplySkills.clericConfig.signatureClericAnointWeaponCooldown * 1000;
                type = "magic, healing, buff";
                cooldownType = "signature";
            }
            case "SummoningRitual" -> {
                cooldown = SimplySkills.necromancerConfig.signatureNecromancerSummoningRitualCooldown * 1000;
                type = "magic, minion";
                cooldownType = "signature";
            }
            case "RighteousHammers" -> {
                cooldown = 60 * 1000;
                type = "physical, buff";
                cooldownType = "ascendancy";
            }
            case "BoneArmor" -> {
                if (ModList.get().isLoaded("prominent"))
                    cooldown = 40 * 1000;
                else cooldown = 70 * 1000;
                type = "physical, buff, recovery";
                cooldownType = "ascendancy";
            }
            case "CyclonicCleave" -> {
                cooldown = 15 * 1000;
                type = "physical, magic, melee, channel";
                cooldownType = "ascendancy";
            }
            case "MagicCircle" -> {
                cooldown = 40 * 1000;
                type = "buff, magic";
                cooldownType = "ascendancy";
            }
            case "ArcaneSlash" -> {
                cooldown = 10 * 1000;
                type = "magic, melee,";
                cooldownType = "ascendancy";
            }
            case "Agony" -> {
                cooldown = 40 * 1000;
                type = "magic, debuff, healing";
                cooldownType = "ascendancy";
            }
            case "Torment" -> {
                cooldown = 40 * 1000;
                type = "magic, debuff";
                cooldownType = "ascendancy";
            }
            case "Rapidfire" -> {
                cooldown = 30 * 1000;
                type = "physical, arrow, channel";
                cooldownType = "ascendancy";
            }
            case "Cataclysm" -> {
                cooldown = 60 * 1000;
                type = "magic, channel, elemental";
                cooldownType = "ascendancy";
            }
            case "Ghostwalk" -> {
                cooldown = 30 * 1000;
                type = "magic, channel, soul";
                cooldownType = "ascendancy";
            }
            case "SkywardSunder" -> {
                cooldown = 16 * 1000;
                type = "melee, channel, buff, physical, magic";
                cooldownType = "ascendancy";
            }
            case "RighteousShield" -> {
                cooldown = 6 * 1000;
                type = "physical, magic, buff";
                cooldownType = "ascendancy";
            }
            case "Chainbreaker" -> {
                cooldown = 45 * 1000;
                type = "physical, magic, buff";
                cooldownType = "ascendancy";
            }
        }

        // Do Gem Effects
        if (ModList.get().isLoaded("simplyswords")) {
            cooldown = SimplySwordsGemEffects.renewed(player, cooldown, minimumCD);
            cooldown = SimplySwordsGemEffects.accelerant(player, cooldown, minimumCD);
        }


        // Calculations
        double spellHaste = SpellPower.getHaste(player, SpellSchools.ARCANE);
        // Spell Power returns a multiplier: 1.0 is normal haste, not a bonus.
        sendCooldown = cooldown - (Math.max(0.0, spellHaste - 1.0) * (2000 * spellHasteCDReduce));

        if (sendCooldown < (minimumCD) && useSuccess) sendCooldown = minimumCD;
        if (!useSuccess) sendCooldown = useDelay;

        //System.out.println("Ability type: " + type);
        //System.out.println(cooldownType);
        //System.out.println(cooldown);
        //System.out.println(sendCooldown);
        sendCooldownPacket((ServerPlayer) player, (int) sendCooldown, cooldownType);
    }



    // -- SPELL CASTING --

    public static void castSpellEngineDumbFire(Player player, String spellIdentifier) {

        // -- Cast spell at a target we are looking at --

        //Entity target = HelperMethods.getTargetedEntity(player, range);
        SpellCast.Action action = SpellCast.Action.RELEASE;
        ResourceLocation spellID      = ResourceLocation.parse(spellIdentifier);
        List<Entity> list       = new ArrayList<Entity>();
        //list.add(target);

        performSpell(player, spellID, list, action, 20);
    }

    public static void castSpellEngine(Player player, String spellIdentifier) {

        ItemStack itemStack     = player.getMainHandItem();
        ResourceLocation spellID      = ResourceLocation.parse(spellIdentifier);

        ((SpellCaster.Player) player).getInteractor().requestCast(spellID, SpellCast.TargetSnapshot.EMPTY);
    }

    public static void castSpellEngineIndirectTarget(Player player, String spellIdentifier, int range,@Nullable Entity target,@Nullable BlockPos blockpos) {
        if (target == null && blockpos != null) {
            target = EntityRegistry.SPELL_TARGET_ENTITY.spawn( (ServerLevel) player.level(),
                    blockpos,
                    MobSpawnType.TRIGGERED);
        } else if (target == null && blockpos == null) {
            blockpos = HelperMethods.getBlockLookingAt(player, range);
            if (blockpos != null) {
                target = EntityRegistry.SPELL_TARGET_ENTITY.spawn((ServerLevel) player.level(),
                        blockpos,
                        MobSpawnType.TRIGGERED);
            }
        }

        // -- Cast spell at specified target --
        if (target != null) {
            ItemStack itemStack     = player.getMainHandItem();
            InteractionHand hand               = player.getUsedItemHand();
            SpellCast.Action action = SpellCast.Action.RELEASE;
            ResourceLocation spellID      = ResourceLocation.parse(spellIdentifier);
            List<Entity> list       = new ArrayList<Entity>();
            list.add(target);

            performSpell(player, spellID, list, action, 1);
        }
    }

    public static void castSpellEngineIndirectTargets(Player player, String spellIdentifier, List<Entity> targets) {
        if (!targets.isEmpty()) {
            ResourceLocation spellID = ResourceLocation.parse(spellIdentifier);
            performSpell(player, spellID, targets, SpellCast.Action.RELEASE, 1);
        }
    }

    public static boolean castSpellEngineAOE(Player player, String spellIdentifier, int radius, int chance, boolean singleTarget, boolean ignorePassive) {

        // -- Cast spell at nearby targets --

        ItemStack itemStack     = player.getMainHandItem();
        InteractionHand hand               = player.getUsedItemHand();
        SpellCast.Action action = SpellCast.Action.RELEASE;
        ResourceLocation spellID      = ResourceLocation.parse(spellIdentifier);
        List<Entity> list       = new ArrayList<Entity>();


        AABB box = HelperMethods.createBox(player, radius);
        for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
            if (entities != null) {
                if (entities instanceof AgeableMob && ignorePassive)
                    continue; // Skip passive entities if ignorePassive is true
                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {

                    if (player.getRandom().nextInt(100) < chance)
                        list.add(le);
                    if (singleTarget)
                        break; // Stop after adding one target if singleTarget is true

                }
            }
        }

        if (!list.isEmpty()) {
            performSpell(player, spellID, list, action, 20);

            return true;
        }
        return false;
    }



    @OnlyIn(Dist.CLIENT)
    public static void sendKeybindPacket(String type) {
        PacketDistributor.sendToServer(new KeybindPacket(type));

    }

    public static void sendCooldownPacket(ServerPlayer player, int cooldown, String cooldownType) {
        ModPacketHandler.sendCooldown(player, cooldown, cooldownType);

    }


}
