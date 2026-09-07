package net.sweenus.simplyskills.abilities;

import net.spell_engine.Platform;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.utils.AnimationHelper;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.entities.GreaterDreadglareEntity;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.EntityRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NecromancerAbilities {

    public static void effectNecromancerWinterborn(Player player) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationWinterborn, player)) {
            double frostSpellPower = SpellPower.getSpellPower(SpellSchools.FROST, player).baseValue();
            int soulShockStacks = (int) frostSpellPower;
            if (soulShockStacks > 0)
                player.addEffect(new MobEffectInstance(EffectRegistry.SOULSHOCK, 220, soulShockStacks - 1, false ,false, false));
        }
    }

    public static void effectNecromancerEnrage(LivingEntity livingEntity,Player player) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationEnrage, player)) {
            AABB box = HelperMethods.createBoxHeight(livingEntity, 15);
            for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null) {
                    if ((entities instanceof OwnableEntity te) && !HelperMethods.checkFriendlyFire((LivingEntity) te, player)) {
                        HelperMethods.incrementStatusEffect((LivingEntity) te, MobEffects.DAMAGE_BOOST, 200, 1, 3);
                        HelperMethods.incrementStatusEffect((LivingEntity) te, MobEffects.DAMAGE_RESISTANCE, 200, 1, 3);
                    }
                }
            }
            player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_VOICE_20,
                    SoundSource.PLAYERS, 0.2f, 1.2f);
        }
    }

    public static void effectNecromancerDeathEssence(Player player) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationDeathEssence, player)) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.BONEARMOR, 400, 1, 25);
        }
    }

    public static void effectDeathWarden(Player player) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationDeathWarden, player)) {
            boolean success = false;
            AABB box = HelperMethods.createBoxHeight(player, 15);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (entities != null) {
                    if ((entities instanceof OwnableEntity te) && te.getOwner() != null && te.getOwner().equals(player)) {
                        float healAmount = (float) (player.getMaxHealth() * 0.15);
                        player.heal(healAmount);
                        entities.hurt(player.damageSources().generic(), healAmount);
                        HelperMethods.spawnWaistHeightParticles((ServerLevel) player.level(), ParticleTypes.POOF, player, entities, 20);
                        success = true;
                    }
                }
            }
            if (success)
                player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_VOICE_20,
                        SoundSource.PLAYERS, 0.2f, 1.3f);
        }
    }

    public static void effectPlague(Player player) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationPlague, player) && player.tickCount % 20 == 0
                && player.getActiveEffects() != null && HelperMethods.hasHarmfulStatusEffect(player)) {
            AABB box = HelperMethods.createBoxHeight(player, 15);
            List<Entity> validEntities = player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)
                    .stream()
                    .filter(entities -> entities instanceof TamableAnimal)
                    .filter(entities -> {
                        TamableAnimal te = (TamableAnimal) entities;
                        return te.getOwner() != null && te.getOwner().equals(player);
                    })
                    .toList();

            if (!validEntities.isEmpty()) {
                // Choose a random entity from the list
                Entity randomEntity = validEntities.get(new Random().nextInt(validEntities.size()));
                HelperMethods.buffSteal((LivingEntity) randomEntity, player, true, true, true, false);
                HelperMethods.spawnWaistHeightParticles((ServerLevel) player.level(), ParticleTypes.EFFECT, player, randomEntity, 12);
                player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_03,
                        SoundSource.PLAYERS, 0.1f, 1.5f);
            }
        }
    }

    public static void effectPestilence(Player player, LivingEntity minion, LivingEntity target) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationPestilence, player)) {
            HelperMethods.buffSteal(target, minion, true, true, true, false);
        }
    }

    public static void effectDelightfulSuffering(Player player) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationDelightfulSuffering, player)) {

            int duration = 800;
            List<MobEffectInstance> list = new ArrayList<>();
            list.add(0, new MobEffectInstance(MobEffects.HUNGER, duration, 0, false, false, true));
            list.add(1, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0, false, false, true));
            list.add(2, new MobEffectInstance(MobEffects.WITHER, duration, 0, false, false, true));
            list.add(3, new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, 0, false, false, true));
            list.add(4, new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, 0, false, false, true));
            if (!list.isEmpty()) {
                var chosenEffect = list.get(player.getRandom().nextInt(4)).getEffect();
                HelperMethods.incrementStatusEffect(player, chosenEffect, duration, 1, 2);
            }
        }
    }

    public static void effectEndlessServitude(Player player, TamableAnimal minion) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationEndlessServitude, player)) {
            int chanceThreshold = Math.min(20 + HelperMethods.countHarmfulStatusEffects(minion) * 5, 60);

            int chance = minion.getRandom().nextInt(100);
            if (chance < chanceThreshold) {
                EntityType<?> entityType = minion.getType();
                summonMinion((EntityType<? extends LivingEntity>) entityType, player);
                player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_02,
                        SoundSource.PLAYERS, 0.2f, 1.0f);
            }
        }
    }

    public static void effectShadowCombust(Player player, TamableAnimal minion) {
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationShadowCombust, player)) {

            int radius = 4;
            if (minion instanceof GreaterDreadglareEntity)
                radius = 7;
            AABB box = HelperMethods.createBox(minion, radius);

            minion.level().getEntities(minion, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                    .filter(Objects::nonNull)
                    .filter(entity -> entity instanceof LivingEntity)
                    .forEach(entity -> {
                        LivingEntity le = (LivingEntity) entity;
                        if (player != null && HelperMethods.checkFriendlyFireAOE(le, player)) {
                            le.invulnerableTime = 0;
                            float damageMulti = 3.2f;
                            if (minion instanceof GreaterDreadglareEntity)
                                damageMulti = 6.4f;
                            le.hurt(player.level().damageSources().indirectMagic(player, player),
                                    (float) SpellPower.getSpellPower(SpellSchools.SOUL, player).baseValue() * damageMulti);
                            HelperMethods.spawnWaistHeightParticles((ServerLevel) minion.level(), ParticleTypes.SMOKE, minion, le, 8);
                            le.invulnerableTime = 0;
                        }
                    });
            if (player != null) {
                player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_03,
                        SoundSource.PLAYERS, 0.1f, 1.0f);
                player.level().playSound(null, player, SoundEvents.GENERIC_EXPLODE.value(),
                        SoundSource.PLAYERS, 0.1f, 1.0f);
            }
            HelperMethods.spawnOrbitParticles((ServerLevel) minion.level(), minion.position(), ParticleTypes.EXPLOSION, 1, 2);
            HelperMethods.spawnOrbitParticles((ServerLevel) minion.level(), minion.position(), ParticleTypes.SOUL, 2, 20);
            HelperMethods.spawnOrbitParticles((ServerLevel) minion.level(), minion.position(), ParticleTypes.SMOKE, radius, 20);
            if (minion.isAlive())
                minion.hurt(minion.level().damageSources().indirectMagic(minion, minion), minion.getMaxHealth());
        }
    }

    //------- SIGNATURE ABILITIES --------

    public static int getMinionLimit(String necromancerTree, Player player) {
        int count = 0;
        for (String skillRef : SkillReferencePosition.undeadLegionSkills) {
            if (HelperMethods.isUnlocked(necromancerTree, skillRef, player)) {
                count++;
            }
        }
        return 1 + count; // Add 1 for default summon count
    }

    // Summoning Ritual
    public static boolean signatureNecromancerSummoningRitual(String necromancerTree, Player player) {
        if (!player.level().isClientSide()) {
            // Animate the ritual once, rather than replaying for every summoned minion.
            AnimationHelper.sendAnimation(player, Platform.tracking(player), SpellCast.Animation.RELEASE,
                    PlayerAnimation.of("spell_engine:dual_handed_ground_release"), 1.0F);
        }
        for (int i = 0; i < getMinionLimit(necromancerTree, player); i ++) {

            if (HelperMethods.isUnlocked(necromancerTree, SkillReferencePosition.necromancerSpecialisationGreaterDreadglare, player)) {
                summonMinion(EntityRegistry.GREATER_DREADGLARE, player);
                break;
            }
            else if (HelperMethods.isUnlocked(necromancerTree, SkillReferencePosition.necromancerSpecialisationSummonWraith, player)) {
                int chance = player.getRandom().nextInt(100);

                if (HelperMethods.isUnlocked("simplyskills:necromancer",
                        SkillReferencePosition.necromancerSpecialisationWraithLegion, player))
                    chance = 5;

                if (chance < 50)
                    summonMinion(EntityRegistry.WRAITH, player);
                else
                    summonMinion(EntityRegistry.DREADGLARE, player);
            } else {
                summonMinion(EntityRegistry.DREADGLARE, player);
            }
        }
        player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_VOICE_20,
                SoundSource.PLAYERS, 0.3f, 1.0f);
        return true;
    }

    public static void summonMinion(EntityType<? extends LivingEntity> livingEntity, Player player) {
        LivingEntity minion = livingEntity.spawn((ServerLevel) player.level(),
                player.blockPosition().above(4).relative(player.getMotionDirection(), 3),
                MobSpawnType.MOB_SUMMONED);

        if (minion != null) {
            if (minion instanceof TamableAnimal tameableMinion) {
                tameableMinion.tame(player);
                tameableMinion.setTame(true, true);
                tameableMinion.restrictTo(player.blockPosition().above(3), 32);
                if (HelperMethods.isUnlocked("simplyskills:necromancer",
                        SkillReferencePosition.necromancerSpecialisationShadowAura, player)) {
                    int amplifier = 1;
                    if (tameableMinion instanceof GreaterDreadglareEntity)
                        amplifier = 3;
                    minion.addEffect(new MobEffectInstance(EffectRegistry.SHADOWAURA, 2400, amplifier, false, false, false));
                }
            }

            double attackDamageMultiplier = 1.2;
            if (minion instanceof GreaterDreadglareEntity)
                attackDamageMultiplier = 3.0;
            double healthMultiplier = getHealthMultiplier(livingEntity);
            setMinionAttributes(player, minion, attackDamageMultiplier, healthMultiplier);
        }
    }

    private static double getHealthMultiplier(EntityType<?> entityType) {
        if (entityType.equals(EntityRegistry.DREADGLARE)) {
            return 1.4;
        } else if (entityType.equals(EntityRegistry.WRAITH)) {
            return 0.8;
        } else if (entityType.equals(EntityRegistry.GREATER_DREADGLARE)) {
            return 4.8;
    }
        return 1.0;
    }

    private static void setMinionAttributes(Player player, LivingEntity minion, double attackDamageMultiplier, double healthMultiplier) {
        double attackDamage = 3 + (attackDamageMultiplier * SpellPower.getSpellPower(SpellSchools.SOUL, player).baseValue());
        AttributeInstance attackAttribute = minion.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.setBaseValue(attackDamage);
        }

        double maxHealth = 1 + (healthMultiplier * player.getAttributeValue(Attributes.MAX_HEALTH));
        AttributeInstance healthAttribute = minion.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(maxHealth);
            minion.heal((float)maxHealth);
        }

        // Necrotic Fortification
        if (HelperMethods.isUnlocked("simplyskills:necromancer",
                SkillReferencePosition.necromancerSpecialisationNecroticFortification, player)) {
            double multiplier = 0.5;
            if (minion instanceof GreaterDreadglareEntity)
                multiplier = 1.0;
            double maxArmor = multiplier * player.getAttributeValue(Attributes.ARMOR);
            double maxArmorToughness = multiplier * player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
            AttributeInstance armorAttribute = minion.getAttribute(Attributes.ARMOR);
            AttributeInstance armorToughnessAttribute = minion.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorAttribute != null) {
                armorAttribute.setBaseValue(maxArmor);
            }
            if (armorToughnessAttribute != null) {
                armorToughnessAttribute.setBaseValue(maxArmorToughness);
            }
        }
    }

}
