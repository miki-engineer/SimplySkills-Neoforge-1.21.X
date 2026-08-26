package net.sweenus.simplyskills.abilities;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class WayfarerAbilities {

    public static void passiveWayfarerBreakStealth(
            Entity target,
            Player player,
            Boolean brokenByDamage,
            Boolean backstabBonus) {

        if (player.hasEffect(EffectRegistry.STEALTH)) {

            if (brokenByDamage) {

                int speedDuration = SimplySkills.rogueConfig.passiveRogueFleetfootedSpeedDuration;
                int speedStacks = SimplySkills.rogueConfig.passiveRogueFleetfootedSpeedStacks;
                int speedMaxStacks = SimplySkills.rogueConfig.passiveRogueFleetfootedSpeedMaxStacks;
                int evasionDuration = SimplySkills.wayfarerConfig.passiveWayfarerReflexiveEvasionDuration;
                int evasionChance = SimplySkills.wayfarerConfig.passiveWayfarerReflexiveChance;

                if (HelperMethods.isUnlocked("simplyskills:rogue",
                        SkillReferencePosition.rogueFleetfooted, player))
                    HelperMethods.incrementStatusEffect(player, MobEffects.MOVEMENT_SPEED,
                            speedDuration, speedStacks, speedMaxStacks);
                if (HelperMethods.isUnlocked("simplyskills:tree",
                        SkillReferencePosition.wayfarerReflexive, player)) {
                    int roll = player.getRandom().nextInt(100);
                    if (roll < evasionChance) {
                        HelperMethods.incrementStatusEffect(player, EffectRegistry.EVASION,
                                evasionDuration, 1, 1);
                    }
                }

            }

            if ( !brokenByDamage ) {

                if (HelperMethods.isUnlocked("simplyskills:tree",
                        SkillReferencePosition.wayfarerReflexive, player)) {
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, 40, 1, 20);
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.MARKSMANSHIP, 40, 1, 20);
                }

                if (target != null) {
                    if (target instanceof LivingEntity livingTarget) {
                        int deathmarkDuration = SimplySkills.rogueConfig.passiveRogueExploitationDeathMarkDuration;
                        int deathmarkStacks = SimplySkills.rogueConfig.passiveRogueExploitationDeathMarkStacks;

                        if (backstabBonus && HelperMethods.isBehindTarget(player, livingTarget)) {
                            if (HelperMethods.isUnlocked("simplyskills:rogue",
                                    SkillReferencePosition.rogueExploitation, player))
                                HelperMethods.incrementStatusEffect(
                                        livingTarget,
                                        EffectRegistry.DEATHMARK,
                                        deathmarkDuration,
                                        deathmarkStacks,
                                        3);
                            if (HelperMethods.isUnlocked("simplyskills:rogue",
                                    SkillReferencePosition.rogueOpportunisticMastery, player))
                                RogueAbilities.passiveRogueOpportunisticMastery(livingTarget, player);
                        }
                    }
                }
            }
            player.removeEffect(EffectRegistry.STEALTH);
            player.level().playSound(
                    null, player, SoundRegistry.SOUNDEFFECT36,
                    SoundSource.PLAYERS, 0.7f, 1.4f);
            if (player.hasEffect(MobEffects.INVISIBILITY))
                player.removeEffect(MobEffects.INVISIBILITY);
            player.addEffect(new MobEffectInstance(EffectRegistry.REVEALED, 180, 5, false, false, true));
        }
    }

    public static void passiveWayfarerGuarding(Player player) {
        int barrierFrequency = SimplySkills.wayfarerConfig.passiveWayfarerGuardingBarrierFrequency;
        int barrierDuration = SimplySkills.wayfarerConfig.passiveWayfarerGuardingBarrierDuration;
        int barrierStacks = SimplySkills.wayfarerConfig.passiveWayfarerGuardingBarrierStacks;
        int barrierMaxStacks = SimplySkills.wayfarerConfig.passiveWayfarerGuardingBarrierMaxStacks;
        if (player.getOffhandItem().getItem() instanceof CrossbowItem
                && player.tickCount % barrierFrequency == 0) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.BARRIER, barrierDuration, barrierStacks, barrierMaxStacks);
        }
    }

    public static void passiveWayfarerSlender(Player player) {

        int slenderArmorThreshold = SimplySkills.wayfarerConfig.passiveWayfarerSlenderArmorThreshold;
        int frailArmorThreshold = SimplySkills.initiateConfig.passiveInitiateFrailArmorThreshold;

        if (player.tickCount % 20 == 0) {

            int armorValue = player.getArmorValue();

            if (armorValue < slenderArmorThreshold) {
                if (HelperMethods.isUnlocked("simplyskills:tree", SkillReferencePosition.roguePath, player)
                        || HelperMethods.isUnlocked("simplyskills:tree", SkillReferencePosition.rangerPath, player)) {

                    int buffAmplifier = (slenderArmorThreshold - armorValue) / 5;
                    player.addEffect(new MobEffectInstance(EffectRegistry.AGILE,
                            25, buffAmplifier, false, false, false));
                }
            }
            if (armorValue < frailArmorThreshold) {
                if (HelperMethods.isUnlocked("simplyskills:tree", SkillReferencePosition.wizardPath, player)) {
                    int buffAmplifier = (frailArmorThreshold - armorValue) / 5;
                    player.addEffect(new MobEffectInstance(EffectRegistry.AGILE,
                            25, buffAmplifier, false, false, false));
                }
            }
        }
    }

    public static boolean passiveWayfarerStealth(Player player) {
        boolean active = HelperMethods.isUnlocked("simplyskills:tree",
                SkillReferencePosition.wayfarerStealth, player)
                && player.isShiftKeyDown()
                && !player.hasEffect(EffectRegistry.REVEALED) && !isPlayerTargeted(player, 20);
        return active;
    }

    public static boolean isPlayerTargeted(Player player, int radius) {
        Level world = player.level();
        AABB box = new AABB(player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                player.getX() + radius, player.getY() + radius, player.getZ() + radius);

        // Check if any MobEntity has the player targeted
        for (Entity entity : world.getEntities(player, box, entity -> entity instanceof Mob)) {
            if (entity instanceof Mob mobEntity) {
                LivingEntity target = mobEntity.getTarget();
                if (target == player) {
                    return true;
                }
            }
        }

        // Check if any PlayerEntity has the player in their viewing angle
        for (Entity entity : world.getEntities(player, box, entity -> entity instanceof Player)) {
            if (entity instanceof Player otherPlayer && otherPlayer != player) {
                if (isInViewingAngle(otherPlayer, player)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isInViewingAngle(Player viewer, Player target) {
        Vec3 viewerPos = viewer.position();
        Vec3 targetPos = target.position();
        Vec3 directionToTarget = targetPos.subtract(viewerPos).normalize();
        Vec3 viewerLookVec = viewer.getViewVector(1.0F).normalize();

        double dotProduct = viewerLookVec.dot(directionToTarget);
        double threshold = Math.cos(Math.toRadians(90)); // 90 degrees viewing angle

        return dotProduct > threshold;
    }



}
