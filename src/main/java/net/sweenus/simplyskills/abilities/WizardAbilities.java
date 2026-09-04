package net.sweenus.simplyskills.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellExecution;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.effects.StaticChargeEffect;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.ArrayList;
import java.util.List;

public class WizardAbilities {

    public static void passiveWizardSpellEcho(Player player, List<Entity> targets) {
        //Can we get Spell Identifier from raw Spell in future? This would be better
        Entity target = null;
        //Choose random target from list
        if (!targets.isEmpty())
            target = targets.get(player.getRandom().nextInt(targets.size()));

        int chance = SimplySkills.wizardConfig.passiveWizardSpellEchoChance;
        if (AscendancyAbilities.magicCircleEffect(player))
            chance += 10;
        if (player.getRandom().nextInt(100) < chance) {

            List<String> list = new ArrayList<>();
            list.add("simplyskills:frost_arrow");
            list.add("simplyskills:fire_arrow");
            list.add("simplyskills:lightning_arrow");
            list.add("simplyskills:arcane_bolt_lesser");
            list.add("simplyskills:ice_comet");
            list.add("simplyskills:fire_meteor");
            list.add("simplyskills:static_discharge");
            int spellChoice = player.getRandom().nextInt(list.size());

            if (target != null)
                SignatureAbilities.castSpellEngineIndirectTarget(player, list.get(spellChoice), 120, target, null);
        }
    }


    //------- SIGNATURE ABILITIES --------


    // Meteor Shower
    public static boolean signatureWizardMeteorShower(String wizardSkillTree, Player player) {
        Vec3 blockpos = null;
        boolean success = false;
        int meteoricWrathDuration = SimplySkills.wizardConfig.signatureWizardMeteoricWrathDuration;
        int meteoricWrathStacks = SimplySkills.wizardConfig.signatureWizardMeteoricWrathStacks - 1;
        int meteorShowerRange = SimplySkills.wizardConfig.signatureWizardMeteorShowerRange;

        if (HelperMethods.getTargetedEntity(player, meteorShowerRange) !=null)
            blockpos = HelperMethods.getTargetedEntity(player, meteorShowerRange).position();

        if (blockpos == null)
            blockpos = HelperMethods.getPositionLookingAt(player, meteorShowerRange);

        if (blockpos != null) {
            int xpos = (int) blockpos.x();
            int ypos = (int) blockpos.y();
            int zpos = (int) blockpos.z();
            BlockPos searchArea = new BlockPos(xpos, ypos, zpos);
            AABB box = HelperMethods.createBoxAtBlock(searchArea, 8);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                        success = true;

                        if (HelperMethods.isUnlocked(wizardSkillTree,
                                SkillReferencePosition.wizardSpecialisationMeteorShowerWrath, player))
                            player.addEffect(new MobEffectInstance(EffectRegistry.METEORICWRATH,
                                    meteoricWrathDuration, meteoricWrathStacks, false, false, true));

                        if (HelperMethods.isUnlocked(wizardSkillTree,
                                SkillReferencePosition.wizardSpecialisationMeteorShowerGreater, player)) {
                            SignatureAbilities.castSpellEngineIndirectTarget(player,
                                    "simplyskills:fire_meteor_large",
                                    8, le, searchArea);
                            break;
                        }
                        else
                            SignatureAbilities.castSpellEngineIndirectTarget(player,
                                    "simplyskills:fire_meteor",
                                    8, le, searchArea);
                        break;
                    }
                }
            }
        }
        return success;
    }

    // Ice Comet
    public static boolean signatureWizardIceComet(String wizardSkillTree, Player player) {
        Vec3 blockpos = null;
        boolean success = false;
        LivingEntity target = null;
        int leapVelocity = SimplySkills.wizardConfig.signatureWizardIceCometLeapVelocity;
        double leapHeight = SimplySkills.wizardConfig.signatureWizardIceCometLeapHeight;
        int leapSlowfallDuration = SimplySkills.wizardConfig.signatureWizardIceCometLeapSlowfallDuration;
        int volleyDuration = SimplySkills.wizardConfig.signatureWizardIceCometVolleyDuration;
        int volleyStacks = SimplySkills.wizardConfig.signatureWizardIceCometVolleyStacks - 1;
        int iceCometRange = SimplySkills.wizardConfig.signatureWizardIceCometRange;

        if (HelperMethods.getTargetedEntity(player, iceCometRange) != null)
            blockpos = HelperMethods.getTargetedEntity(player, iceCometRange).position();

        if (blockpos == null)
            blockpos = HelperMethods.getPositionLookingAt(player, iceCometRange);

        if (blockpos != null) {
            int xpos = (int) blockpos.x();
            int ypos = (int) blockpos.y();
            int zpos = (int) blockpos.z();
            BlockPos searchArea = new BlockPos(xpos, ypos, zpos);
            AABB box = HelperMethods.createBoxAtBlock(searchArea, 3);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                    target = le;
                    break;
                }
            }

            if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationIceCometLeap, player)) {
                player.setDeltaMovement(player.getLookAngle().reverse().scale(+leapVelocity));
                player.setDeltaMovement(player.getDeltaMovement().x, leapHeight, player.getDeltaMovement().z);
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, leapSlowfallDuration, 0, false, false, true));
                player.hurtMarked = true;
            }
            if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationIceCometVolley, player))
                player.addEffect(new MobEffectInstance(EffectRegistry.FROSTVOLLEY,
                        volleyDuration, volleyStacks, false, false, true));
            if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationIceCometDamageThree, player))
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        "simplyskills:ice_comet_large_three",
                        3, target, searchArea);
            else if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationIceCometDamageTwo, player))
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        "simplyskills:ice_comet_large_two",
                        3, target, searchArea);
            else if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationIceCometDamageOne, player))
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        "simplyskills:ice_comet_large",
                        3, target, searchArea);
            else {
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        "simplyskills:ice_comet",
                        3, target, searchArea);
            }
        }
        if (blockpos != null || target != null)
            success = true;
        return success;
    }

    // Static Discharge
    public static boolean signatureWizardStaticDischarge(String wizardSkillTree, Player player) {
        Vec3 blockpos = null;
        boolean success = false;
        int amplifier = SimplySkills.wizardConfig.signatureWizardStaticDischargeBaseLeaps - 1;
        int leapsPerTier = SimplySkills.wizardConfig.signatureWizardStaticDischargeLeapsPerTier;
        int staticDischargeRange = SimplySkills.wizardConfig.signatureWizardStaticDischargeRange;
        int staticChargeDuration = SimplySkills.wizardConfig.signatureWizardStaticChargeDuration;

        if (HelperMethods.isUnlocked(wizardSkillTree,
                SkillReferencePosition.wizardSpecialisationStaticDischargeLeapThree, player))
            amplifier = amplifier + (leapsPerTier * 2);
        else if (HelperMethods.isUnlocked(wizardSkillTree,
                SkillReferencePosition.wizardSpecialisationStaticDischargeLeapTwo, player))
            amplifier = amplifier + leapsPerTier;

        if (HelperMethods.getTargetedEntity(player, staticDischargeRange) !=null)
            blockpos = HelperMethods.getTargetedEntity(player, staticDischargeRange).position();

        if (blockpos == null)
            blockpos = HelperMethods.getPositionLookingAt(player, staticDischargeRange);

        if (HelperMethods.isUnlocked(wizardSkillTree,
                SkillReferencePosition.wizardSpecialisationStaticDischargeLightningBall, player)) {
            SignatureAbilities.castSpellEngineIndirectTarget(player,
                    "simplyskills:lightning_ball",
                    3, null, null);
            success = true;
        } else {

            if (blockpos != null) {
                int xpos = (int) blockpos.x();
                int ypos = (int) blockpos.y();
                int zpos = (int) blockpos.z();
                BlockPos searchArea = new BlockPos(xpos, ypos, zpos);
                AABB box = HelperMethods.createBoxAtBlock(searchArea, 3);
                for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                            success = true;
                            SignatureAbilities.castSpellEngineIndirectTarget(player,
                                    "simplyskills:static_discharge",
                                    3, le, searchArea);
                            if (HelperMethods.isUnlocked(wizardSkillTree,
                                    SkillReferencePosition.wizardSpecialisationStaticDischargeLeap, player)) {
                                le.addEffect(new MobEffectInstance(EffectRegistry.STATICCHARGE,
                                        staticChargeDuration, amplifier, false, false, true));
                            }
                            StaticChargeEffect.onHitEffects(player, StaticChargeEffect.calculateSpeedChance(player), le);

                            break;
                        }
                    }
                }
            }
        }

        return success;
    }

    // Static Discharge Lightning Ball
    public static void signatureWizardStaticDischargeBall(ServerPlayer player, SpellProjectile spellProjectile,
                                                          ResourceLocation spellId, SpellExecution.ImpactContext context,
                                                          Spell.ProjectileData.Perks perks) {

        if (player != null && spellProjectile.tickCount % 5 == 0 && spellProjectile.tickCount > 5) {
            if (spellId.toString().contains("lightning_ball") || spellId.toString().contains("lightning_lesser")) {

                if (HelperMethods.isUnlocked("simplyskills:wizard",
                        SkillReferencePosition.wizardSpecialisationStaticDischargeLightningBall, player)) {

                    Vec3 position = spellProjectile.position();
                    if (!spellId.toString().contains("ball_homing")) {
                        perks.pierce = 132;
                        SpellProjectile projectile = new SpellProjectile(spellProjectile.level(),
                                (LivingEntity) spellProjectile.getOwner(), position.x(), position.y(), position.z(),
                                spellProjectile.getBehaviour(), SpellRegistry.from(player.level()).getHolder(ResourceLocation.parse("simplyskills:lightning_lesser")).orElseThrow(),
                                context, perks.copy());

                        projectile.setDeltaMovement(spellProjectile.getDeltaMovement().scale(5));
                        projectile.range = spellProjectile.range;
                        ProjectileUtil.rotateTowardsMovement(projectile, 0.2F);

                        int radius = 5;
                        List<Entity> targets = new ArrayList<Entity>();
                        AABB box = new AABB(spellProjectile.getX() + radius, spellProjectile.getY() + (float) radius / 2, spellProjectile.getZ() + radius,
                                spellProjectile.getX() - radius, spellProjectile.getY() - (float) radius / 2, spellProjectile.getZ() - radius);

                        for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                            if (entities != null && player.getRandom().nextInt(100) < 5) {
                                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                                    projectile.setFollowedTarget(le);
                                    spellProjectile.level().addFreshEntity(projectile);
                                    targets.add(le);
                                    AbilityLogic.onSpellCastEffects(player, targets, spellId, null);
                                    StaticChargeEffect.onHitEffects(player, StaticChargeEffect.calculateSpeedChance(player), le);
                                    targets.clear();
                                    break;

                                }
                            }
                        }
                    }
                }

                if (HelperMethods.isUnlocked("simplyskills:wizard",
                        SkillReferencePosition.wizardSpecialisationStaticDischargeLightningOrb, player)
                        && spellId.toString().contains("lightning_ball")) {

                    spellProjectile.setFollowedTarget(player);
                    spellProjectile.range = 512;
                    spellProjectile.setXRot(90);
                }

            }
        }
    }

    public static void signatureWizardLightningOrb(SpellProjectile spellProjectile, Entity followedTarget, ResourceLocation spellId) {

        if (spellId != null) {
            if (spellId.toString().equals("simplyskills:lightning_ball_homing") && spellProjectile.tickCount % 20 == 0 && followedTarget !=null) {
                if (spellProjectile.distanceTo(followedTarget) > 10) {
                    spellProjectile.teleportTo(followedTarget.getX(), followedTarget.getY(), followedTarget.getZ());
                    spellProjectile.setXRot(90);
                    spellProjectile.hurtMarked = true;
                }
            }
        }

    }

    public static void signatureWizardLightningOrbBuff(Player player) {
        int radius = SimplySkills.wizardConfig.signatureWizardLightningOrbBuffRadius;
        int frequency = SimplySkills.wizardConfig.signatureWizardLightningOrbBuffFrequency;
        int count = 0;
        AABB box = new AABB(player.getX() + radius, player.getY() + (float) radius * 3, player.getZ() + radius,
                player.getX() - radius, player.getY() - (float) radius * 3, player.getZ() - radius);
        if (player.tickCount % frequency == 0) {
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.ENTITY_STILL_ALIVE)) {
                if (entities != null && player.getRandom().nextInt(100) < SimplySkills.wizardConfig.signatureWizardLightningOrbBuffChance) {
                    if ((entities instanceof SpellProjectile spe) && spe.getOwner() != null) {
                        if (spe.getOwner() == player)
                            count ++;
                    }
                }
            }
            if (count > 0)
                HelperMethods.incrementStatusEffect(player, EffectRegistry.SOULSHOCK,
                        frequency + 5, count, count);
        }
    }


    // Arcane Bolt
    public static boolean signatureWizardArcaneBolt(String wizardSkillTree, Player player) {
        boolean success = false;
        Entity target = null;
        int volleyDuration = SimplySkills.wizardConfig.signatureWizardArcaneBoltVolleyDuration;
        int volleyStacks = SimplySkills.wizardConfig.signatureWizardArcaneBoltVolleyStacks - 1;
        int arcaneBoltRange = SimplySkills.wizardConfig.signatureWizardArcaneBoltRange;
        int radius = 3;
        if (HelperMethods.isUnlocked(wizardSkillTree,
                SkillReferencePosition.wizardSpecialisationArcaneBoltLesser, player)) {
            radius = SimplySkills.wizardConfig.signatureWizardLesserArcaneBoltRadius;
        }

        target = HelperMethods.getTargetedEntity(player, arcaneBoltRange);

        BlockPos searchArea = HelperMethods.getBlockLookingAt(player, 512);
        if (((target instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) || target == null) {

            if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationArcaneBoltVolley, player))
                player.addEffect(new MobEffectInstance(EffectRegistry.ARCANEVOLLEY,
                        volleyDuration, volleyStacks, false, false, true));

            if (HelperMethods.isUnlocked(wizardSkillTree,
                    SkillReferencePosition.wizardSpecialisationArcaneBoltLesser, player)) {
                SignatureAbilities.castSpellEngineIndirectTarget(player,
                        "simplyskills:arcane_bolt_expanding",
                        radius, target, searchArea);
            } else {
                if (HelperMethods.isUnlocked(wizardSkillTree,
                        SkillReferencePosition.wizardSpecialisationArcaneBoltGreater, player))
                    SignatureAbilities.castSpellEngineIndirectTarget(player,
                            "simplyskills:arcane_bolt_greater",
                            radius, target, searchArea);
                else
                    SignatureAbilities.castSpellEngineIndirectTarget(player,
                            "simplyskills:arcane_bolt",
                            radius, target, searchArea);
            }
            success = true;
        }
        return success;
    }
}
