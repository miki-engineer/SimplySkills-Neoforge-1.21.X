package net.sweenus.simplyskills.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClericAbilities {

    // ------ PASSIVES ------

    // Healing Ward
    //Chance when casting a healing spell to grant your target a stack of barrier
    public static void passiveClericHealingWard(Player player, List<Entity> targets, ResourceLocation spellId) {
        int random = new Random().nextInt(100);
        int chance = 10;
        Spell spell = SpellRegistry.from(player.level()).get(spellId);
        SpellSchool healingSchool = SpellSchools.HEALING;
        if (random < chance) {
            targets.forEach(target -> {
                if (target instanceof LivingEntity livingTarget && spell.school == healingSchool) {
                    HelperMethods.incrementStatusEffect(livingTarget, EffectRegistry.BARRIER, 100, 1, 20);
                }
            });
        }
    }

    // Mutual Mending
    // Chance when casting a healing spell to also cast the spell on yourself
    public static void passiveClericMutualMending(Player player, ResourceLocation spellId, List<Entity> targets) {
        int random = new Random().nextInt(100);
        int chance = 20;
        if (spellId.toString().contains("holy_beam"))
            chance = 10;
        Spell spell = SpellRegistry.from(player.level()).get(spellId);
        SpellSchool healingSchool = SpellSchools.HEALING;
        if (random < chance && !targets.contains(player) && spell != null && spell.school == healingSchool) {
            if (spellId.toString().contains("holy_beam"))
                SignatureAbilities.castSpellEngineIndirectTarget(player, "paladins:heal", 10, player, null);
            else SignatureAbilities.castSpellEngineIndirectTarget(player, spellId.toString(), 10, player, null);
        }
    }

    // Altruism
    // When wearing less than 10 points of armor, you periodically generate Spellforged stacks
    public static void passiveClericAltruism(Player player) {
        int frequency = 600;
        if (player.getArmorValue() <= 10 && player.tickCount %frequency == 0) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.SPELLFORGED, frequency+5, 1, 2);
        }
    }


    //------- SIGNATURE ABILITIES --------

    // Divine Intervention
    //Call down celestial energy on a ally in the targeted area, granting them Undying for 12s
    public static boolean signatureClericDivineIntervention(String clericSkillTree, Player player) {
        Vec3 blockpos = null;
        boolean success = false;
        int divineInterventionRange = 25;
        if (HelperMethods.getTargetedEntity(player, divineInterventionRange) != null)
            blockpos = HelperMethods.getTargetedEntity(player, divineInterventionRange).position();

        if (blockpos == null)
            blockpos = HelperMethods.getPositionLookingAt(player, divineInterventionRange);

        if (blockpos != null) {
            int xpos = (int) blockpos.x();
            int ypos = (int) blockpos.y();
            int zpos = (int) blockpos.z();
            BlockPos searchArea = new BlockPos(xpos, ypos, zpos);
            AABB box = HelperMethods.createBoxAtBlock(searchArea, 3);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && !HelperMethods.checkFriendlyFireAOE(le, player)) {
                        success = true;

                        // Grants recipient Fire Resistance
                        if (HelperMethods.isUnlocked(clericSkillTree, SkillReferencePosition.clericSpecialisationDivineInterventionFireResistance, player))
                            HelperMethods.incrementStatusEffect(le, MobEffects.FIRE_RESISTANCE, 240, 1, 5);

                        // Grants recipient Might
                        if (HelperMethods.isUnlocked(clericSkillTree, SkillReferencePosition.clericSpecialisationDivineInterventionMight, player))
                            HelperMethods.incrementStatusEffect(le, EffectRegistry.MIGHT, 240, 3, 10);

                        // Grants recipient Spellforged
                        if (HelperMethods.isUnlocked(clericSkillTree, SkillReferencePosition.clericSpecialisationDivineInterventionSpellforged, player))
                            HelperMethods.incrementStatusEffect(le, EffectRegistry.SPELLFORGED, 240, 3, 10);

                        SignatureAbilities.castSpellEngineIndirectTarget(player,
                                "simplyskills:divine_intervention",
                                15, le, HelperMethods.getBlockLookingAt(player, 256));
                        break;
                    }
                }
            }
        }
        return success;
    }
    // Sacred Orb
    public static boolean signatureClericSacredOrb(String clericSkillTree, Player player) {
        SignatureAbilities.castSpellEngineDumbFire(player, "simplyskills:sacred_orb");
        return true;
    }
    public static void signatureClericSacredOrbHoming(SpellProjectile spellProjectile, ResourceLocation spellId) {
        if (spellProjectile.getSpellEntry() != null && spellId != null && spellId.toString().equals("simplyskills:sacred_orb") && spellProjectile.tickCount > 20 && spellProjectile.getFollowedTarget() == null) {
            AABB box = HelperMethods.createBox(spellProjectile, 6);
            for (Entity entities : spellProjectile.level().getEntities(spellProjectile, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities instanceof LivingEntity le && spellProjectile.getOwner() instanceof Player playerOwner && !HelperMethods.checkFriendlyFireAOE(le, playerOwner)) {
                    spellProjectile.setFollowedTarget(le);
                    break;
                }
            }
        }
    }
    public static void signatureClericSacredOrbImpact(EntityHitResult entityHitResult, ResourceLocation spellId, Entity ownerEntity, SpellProjectile spellProjectile) {
        if (spellProjectile.getSpellEntry() != null && spellId != null && spellId.toString().equals("simplyskills:sacred_orb") && entityHitResult.getEntity() != null
                && entityHitResult.getEntity() instanceof LivingEntity livingEntity && ownerEntity instanceof LivingEntity livingOwner) {

            SimplyStatusEffectInstance vitalityBond = new SimplyStatusEffectInstance(EffectRegistry.VITALITYBOND, 500, 0, false, false, true);
            SimplyStatusEffectInstance vitalityBond2 = new SimplyStatusEffectInstance(EffectRegistry.VITALITYBOND, 500, 0, false, false, true);
            vitalityBond.setSourceEntity(livingOwner);
            vitalityBond2.setSourceEntity(livingOwner);
            livingEntity.addEffect(vitalityBond);
            livingOwner.addEffect(vitalityBond2);

        }
    }

    // Anoint Weapon
    public static boolean signatureClericAnointWeapon(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.ANOINTED, 400, 0, false, false, true));
        SignatureAbilities.playCastingGesture(player, "spell_engine:one_handed_healing_release");
        SignatureAbilities.playBuffParticles(player, net.spell_engine.fx.SpellEngineParticles.magic_holy);
        return true;
    }
    // Cleanse tick
    public static void signatureClericAnointWeaponCleanse(Player player) {
        int frequency = 20;
        if (player.tickCount %frequency == 0) {
            HelperMethods.buffSteal(player, player, true, true, true, true);
        }
    }
    // Undying on damaged
    public static void signatureClericAnointWeaponUndying(Player player) {
        float playerHealthPercent = ((player.getHealth() / player.getMaxHealth()) * 100);
        int roll = player.getRandom().nextInt(100);
        int chance = 15;

        if (playerHealthPercent < 30 && roll < chance)
            player.addEffect(new MobEffectInstance(EffectRegistry.UNDYING, 120, 0, false, false, true));

    }

    public static void signatureClericAnointWeaponEffect(Player player) {
        int radius = 4;
        float damageMultiplier = 2.2f;

        AABB box = HelperMethods.createBox(player, radius);
        List<Entity> targets = new ArrayList<>();
        List<Entity> hostileTargets = new ArrayList<>();
        for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
            if (entities instanceof LivingEntity le) {
                if (!HelperMethods.checkFriendlyFireAOE(le, player))
                    targets.add(le);
                else if (HelperMethods.checkFriendlyFireAOE(le, player))
                    hostileTargets.add(le);
            }
        }
        ResourceLocation spellId = ResourceLocation.parse("simplyskills:paladins_flash_heal");
        DamageSource damageSource = player.damageSources().indirectMagic(player, player);
        SignatureAbilities.castSpellEngineIndirectTargets(player, spellId.toString(), targets);

        if (!hostileTargets.isEmpty()) {
            float amount = (float) (SpellPower.getSpellPower(SpellSchools.HEALING, player).randomValue() * damageMultiplier) / hostileTargets.size();

            hostileTargets.forEach(entity -> {
                entity.invulnerableTime = 0;
                entity.hurt(damageSource, amount);
                entity.invulnerableTime = 0;

                if (entity instanceof Mob mobEntity && mobEntity.isInvertedHealAndHarm())
                    HelperMethods.incrementStatusEffect(mobEntity, MobEffects.MOVEMENT_SLOWDOWN, 40, 1, 4);

                for (int i = 6; i > 0; i--) {
                    HelperMethods.spawnParticle(player.level(), SpellEngineParticles.magic_holy.type(),
                            entity.getX(), entity.getY(), entity.getZ(), 0.1, 0.1+i, 0.2);
                    HelperMethods.spawnParticle(player.level(), SpellEngineParticles.magic_holy.type(),
                            entity.getX(), entity.getY(), entity.getZ(), 0.2, 0.2+i, 0.1);
                    HelperMethods.spawnParticle(player.level(), SpellEngineParticles.magic_holy.type(),
                            entity.getX(), entity.getY(), entity.getZ(), 0.1, 0.2*i, 0.2);
                }
            });
        }

        // Grants player Resistance
        if (HelperMethods.isUnlocked("simplyskills:cleric", SkillReferencePosition.clericSpecialisationAnointWeaponResistance, player))
            HelperMethods.incrementStatusEffect(player, MobEffects.DAMAGE_RESISTANCE, 40, 1, 2);

    }


}
