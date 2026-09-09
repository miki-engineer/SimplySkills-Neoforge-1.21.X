package net.sweenus.simplyskills.abilities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class WarriorAbilities {

    public static void passiveWarriorSpellbreaker(Player player) {
        int spellbreakingDuration = SimplySkills.warriorConfig.passiveWarriorSpellbreakerDuration;
        int spellbreakingChance = SimplySkills.warriorConfig.passiveWarriorSpellbreakerChance;
        int roll = player.getRandom().nextInt(100);
        if (roll < spellbreakingChance) {
            player.addEffect(new MobEffectInstance(EffectRegistry.SPELLBREAKING, spellbreakingDuration, 0, false, false, true));
        }
    }
    public static void passiveWarriorSpellbreakerOnHit(Player player) {
        int spellbreakingDuration = SimplySkills.warriorConfig.passiveWarriorSpellbreakerDuration;
        int spellbreakingChance = SimplySkills.warriorConfig.passiveWarriorSpellbreakerChance;
        int roll = player.getRandom().nextInt(100);
        if (roll < spellbreakingChance) {
            player.addEffect(new MobEffectInstance(EffectRegistry.RAGINGJAVELIN, spellbreakingDuration / 2, 0, false, false, true));
        }
    }

    public static void passiveWarriorDeathDefy(Player player) {
        int deathDefyFrequency = SimplySkills.warriorConfig.passiveWarriorDeathDefyFrequency;
        int deathDefyAmplifierPerTenPercentHealth = SimplySkills.warriorConfig.passiveWarriorDeathDefyAmplifierPerTenPercentHealth;
        int regen = 0;

        int healthThreshold = SimplySkills.warriorConfig.passiveWarriorDeathDefyHealthThreshold;
        if (player.tickCount % deathDefyFrequency == 0) {
            float playerHealthPercent = ((player.getHealth() / player.getMaxHealth()) * 100);
            if (playerHealthPercent < healthThreshold) {
                if (playerHealthPercent < (healthThreshold - 10))
                    regen = regen + deathDefyAmplifierPerTenPercentHealth;
                if (playerHealthPercent < (healthThreshold - 20))
                    regen = regen + (deathDefyAmplifierPerTenPercentHealth * 2);

                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        deathDefyFrequency + 5, regen, false, false, true));
                if (player.hasEffect(MobEffects.REGENERATION)
                        && player.getEffect(MobEffects.REGENERATION).getAmplifier() > 0)
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.EXHAUSTION,
                            deathDefyFrequency + 60, regen +1, 49);
            }
        }
    }

    public static void passiveWarriorGoliath(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.EARTHSHAKER, 200, 0, false, false, true));
    }

    public static void passiveWarriorArmorMastery(Player player) {
        int armorMasteryThreshold = SimplySkills.warriorConfig.passiveWarriorArmorMasteryArmorThreshold - 1;
        int armorMasteryChance = SimplySkills.warriorConfig.passiveWarriorArmorMasteryChance;
        int heavyArmorMasteryDuration = SimplySkills.warriorConfig.passiveWarriorHeavyArmorMasteryDuration;
        int heavyArmorMasteryAmplifier = SimplySkills.warriorConfig.passiveWarriorHeavyArmorMasteryAmplifier;
        int mediumArmorMasteryDuration = SimplySkills.warriorConfig.passiveWarriorMediumArmorMasteryDuration;
        int mediumArmorMasteryAmplifier = SimplySkills.warriorConfig.passiveWarriorMediumArmorMasteryAmplifier;

        int roll = player.getRandom().nextInt(100);
        if (roll < armorMasteryChance) {
            if (player.getArmorValue() > armorMasteryThreshold
                    && HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorHeavyArmorMastery, player)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                        heavyArmorMasteryDuration, heavyArmorMasteryAmplifier, false, false, true));
            } else if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorMediumArmorMastery, player)){
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                        mediumArmorMasteryDuration, mediumArmorMasteryAmplifier, false, false, true));
            }
        }
    }

    public static void passiveWarriorFrenzy(Player player) {
        int frenzyDuration = SimplySkills.warriorConfig.passiveWarriorFrenzyExhaustionDuration;
        int frenzyStacks = SimplySkills.warriorConfig.passiveWarriorFrenzyExhaustionStacks;

        HelperMethods.incrementStatusEffect(player, EffectRegistry.EXHAUSTION, frenzyDuration, frenzyStacks, 79);
    }

    public static void passiveWarriorCarnage(Player player) {

        int frequency = 15;
        float stableMaxHealth = player.getMaxHealth();
        MobEffectInstance healthBoost = player.getEffect(MobEffects.HEALTH_BOOST);
        if (healthBoost != null)
            stableMaxHealth -= 4 * (healthBoost.getAmplifier() + 1);
        int amount = Math.max(0, (int) stableMaxHealth / 10);

        if (player.tickCount % frequency == 0) {
            if (player.hasEffect(EffectRegistry.EXHAUSTION)) {
                if (player.getEffect(EffectRegistry.EXHAUSTION).getAmplifier() >= 74) {
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, frequency + 5, amount, false, false, true));
                    player.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZINGAURA, frequency + 5, amount, false, false, true));
                }
            }
            if (player.hasEffect(EffectRegistry.RAGE)) {
                if (player.getEffect(EffectRegistry.RAGE).getAmplifier() >= 74) {
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, frequency + 5, amount, false, false, true));
                    player.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZINGAURA, frequency + 5, amount, false, false, true));
                }
            }
        }
    }

    public static void passiveWarriorShieldMastery(Player player) {
        int shieldMasteryFrequency = SimplySkills.warriorConfig.passiveWarriorShieldMasteryFrequency;
        int shieldMasteryWeaknessAmplifier = SimplySkills.warriorConfig.passiveWarriorShieldMasteryWeaknessAmplifier;
        int shieldMasteryResistanceAmplifier = SimplySkills.warriorConfig.passiveWarriorShieldMasteryResistanceAmplifier;
        int shieldMasteryResistanceAmplifierPerTier = SimplySkills.warriorConfig.passiveWarriorShieldMasteryResistanceAmplifierPerTier;


        if (player.tickCount % shieldMasteryFrequency == 0) {
            if (player.getOffhandItem() != null) {
                if (player.getOffhandItem().getItem() instanceof ShieldItem) {

                    int mastery = shieldMasteryResistanceAmplifier;

                    if (HelperMethods.isUnlocked("simplyskills:tree",
                            SkillReferencePosition.bulwarkShieldMasterySkilled, player))
                        mastery = mastery + (shieldMasteryResistanceAmplifierPerTier * 2);
                    else if (HelperMethods.isUnlocked("simplyskills:tree",
                            SkillReferencePosition.bulwarkShieldMasteryProficient, player))
                        mastery = mastery + shieldMasteryResistanceAmplifierPerTier;

                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                            shieldMasteryFrequency + 5, mastery, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                            shieldMasteryFrequency + 5, shieldMasteryWeaknessAmplifier, false, false, true));
                }
            }
        }
    }

    public static void passiveWarriorRebuke(Player player, LivingEntity attacker) {
        int rebukeChance = SimplySkills.warriorConfig.passiveWarriorRebukeChance;
        int rebukeWeaknessDuration = SimplySkills.warriorConfig.passiveWarriorRebukeWeaknessDuration;
        int rebukeWeaknessAmplifier = SimplySkills.warriorConfig.passiveWarriorRebukeWeaknessAmplifier;
        int roll = player.getRandom().nextInt(100);
        if (roll < rebukeChance) {
            attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                    rebukeWeaknessDuration, rebukeWeaknessAmplifier, false, false, true));
        }
    }

    public static void passiveWarriorTwinstrike(Player player, LivingEntity target) {
        int effectChance = SimplySkills.warriorConfig.passiveWarriorTwinstrikeChance;
        int effectDamage = (int) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damageSource = player.damageSources().playerAttack(player);
        if (player.hasEffect(MobEffects.HEALTH_BOOST))
            effectChance = effectChance * 2;
        int roll = player.getRandom().nextInt(100);
        if (roll < effectChance) {
            target.hurt(damageSource, effectDamage);
            target.invulnerableTime = 0;
        }
    }

    public static void passiveWarriorSwordfall(Player player, LivingEntity target) {
        if (player.getMainHandItem().getItem() instanceof SwordItem || player.getMainHandItem().getItem() instanceof AxeItem) {
            int effectChance = SimplySkills.warriorConfig.passiveWarriorSwordfallChance;
            if (player.hasEffect(EffectRegistry.MIGHT))
                effectChance = effectChance * 2;
            int roll = player.getRandom().nextInt(100);
            if (roll < effectChance) {
                SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:physical_swordfall", 32, target, null);
            }
        }
    }

}
