package net.sweenus.simplyskills.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellExecution;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RangerAbilities {

    private static final List<ResourceLocation> arrowRainElements = List.of(
            ResourceLocation.parse("simplyskills:frost_arrow_homing"),
            ResourceLocation.parse("simplyskills:fire_arrow_homing"),
            ResourceLocation.parse("simplyskills:lightning_arrow_homing"));

    public static void passiveRangerReveal(Player player) {
        int frequency = SimplySkills.rangerConfig.passiveRangerRevealFrequency;
        if (player.tickCount % frequency == 0) {
            int radius = SimplySkills.rangerConfig.passiveRangerRevealRadius;

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {
                        for (MobEffectInstance statusEffect : le.getActiveEffects()) {
                            if (statusEffect != null && statusEffect.getEffect().equals(EffectRegistry.STEALTH)) {
                                le.removeEffect(statusEffect.getEffect());
                                le.addEffect(new MobEffectInstance(EffectRegistry.REVEALED, 180, 1, false, false, true));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isOwnPet(Entity entity, Player player) {
        if ((entity instanceof OwnableEntity te)) {
            if (te.getOwner() != null) {
                return Objects.equals(te.getOwnerUUID(), player.getUUID());
            }
        }
        return false;
    }

    public static void passiveRangerTamer(Player player) {
        int frequency = SimplySkills.rangerConfig.passiveRangerTamerFrequency;
        if (player.tickCount % frequency == 0) {
            int radius = SimplySkills.rangerConfig.passiveRangerTamerRadius;
            int resistanceAmplifier = SimplySkills.rangerConfig.passiveRangerTamerResistanceAmplifier;
            int regenerationAmplifier = SimplySkills.rangerConfig.passiveRangerTamerRegenerationAmplifier;

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null && entities instanceof  LivingEntity le) {
                    if (isOwnPet(entities, player)) {
                        le.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                                 frequency + 5, regenerationAmplifier, false, false, true));
                        le.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                                 frequency + 5, resistanceAmplifier, false, false, true));
                    }
                }
            }
        }
    }
    public static void passiveRangerBonded(Player player) {
        int frequency = SimplySkills.rangerConfig.passiveRangerBondedFrequency;
        if (player.tickCount % frequency == 0) {
            int radius = SimplySkills.rangerConfig.passiveRangerBondedRadius;
            int petMinimumHealthPercent = SimplySkills.rangerConfig.passiveRangerBondedPetMinimumHealthPercent;
            int healthTransferAmount = SimplySkills.rangerConfig.passiveRangerBondedHealthTransferAmount;

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null && entities instanceof  LivingEntity le) {
                    if (isOwnPet(entities, player)) {
                        float teHealthPercent = ((le.getHealth() / le.getMaxHealth()) * 100);
                        float playerHealthPercent = ((player.getHealth() / player.getMaxHealth()) * 100);
                        if (teHealthPercent > playerHealthPercent && teHealthPercent > petMinimumHealthPercent) {
                            le.setHealth(le.getHealth() - healthTransferAmount);
                            player.heal(healthTransferAmount);
                        }
                    }
                }
            }
        }
    }
    public static void passiveRangerTrained(Player player) {
        int frequency = SimplySkills.rangerConfig.passiveRangerTrainedFrequency;
        if (player.tickCount % frequency == 0) {
            int radius = SimplySkills.rangerConfig.passiveRangerTrainedRadius;
            int strengthAmplifier = SimplySkills.rangerConfig.passiveRangerTrainedStrengthAmplifier;
            int speedAmplifier = SimplySkills.rangerConfig.passiveRangerTrainedSpeedAmplifier;
            int minimumHealthPercent = SimplySkills.rangerConfig.passiveRangerTrainedMinimumHealthPercent;

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null && entities instanceof  LivingEntity le) {
                    if (isOwnPet(entities, player)) {
                        float teHealthPercent = ((le.getHealth() / le.getMaxHealth()) * 100);
                        if (teHealthPercent > minimumHealthPercent) {
                            le.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                                    frequency + 5, strengthAmplifier, false, false, true));
                            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                                    frequency + 5, speedAmplifier, false, false, true));
                        }
                    }
                }
            }
        }
    }

    public static void passiveRangerIncognito(Player player) {
        int frequency = SimplySkills.rangerConfig.passiveRangerIncognitoFrequency;
        if (player.tickCount % frequency == 0) {
            int radius = SimplySkills.rangerConfig.passiveRangerIncognitoRadius;

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null && entities instanceof  LivingEntity le) {
                    if (isOwnPet(entities, player)) {
                        if (player.hasEffect(EffectRegistry.STEALTH)) {
                            le.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,
                                    frequency + 5,0, false, false, true));

                        }
                    }
                }
            }
        }
    }

    public static void passiveRangerElementalArrowsRenewal(Player player) {
        int random = new Random().nextInt(100);
        int renewalChance = SimplySkills.rangerConfig.passiveRangerElementalArrowsRenewalChance;
        int renewalDuration = SimplySkills.rangerConfig.passiveRangerElementalArrowsRenewalDuration;
        int renewalMaxStacks = SimplySkills.rangerConfig.passiveRangerElementalArrowsRenewalMaximumStacks;
        int renewalStacks = SimplySkills.rangerConfig.passiveRangerElementalArrowsRenewalStacks;
        if (random < renewalChance)
            HelperMethods.incrementStatusEffect(player, EffectRegistry.ELEMENTALARROWS,
                    renewalDuration, renewalStacks, renewalMaxStacks);
    }


    //------- SIGNATURE ABILITIES --------


    // Disengage
    public static boolean signatureRangerDisengage(String rangerSkillTree, Player player) {

        int radius = SimplySkills.rangerConfig.signatureRangerDisengageRadius;
        int velocity = SimplySkills.rangerConfig.signatureRangerDisengageVelocity;
        int height = SimplySkills.rangerConfig.signatureRangerDisengageHeight;
        int slownessDuration = SimplySkills.rangerConfig.signatureRangerDisengageSlownessDuration;
        int slownessAmplifier = SimplySkills.rangerConfig.signatureRangerDisengageSlownessAmplifier;
        int slowFallDuration = SimplySkills.rangerConfig.signatureRangerDisengageSlowFallDuration;
        int slowFallAmplifier = SimplySkills.rangerConfig.signatureRangerDisengageSlowFallAmplifier;

        AABB box = HelperMethods.createBox(player, radius);
        for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
            if (entities != null) {
                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {

                    le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                            slownessDuration, slownessAmplifier, false, false, true));

                }
            }
        }

        player.setDeltaMovement(player.getLookAngle().reverse().scale(+velocity));
        player.setDeltaMovement(player.getDeltaMovement().x, height, player.getDeltaMovement().z);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,
                slowFallDuration, slowFallAmplifier, false, false, true));
        player.hurtMarked = true;

        if (HelperMethods.isUnlocked(rangerSkillTree,
                SkillReferencePosition.rangerSpecialisationDisengageRecuperate, player))
            signatureRangerDisengageRecuperate(player);

        if (HelperMethods.isUnlocked(rangerSkillTree,
                SkillReferencePosition.rangerSpecialisationDisengageExploitation, player))
            signatureRangerDisengageExploitation(player);

        if (HelperMethods.isUnlocked(rangerSkillTree,
                SkillReferencePosition.rangerSpecialisationDisengageMarksman, player)) {
            int marksmanDuration = SimplySkills.rangerConfig.signatureRangerDisengageMarksmanDuration;
            int marksmanStacks = SimplySkills.rangerConfig.signatureRangerDisengageMarksmanStacks;
            if (player.getMainHandItem().getItem() instanceof BowItem)
                HelperMethods.incrementStatusEffect(player, EffectRegistry.MARKSMAN, marksmanDuration, marksmanStacks, 99);
            else
                HelperMethods.incrementStatusEffect(player, EffectRegistry.BARRIER, marksmanDuration, marksmanStacks, 6);
        }
        SignatureAbilities.playCastingGesture(player, "spell_engine:one_handed_area_release");
        return true;
    }

    // Disengage Recuperate
    public static void signatureRangerDisengageRecuperate(Player player) {
        int radius = SimplySkills.rangerConfig.signatureRangerDisengageRecuperateRadius;

        AABB box = HelperMethods.createBox(player, radius);
        for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

            if (entities != null && entities instanceof  LivingEntity le) {
                if (isOwnPet(entities, player)) {
                    le.heal(le.getMaxHealth());
                }
            }
        }
    }

    // Disengage Exploitation
    public static void signatureRangerDisengageExploitation(Player player) {
        int radius = SimplySkills.rangerConfig.signatureRangerDisengageExploitationRadius;
        int effectDuration = SimplySkills.rangerConfig.signatureRangerDisengageExploitationDuration;

        AABB box = HelperMethods.createBox(player, radius);
        for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

            if (entities != null && entities instanceof LivingEntity le) {
                if (isOwnPet(entities, player)) {
                    le.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZINGAURA, effectDuration, 0, false, false, true));
                }
            }
        }
    }

    // Elemental Arrows
    public static boolean signatureRangerElementalArrows(String rangerSkillTree, Player player) {
        int elementalArrowsDuration = SimplySkills.rangerConfig.effectRangerElementalArrowsDuration;
        int elementalArrowsStacks = SimplySkills.rangerConfig.effectRangerElementalArrowsStacks;
        int elementalArrowsStacksIncreasePerTier = SimplySkills.rangerConfig.effectRangerElementalArrowsStacksIncreasePerTier;

        int amplifier =elementalArrowsStacks;

        if (HelperMethods.isUnlocked(rangerSkillTree,
                SkillReferencePosition.rangerSpecialisationElementalArrowsStacksThree, player))
            amplifier = amplifier + (elementalArrowsStacksIncreasePerTier * 3);
        else if (HelperMethods.isUnlocked(rangerSkillTree,
                SkillReferencePosition.rangerSpecialisationElementalArrowsStacksTwo, player))
            amplifier = amplifier + (elementalArrowsStacksIncreasePerTier * 2);
        else if (HelperMethods.isUnlocked(rangerSkillTree,
                SkillReferencePosition.rangerSpecialisationElementalArrowsStacksOne, player))
            amplifier = amplifier + elementalArrowsStacksIncreasePerTier;

        player.addEffect(new MobEffectInstance(EffectRegistry.ELEMENTALARROWS,
                elementalArrowsDuration, amplifier, false, false, true));

        SignatureAbilities.playCastingGesture(player, "spell_engine:one_handed_healing_release");
        return true;
    }

    // Elemental Arrows
    public static boolean signatureRangerArrowRain(String rangerSkillTree, Player player) {
        int arrowRainDuration = SimplySkills.rangerConfig.effectRangerArrowRainDuration;
        player.addEffect(new MobEffectInstance(EffectRegistry.ARROWRAIN, arrowRainDuration, 0, false, false, true));
        SignatureAbilities.playCastingGesture(player, "spell_engine:archery_upwards_release");
        return true;
    }

    // Arrow Rain Elemental Artillery
    public static void signatureRangerElementalArtillery(ServerPlayer player, SpellProjectile spellProjectile, ResourceLocation spellId, SpellExecution.ImpactContext context, Spell .ProjectileData.Perks perks) {
        if (player != null && spellProjectile.tickCount % 12 == 0 && spellProjectile.tickCount > 30) {
            if (HelperMethods.isUnlocked("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationArrowRainElementalArtillery, player)
                    && spellId.toString().contains("arrow_rain")) {

                int radius = 20;
                LivingEntity target = null;
                AABB box = spellProjectile.getBoundingBox().inflate(radius, radius * 3, radius);
                for (Entity entity : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                    if (player.getRandom().nextInt(100) < 35) {
                        if (entity instanceof LivingEntity livingEntity && HelperMethods.checkFriendlyFireAOE(livingEntity, player)) {
                            target = livingEntity;
                            break;
                        }
                    }
                }

                if (target != null) {
                    Vec3 position = spellProjectile.position();
                    ResourceLocation randomSpell = arrowRainElements.get(player.getRandom().nextInt(arrowRainElements.size()));
                    SpellProjectile projectile = new SpellProjectile(spellProjectile.level(),
                            (LivingEntity) spellProjectile.getOwner(), position.x(), position.y(), position.z(),
                            spellProjectile.getBehaviour(), SpellRegistry.from(player.level()).getHolder(randomSpell).orElseThrow(),
                            context, perks.copy());

                    projectile.setDeltaMovement(spellProjectile.getDeltaMovement());
                    projectile.range = spellProjectile.range;
                    projectile.setFollowedTarget(target);
                    ProjectileUtil.rotateTowardsMovement(projectile, 0.2F);
                    spellProjectile.level().addFreshEntity(projectile);
                }
            }
        }
    }

}
