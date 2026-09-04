package net.sweenus.simplyskills.abilities;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InitiateAbilities {

    public static void passiveInitiateNullification(Player player) {
        int nullificationFrequency = SimplySkills.initiateConfig.passiveInitiateNullificationFrequency;
        int radius = SimplySkills.initiateConfig.passiveInitiateNullificationRadius;
        if (player.tickCount % nullificationFrequency == 0) {

            AABB box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {
                        for (MobEffectInstance statusEffect : le.getActiveEffects()) {
                            if (statusEffect != null && statusEffect.getEffect().value().isBeneficial()) {
                                HelperMethods.decrementStatusEffect(le, statusEffect.getEffect());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public static void passiveInitiateEmpower(Player player, @Nullable SpellSchool school, @Nullable Set<? extends SpellSchool> schools) {
        int chance = SimplySkills.initiateConfig.passiveInitiateEmpowerChance;
        int duration = SimplySkills.initiateConfig.passiveInitiateEmpowerDuration;
        int amplifier = SimplySkills.initiateConfig.passiveInitiateEmpowerStacks;
        int amplifierMax = SimplySkills.initiateConfig.passiveInitiateEmpowerMaxStacks;
        List<Holder<MobEffect>> list = new ArrayList<>();
        if (school == SpellSchools.ARCANE || (schools != null ? schools.contains(SpellSchools.ARCANE) : false))
            list.add(EffectRegistry.ARCANEATTUNEMENT);
        if (school == SpellSchools.SOUL || (schools != null ? schools.contains(SpellSchools.SOUL) : false))
            list.add(EffectRegistry.SOULATTUNEMENT);
        if (school == SpellSchools.HEALING || (schools != null ? schools.contains(SpellSchools.HEALING) : false))
            list.add(EffectRegistry.HOLYATTUNEMENT);
        if (school == SpellSchools.FIRE || (schools != null ? schools.contains(SpellSchools.FIRE) : false))
            list.add(EffectRegistry.FIREATTUNEMENT);
        if (school == SpellSchools.FROST || (schools != null ? schools.contains(SpellSchools.FROST) : false))
            list.add(EffectRegistry.FROSTATTUNEMENT);
        if (school == SpellSchools.LIGHTNING || (schools != null ? schools.contains(SpellSchools.LIGHTNING) : false))
            list.add(EffectRegistry.LIGHTNINGATTUNEMENT);

        if (!list.isEmpty()) {

            int roll = player.getRandom().nextInt(100);
            if (roll < chance) {
                int random = player.getRandom().nextInt(list.size());
                Holder<MobEffect> chosenEffect = list.get(random);
                HelperMethods.incrementStatusEffect(player, chosenEffect, duration, amplifier, amplifierMax);
            }
        }
    }

    public static void passiveInitiateAttuned(Player player) {
        int duration = SimplySkills.initiateConfig.passiveInitiateAttunedDuration;
        int stacks = SimplySkills.initiateConfig.passiveInitiateAttunedStacks;
        int maxStacks = SimplySkills.initiateConfig.passiveInitiateAttunedMaxStacks;
        int frequency = SimplySkills.initiateConfig.passiveInitiateAttunedFrequency;
        int stackThreshold = SimplySkills.initiateConfig.passiveInitiateAttunedStackThreshold;
        if (player.tickCount % frequency != 0)
            return;

        List<Holder<MobEffect>> attunements = List.of(
                EffectRegistry.ARCANEATTUNEMENT,
                EffectRegistry.SOULATTUNEMENT,
                EffectRegistry.HOLYATTUNEMENT,
                EffectRegistry.FIREATTUNEMENT,
                EffectRegistry.FROSTATTUNEMENT,
                EffectRegistry.LIGHTNINGATTUNEMENT);
        for (Holder<MobEffect> attunement : attunements) {
            MobEffectInstance statusInstance = player.getEffect(attunement);
            if (statusInstance != null && statusInstance.getAmplifier() + 1 >= stackThreshold) {
                HelperMethods.incrementStatusEffect(player, EffectRegistry.PRECISION, duration, stacks, maxStacks);
                HelperMethods.decrementStatusEffect(player, attunement);
                break;
            }
        }
    }

    public static void passiveInitiateLightningRod(Player player) {
        int duration = SimplySkills.initiateConfig.passiveInitiateLightningRodDuration;
        int stacks = SimplySkills.initiateConfig.passiveInitiateLightningRodStacks;
        int maxStacks = SimplySkills.initiateConfig.passiveInitiateLightningRodMaxStacks;
        int frequency = SimplySkills.initiateConfig.passiveInitiateLightningRodFrequency;
        if (player.tickCount % frequency == 0 && player.level().isThundering()) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.LIGHTNINGATTUNEMENT, duration, stacks, maxStacks);
        }
    }

    public static void passiveInitiateHasty(Player player) {
        int duration = SimplySkills.initiateConfig.passiveInitiateHastyDuration;
        int stacks = SimplySkills.initiateConfig.passiveInitiateHastyStacks;
        HelperMethods.incrementStatusEffect(player, MobEffects.MOVEMENT_SLOWDOWN, duration, stacks, 4);
    }

    public static void passiveInitiateFrail(Player player) {
        int attackThreshold = SimplySkills.initiateConfig.passiveInitiateFrailAttackThreshold;
        int weaknessAmplifier = SimplySkills.initiateConfig.passiveInitiateFrailWeaknessAmplifier;
        int miningFatigueAmplifier = SimplySkills.initiateConfig.passiveInitiateFrailMiningFatigueAmplifier;

        if ((HelperMethods.getAttackDamage(player.getMainHandItem()) > attackThreshold
                || HelperMethods.getAttackDamage(player.getOffhandItem()) > attackThreshold)
                && HelperMethods.isUnlocked("simplyskills:tree",
                SkillReferencePosition.wizardPath, player)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                    25, weaknessAmplifier, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                    25, miningFatigueAmplifier, false, false, true));
        }
    }

    public static void passiveInitiateEldritchEnfeeblement(Player player, List<Entity> targets) {
        if (targets.isEmpty())
            return;
        double  critDamage = 0;
        double  critChance = 0;
        if (BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("spell_power:critical_chance")) != null)
            critChance = player.getAttributeValue(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("spell_power:critical_chance")).orElseThrow());
        if (BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("spell_power:critical_damage")) != null)
            critDamage = player.getAttributeValue(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("spell_power:critical_damage")).orElseThrow());
        critChance = Math.max(0, Math.min(100,
                critChance - SpellPowerMechanics.PERCENT_ATTRIBUTE_BASELINE));
        int roll = player.getRandom().nextInt(100);
        if (roll < critChance) {
            player.heal((float) Math.min(3, critDamage / 100));
        }
    }

    public static void passiveInitiatePerilousPrecision(Player player, List<Entity> targets) {
        if (targets.isEmpty())
            return;
        int chance = Math.max(1, 50 - (int) player.getAttributeValue(Attributes.MAX_HEALTH));
        int roll = player.getRandom().nextInt(100);
        if (roll < chance) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.BARRIER, 60, 1, 10);
        }
    }

}
