package net.sweenus.simplyskills.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.entities.SimplySkillsArrowEntity;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.EntityRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class AbilityEffects {

    private static final Map<UUID, ArrowRainVolley> pendingArrowRainVolleys = new HashMap<>();

    private static class ArrowRainVolley {
        private final Vec3 position;
        private final int radius;
        private final int density;
        private final int volleys;
        private final boolean elemental;
        private final int projectileLimit;
        private final List<List<BlockPos>> normalArrowVolleys = new ArrayList<>();
        private int remainingVolleys;
        private int elementalProjectiles;
        private int nextVolleyTick;

        private ArrowRainVolley(Vec3 position, int radius, int density, int volleys,
                                boolean elemental, int projectileLimit, int nextVolleyTick) {
            this.position = position;
            this.radius = radius;
            this.density = density;
            this.volleys = volleys;
            this.elemental = elemental;
            this.projectileLimit = projectileLimit;
            this.remainingVolleys = volleys;
            this.nextVolleyTick = nextVolleyTick;
        }
    }


    public static void effectBerserkerBerserking(Entity target, Player player) {

        if ((target instanceof LivingEntity livingTarget) && player.hasEffect(EffectRegistry.BERSERKING)) {
            int berserkingSubEffectDuration = SimplySkills.berserkerConfig.signatureBerserkerBerserkingSubEffectDuration;
            int berserkingSubEffectMaxAmplifier = SimplySkills.berserkerConfig.signatureBerserkerBerserkingSubEffectMaxAmplifier;
            HelperMethods.incrementStatusEffect(player, MobEffects.DIG_SPEED, berserkingSubEffectDuration,
                    1, berserkingSubEffectMaxAmplifier);
            HelperMethods.incrementStatusEffect(player, MobEffects.DAMAGE_BOOST, berserkingSubEffectDuration,
                    1, berserkingSubEffectMaxAmplifier);
            HelperMethods.incrementStatusEffect(player, MobEffects.MOVEMENT_SPEED, berserkingSubEffectDuration,
                    1, berserkingSubEffectMaxAmplifier);
        }
    }

    public static void effectBerserkerBloodthirsty(Player player) {

        if (player.hasEffect(EffectRegistry.BLOODTHIRSTY)) {
            float bloodthirstyHealPercent = SimplySkills.berserkerConfig.signatureBerserkerBloodthirstyHealPercent;
            float healAmount = player.getMaxHealth() * bloodthirstyHealPercent;
            player.heal(healAmount);
        }
    }

    public static void effectBerserkerBloodthirstyTireless(Player player) {

        if (player.hasEffect(EffectRegistry.BLOODTHIRSTY)) {
            int bloodthirstyTirelessChance = SimplySkills.berserkerConfig.signatureBerserkerBloodthirstyTirelessChance;
            if (player.getRandom().nextInt(100) < bloodthirstyTirelessChance) {
                HelperMethods.decrementStatusEffect(player, EffectRegistry.EXHAUSTION);
            }
        }
    }

    public static void effectBerserkerBloodthirstyTremor(Player player) {

        if (player.hasEffect(EffectRegistry.BLOODTHIRSTY)) {
            int bloodthirstyTremoreChance = SimplySkills.berserkerConfig.signatureBerserkerBloodthirstyTremorChance;
            if (player.getRandom().nextInt(100) < bloodthirstyTremoreChance) {
                HelperMethods.incrementStatusEffect(player, EffectRegistry.EARTHSHAKER, 30,
                        1, 1);
            }
        }
    }

    public static void effectBerserkerRampage(Player player) {

        if (player.hasEffect(EffectRegistry.RAMPAGE)) {
            int rampageSubEffectDuration = SimplySkills.berserkerConfig.signatureBerserkerRampageSubEffectDuration;
            int rampageSubEffectMaxAmplifier = SimplySkills.berserkerConfig.signatureBerserkerRampageSubEffectMaxAmplifier;

            List<Holder<MobEffect>> list = new ArrayList<>();
            list.add(MobEffects.DAMAGE_BOOST);
            list.add(MobEffects.MOVEMENT_SPEED);
            list.add(MobEffects.DAMAGE_RESISTANCE);
            list.add(MobEffects.DIG_SPEED);

            Random rand = new Random();
            Holder<MobEffect> randomStatus = list.get(rand.nextInt(list.size()));
            HelperMethods.incrementStatusEffect(player, randomStatus, rampageSubEffectDuration, 1,
                    rampageSubEffectMaxAmplifier);
        }
    }

    public static void effectRogueSiphoningStrikes(Entity target, Player player) {

        if (player.hasEffect(EffectRegistry.SIPHONINGSTRIKES)) {
            if (target instanceof LivingEntity livingTarget) {
                double leechMultiplier = SimplySkills.rogueConfig.signatureRogueSiphoningStrikesLeechMultiplier;

                double attackValue = Objects.requireNonNull(
                        player.getAttribute(Attributes.ATTACK_DAMAGE)).getValue();
                float healAmount = (float) (attackValue * leechMultiplier);
                player.heal(healAmount);

                HelperMethods.decrementStatusEffect(player, EffectRegistry.SIPHONINGSTRIKES);

                for (MobEffectInstance statusEffect : livingTarget.getActiveEffects()) {
                    if (statusEffect != null && statusEffect.getEffect().value().isBeneficial()) {
                        livingTarget.removeEffect(statusEffect.getEffect());
                        break;
                    }
                }

                if (HelperMethods.isUnlocked("simplyskills:rogue",
                        SkillReferencePosition.rogueSpecialisationSiphoningStrikesVanish, player))
                    AbilityEffects.effectRogueSiphoningStrikesVanish(player);

            }
        }
    }

    public static void effectRogueFanOfBlades(Player player) {
        int fobFrequency = SimplySkills.rogueConfig.signatureRogueFanOfBladesBaseFrequency;
        if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueSpecialisationEvasionFanOfBladesAssault, player))
            fobFrequency = SimplySkills.rogueConfig.signatureRogueFanOfBladesEnhancedFrequency;
        if (HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueSpecialisationEvasionFanOfBlades, player) &&
                player.hasEffect(EffectRegistry.FANOFBLADES) && player.tickCount % fobFrequency == 0) {
            int fobRange = SimplySkills.rogueConfig.signatureRogueFanOfBladesRange;
            int fobRadius = SimplySkills.rogueConfig.signatureRogueFanOfBladesRadius;
            int disenchantDuration = SimplySkills.rogueConfig.signatureRogueFanOfBladesDisenchantDuration;

            BlockPos blockPos = player.blockPosition().relative(player.getMotionDirection(), fobRange);
            BlockState blockstate = player.level().getBlockState(blockPos);
            BlockState blockstateUp = player.level().getBlockState(blockPos.above(1));
            for (int i = fobRange; i > 0; i--) {
                if (blockstate.isAir() && blockstateUp.isAir())
                    break;
                blockPos = player.blockPosition().relative(player.getMotionDirection(), i);
            }

            AABB box = HelperMethods.createBoxBetween(player.blockPosition(), blockPos, fobRadius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {

                        if (HelperMethods.isUnlocked("simplyskills:rogue",
                                SkillReferencePosition.rogueSpecialisationEvasionFanOfBladesAssault, player))
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:fan_of_blades_assault", fobRange * 2, le, HelperMethods.getBlockLookingAt(player, 256));
                        else
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:fan_of_blades", fobRange * 2, le, HelperMethods.getBlockLookingAt(player, 256));

                        if (HelperMethods.isUnlocked("simplyskills:rogue",
                                SkillReferencePosition.rogueSpecialisationEvasionFanOfBladesDisenchantment, player))
                            le.addEffect(new MobEffectInstance(EffectRegistry.DISENCHANTMENT, disenchantDuration, 0, false ,false));

                        if (HelperMethods.isUnlocked("simplyskills:rogue",
                                SkillReferencePosition.rogueBladestorm, player))
                            HelperMethods.incrementStatusEffect(player, EffectRegistry.BLADESTORM, 400, 1, 20);

                    }
                }
            }
            HelperMethods.decrementStatusEffect(player, EffectRegistry.FANOFBLADES);
        }
    }

    public static void effectRogueSiphoningStrikesVanish(Player player) {

        if (player.hasEffect(EffectRegistry.SIPHONINGSTRIKES)) {
            if (player.hasEffect(EffectRegistry.REVEALED))
                player.removeEffect(EffectRegistry.REVEALED);
        }
    }



    public static boolean effectRangerElementalArrows(Player player) {

        if (player.hasEffect(EffectRegistry.ELEMENTALARROWS)) {


            BlockPos blockpos = null;
            int radius = SimplySkills.rangerConfig.effectRangerElementalArrowsRadius;
            int increase = SimplySkills.rangerConfig.effectRangerElementalArrowsRadiusIncreasePerTier;
            int targetingRange = SimplySkills.rangerConfig.effectRangerElementalArrowsTargetingRange;
            int arrowCount = 1;
            int increasedArrowCount = 6;
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsRadiusThree, player))
                radius = radius + (increase * 3);
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsRadiusTwo, player))
                radius = radius + (increase * 2);
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsRadiusOne, player))
                radius = radius + increase;

            List<String> list = new ArrayList<>();
            list.add("simplyskills:frost_arrow_rain");
            list.add("simplyskills:fire_arrow_rain");
            list.add("simplyskills:lightning_arrow_rain");

            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsFireAttuned, player)) {
                list.remove("simplyskills:frost_arrow_rain");
                list.remove("simplyskills:lightning_arrow_rain");
            }
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsFrostAttuned, player)) {
                list.remove("simplyskills:fire_arrow_rain");
                list.remove("simplyskills:lightning_arrow_rain");
            }
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsLightningAttuned, player)) {
                list.remove("simplyskills:fire_arrow_rain");
                list.remove("simplyskills:frost_arrow_rain");
            }

            HelperMethods.decrementStatusEffect(player, EffectRegistry.ELEMENTALARROWS);
            
            if (HelperMethods.getTargetedEntity(player, targetingRange) !=null)
                blockpos = HelperMethods.getTargetedEntity(player, targetingRange).blockPosition();

            if (blockpos == null)
                blockpos = HelperMethods.getBlockLookingAt(player, targetingRange);

            if (blockpos != null) {
                int xpos = (int) blockpos.getX();
                int ypos = (int) blockpos.getY();
                int zpos = (int) blockpos.getZ();
                BlockPos searchArea = new BlockPos(xpos, ypos, zpos);
                AABB box = HelperMethods.createBoxAtBlock(searchArea, radius);
                for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).size() == 1)
                        arrowCount = increasedArrowCount;

                    for (int i = arrowCount; i > 0; i--) {
                        if (entities != null) {
                            Random rand = new Random();
                            String randomSpell = list.get(rand.nextInt(list.size()));
                            if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {
                                SignatureAbilities.castSpellEngineIndirectTarget(player,
                                        randomSpell,
                                        512, le, HelperMethods.getBlockLookingAt(player, 256));
                            }
                        }
                    }
                }
                Random rand = new Random();
                String randomSpell = list.get(rand.nextInt(list.size()));
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        randomSpell,
                        512, null, HelperMethods.getBlockLookingAt(player, 256));
            }
            return true;
        }
        return false;
    }

    public static boolean effectRangerMarksman(Player player) {

        if (player.hasEffect(EffectRegistry.MARKSMAN)) {
            Entity target = null;
            int targetingRange = SimplySkills.rangerConfig.effectRangerElementalArrowsTargetingRange;

            if (HelperMethods.getTargetedEntity(player, targetingRange) !=null)
                target = HelperMethods.getTargetedEntity(player, targetingRange);
            if ((target instanceof LivingEntity livingTarget) && !HelperMethods.checkFriendlyFire(livingTarget, player))
                target = null;

            String spell = "simplyskills:physical_bow_snipe";
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        spell,
                        512, target, HelperMethods.getBlockLookingAt(player, 256));
                HelperMethods.decrementStatusEffect(player, EffectRegistry.MARKSMAN);


            return true;
        }
        return false;
    }


    public static boolean effectRangerArrowRain(Player player) {

        if (player.hasEffect(EffectRegistry.ARROWRAIN)) {

            int arrowRainRadius = SimplySkills.rangerConfig.effectRangerArrowRainRadius;
            int arrowRainRadiusIncrease = SimplySkills.rangerConfig.effectRangerArrowRainRadiusIncreasePerTier;
            int arrowRainChance = SimplySkills.rangerConfig.effectRangerArrowRainArrowDensity;
            int arrowRainVolleys = SimplySkills.rangerConfig.effectRangerArrowRainVolleys;
            int arrowRainVolleyIncrease = SimplySkills.rangerConfig.effectRangerArrowRainVolleyIncreasePerTier;
            int arrowRainRange = SimplySkills.rangerConfig.effectRangerArrowRainRange;
            int projectileLimiterCap = Integer.MAX_VALUE;

            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainRadiusThree, player))
                arrowRainRadius = arrowRainRadius + (arrowRainRadiusIncrease * 3);
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainRadiusTwo, player))
                arrowRainRadius = arrowRainRadius + (arrowRainRadiusIncrease * 2);
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainRadiusOne, player))
                arrowRainRadius = arrowRainRadius + arrowRainRadiusIncrease;

            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainVolleyThree, player))
                arrowRainVolleys = arrowRainVolleys + (arrowRainVolleyIncrease * 3);
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainVolleyTwo, player))
                arrowRainVolleys = arrowRainVolleys + (arrowRainVolleyIncrease * 2);
            else if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainVolleyOne, player))
                arrowRainVolleys = arrowRainVolleys + arrowRainVolleyIncrease;


            BlockPos blockpos2;
            Entity target;
            Vec3 blockpos = HelperMethods.getPositionLookingAt(player, arrowRainRange);
            if (blockpos == null) {
                blockpos2 = HelperMethods.getBlockLookingAt(player, arrowRainRange);
                if (blockpos2 != null) {
                    target = EntityRegistry.SPELL_TARGET_ENTITY.spawn((ServerLevel) player.level(),
                            blockpos2,
                            MobSpawnType.TRIGGERED);
                    if (target !=null)
                        blockpos = target.position();
                }
            }

            if (blockpos != null) {
                boolean elemental = HelperMethods.isUnlocked("simplyskills:ranger",
                        SkillReferencePosition.rangerSpecialisationArrowRainElemental, player);
                if (elemental) {
                    BlockPos blockPos = player.blockPosition().relative(player.getMotionDirection(), 3);
                    AABB box = HelperMethods.createBoxBetween(player.blockPosition(), blockPos, 3);
                    for (Entity entity : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                        if (entity instanceof LivingEntity livingEntity && HelperMethods.checkFriendlyFireAOE(livingEntity, player)) {
                            projectileLimiterCap = 4;
                            break;
                        }
                    }
                }

                ArrowRainVolley volley = new ArrowRainVolley(blockpos, arrowRainRadius, arrowRainChance,
                        arrowRainVolleys, elemental, projectileLimiterCap, player.tickCount);
                pendingArrowRainVolleys.put(player.getUUID(), volley);
                prepareArrowRain((ServerPlayer) player, volley);
                spawnArrowRainVolley((ServerPlayer) player, volley);
                HelperMethods.decrementStatusEffect(player, EffectRegistry.ARROWRAIN);
            }
            return true;
        }
        return false;
    }

    public static void tickRangerArrowRain(ServerPlayer player) {
        ArrowRainVolley volley = pendingArrowRainVolleys.get(player.getUUID());
        if (volley == null || player.tickCount < volley.nextVolleyTick)
            return;

        spawnArrowRainVolley(player, volley);
    }

    public static void clearRangerArrowRain(ServerPlayer player) {
        pendingArrowRainVolleys.remove(player.getUUID());
    }

    private static void prepareArrowRain(ServerPlayer player, ArrowRainVolley volley) {
        int xpos = (int) volley.position.x() - volley.radius;
        int ypos = (int) volley.position.y();
        int zpos = (int) volley.position.z() - volley.radius;

        for (int i = volley.volleys; i > 0; i--) {
            List<BlockPos> normalArrows = new ArrayList<>();
            for (int x = volley.radius * 2; x > 0; x--) {
                for (int z = volley.radius * 2; z > 0; z--) {
                    BlockPos spawnPosition = new BlockPos(xpos + x,
                            ypos + 25 + (player.getRandom().nextInt(15) * volley.volleys + 1),
                            zpos + z);

                    if (player.getRandom().nextInt(100) < volley.density
                            && player.level().getBlockState(spawnPosition).isAir()) {
                        String elementalSpell = null;
                        if (volley.elemental && volley.elementalProjectiles < volley.projectileLimit)
                            elementalSpell = getArrowRainElement(player);

                        if (elementalSpell != null) {
                            SimplySkillsArrowEntity arrowEntity = spawnArrowRainArrow(player, spawnPosition);
                            arrowEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN));
                            SignatureAbilities.castSpellEngineIndirectTarget(player,
                                    elementalSpell, 512, arrowEntity, null);
                            arrowEntity.setInvisible(true);
                            volley.elementalProjectiles++;
                        } else normalArrows.add(spawnPosition);
                    }
                }
            }
            volley.normalArrowVolleys.add(normalArrows);
        }
    }

    private static void spawnArrowRainVolley(ServerPlayer player, ArrowRainVolley volley) {
        int volleyNumber = volley.volleys - volley.remainingVolleys;
        for (BlockPos spawnPosition : volley.normalArrowVolleys.get(volleyNumber))
            spawnArrowRainArrow(player, spawnPosition);

        volley.remainingVolleys--;
        if (volley.remainingVolleys > 0)
            volley.nextVolleyTick = player.tickCount + 4;
        else pendingArrowRainVolleys.remove(player.getUUID());
    }

    private static SimplySkillsArrowEntity spawnArrowRainArrow(ServerPlayer player, BlockPos position) {
        SimplySkillsArrowEntity arrowEntity = new SimplySkillsArrowEntity(EntityType.ARROW, player.level());
        arrowEntity.absMoveTo(position.getX(), position.getY(), position.getZ());
        arrowEntity.setOwner(player);
        arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        arrowEntity.setDeltaMovement(0, -0.5, 0);
        player.level().addFreshEntity(arrowEntity);
        return arrowEntity;
    }

    private static String getArrowRainElement(Player player) {
        if (player.getRandom().nextInt(100) < 5)
            return "simplyskills:fire_arrow_rain";
        else if (player.getRandom().nextInt(100) < 15)
            return "simplyskills:frost_arrow_rain";
        else if (player.getRandom().nextInt(100) < 25)
            return "simplyskills:lightning_arrow_rain";
        return null;
    }

    public static void effectWizardFrostVolley(Player player) {
        int frequency = SimplySkills.wizardConfig.signatureWizardIceCometVolleyFrequency;

        if (HelperMethods.isUnlocked("simplyskills:wizard",
                SkillReferencePosition.wizardSpecialisationIceCometVolley, player) &&
        player.hasEffect(EffectRegistry.FROSTVOLLEY) && player.tickCount % frequency == 0) {
            Entity target = null;
            int volleyRange = SimplySkills.wizardConfig.signatureWizardIceCometVolleyRange;

            if (HelperMethods.getTargetedEntity(player, volleyRange) !=null)
                target = HelperMethods.getTargetedEntity(player, volleyRange);
            if ((target instanceof LivingEntity livingTarget) && !HelperMethods.checkFriendlyFire(livingTarget, player))
                target = null;

            String spell = "simplyskills:frost_arrow";
            SignatureAbilities.castSpellEngineIndirectTarget(player,
                    spell,
                    volleyRange, target, HelperMethods.getBlockLookingAt(player, volleyRange));
            HelperMethods.decrementStatusEffect(player, EffectRegistry.FROSTVOLLEY);
        }
    }
    public static void effectWizardArcaneVolley(Player player) {
        int volleyFrequency = SimplySkills.wizardConfig.signatureWizardArcaneBoltVolleyFrequency;

        if (HelperMethods.isUnlocked("simplyskills:wizard",
                SkillReferencePosition.wizardSpecialisationArcaneBoltVolley, player) &&
                player.hasEffect(EffectRegistry.ARCANEVOLLEY) && player.tickCount % volleyFrequency == 0) {
            Entity target = null;
            int volleyRange = SimplySkills.wizardConfig.signatureWizardArcaneBoltVolleyRange;

            if (HelperMethods.getTargetedEntity(player, volleyRange) !=null)
                target = HelperMethods.getTargetedEntity(player, volleyRange);
            if ((target instanceof LivingEntity livingTarget) && !HelperMethods.checkFriendlyFire(livingTarget, player))
                target = null;

            String spell = "simplyskills:arcane_bolt_lesser";
            SignatureAbilities.castSpellEngineIndirectTarget(player,
                    spell,
                    volleyRange, target, HelperMethods.getBlockLookingAt(player, volleyRange));
            HelperMethods.decrementStatusEffect(player, EffectRegistry.ARCANEVOLLEY);
        }
    }
    public static void effectWizardMeteoricWrath(Player player) {
        int frequency = SimplySkills.wizardConfig.signatureWizardMeteoricWrathFrequency;

        if (HelperMethods.isUnlocked("simplyskills:wizard",
                SkillReferencePosition.wizardSpecialisationMeteorShowerWrath, player) &&
                player.hasEffect(EffectRegistry.METEORICWRATH) && player.tickCount % frequency == 0) {
            int chance = SimplySkills.wizardConfig.signatureWizardMeteoricWrathChance;
            int radius = SimplySkills.wizardConfig.signatureWizardMeteoricWrathRadius;
            int baseRenewalChance = SimplySkills.wizardConfig.signatureWizardMeteoricWrathRenewalBaseChance;
            int renewalChancePerTier = SimplySkills.wizardConfig.signatureWizardMeteoricWrathRenewalChanceIncreasePerTier;
            String spellIdentifier = "simplyskills:fire_meteor_small";


            if (SignatureAbilities.castSpellEngineAOE(player, spellIdentifier, radius, chance, true, false)) {
                int renewalChance = 0;
                if (HelperMethods.isUnlocked("simplyskills:wizard",
                        SkillReferencePosition.wizardSpecialisationMeteorShowerRenewingWrathThree, player))
                    renewalChance = baseRenewalChance + (renewalChancePerTier * 2);
                else if (HelperMethods.isUnlocked("simplyskills:wizard",
                        SkillReferencePosition.wizardSpecialisationMeteorShowerRenewingWrathTwo, player))
                    renewalChance = baseRenewalChance + renewalChancePerTier;
                else if (HelperMethods.isUnlocked("simplyskills:wizard",
                        SkillReferencePosition.wizardSpecialisationMeteorShowerRenewingWrath, player))
                    renewalChance = baseRenewalChance;
                if (player.getRandom().nextInt(100) >= renewalChance)
                    HelperMethods.decrementStatusEffect(player, EffectRegistry.METEORICWRATH);
            }
        }

    }

    public static void effectSpellbladeSpellweaving(Entity target, Player player) {
        int chance = SimplySkills.spellbladeConfig.passiveSpellbladeSpellweavingChance;
        int spellweaverHasteDuration = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverHasteDuration;
        int spellweaverHasteStacks = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverHasteStacks;
        int spellweaverHasteMaxStacks = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverHasteMaxStacks;
        int spellweaverRegenerationDuration = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverRegenerationDuration;
        int spellweaverRegenerationStacks = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverRegenerationStacks;
        int spellweaverRegenerationMaxStacks = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverRegenerationMaxStacks;
        int spellweaverRegenerationChance = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverRegenerationChance;

        if (player.hasEffect(EffectRegistry.SPELLWEAVER) &&
                (HelperMethods.isUnlocked("simplyskills:spellblade",
                        SkillReferencePosition.spellbladeSpecialisationSpellweaver, player)))
            chance = SimplySkills.spellbladeConfig.signatureSpellbladeSpellweaverChance;

        List<String> list = new ArrayList<>();
        list.add("simplyskills:frost_arrow");
        list.add("simplyskills:fire_arrow");
        list.add("simplyskills:lightning_arrow");
        list.add("simplyskills:arcane_bolt");
        list.add("simplyskills:arcane_bolt_lesser");
        list.add("simplyskills:ice_comet");
        list.add("simplyskills:fire_meteor_small");
        list.add("simplyskills:static_discharge");

        if (player.hasEffect(EffectRegistry.SPELLWEAVER)) {
            list.add("simplyskills:physical_swordrain");
            list.add("simplyskills:arcane_slash_projectile");
            list.add("simplyskills:righteous_hammer_projectile");
            list.add("simplyskills:lightning_ball_homing");
            list.add("simplyskills:fire_meteor_large");
        }
        int spellChoice = player.getRandom().nextInt(list.size());

        if ((target instanceof LivingEntity livingTarget) && player.getRandom().nextInt(100) < chance) {
            SignatureAbilities.castSpellEngineIndirectTarget(player,
                    list.get(spellChoice),
                    8, livingTarget, HelperMethods.getBlockLookingAt(player, 256));

            if (HelperMethods.isUnlocked("simplyskills:spellblade",
                    SkillReferencePosition.spellbladeSpecialisationSpellweaverHaste, player))
                HelperMethods.incrementStatusEffect(player, MobEffects.DIG_SPEED, spellweaverHasteDuration,
                        spellweaverHasteStacks, spellweaverHasteMaxStacks);
            if (HelperMethods.isUnlocked("simplyskills:spellblade",
                    SkillReferencePosition.spellbladeSpecialisationSpellweaverRegeneration, player) &&
                    player.getRandom().nextInt(100) < spellweaverRegenerationChance)
                HelperMethods.incrementStatusEffect(player, MobEffects.REGENERATION, spellweaverRegenerationDuration,
                        spellweaverRegenerationStacks, spellweaverRegenerationMaxStacks);
        }
    }

    public static void effectRagingJavelin(Player player) {
        int frequency = SimplySkills.warriorConfig.passiveWarriorRagingJavelinFrequency;
        if (player.tickCount % frequency == 0 && !player.getMainHandItem().isEmpty()) {

            String spellIdentifier = "simplyskills:passive_throw";
            int radius = 10;
            int chance = 80;

            if (SignatureAbilities.castSpellEngineAOE(player, spellIdentifier, radius, chance, true, false)) {
            }

        }
    }




}
