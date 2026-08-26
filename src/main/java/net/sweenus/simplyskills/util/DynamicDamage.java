package net.sweenus.simplyskills.util;

import net.minecraft.network.chat.Component;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.puffish.skillsmod.api.SkillsAPI;
import net.sweenus.simplyskills.SimplySkills;

import java.util.List;
import java.util.UUID;

public class DynamicDamage {


    public static float dynamicDamageReduction(LivingEntity attacker, LivingEntity target, float amount, float lastDamageTaken, float damage, float lastDamageTime) {

        if (attacker != null && lastDamageTaken > 0 && SimplySkills.generalConfig.enableDDR) {
            if (!(target instanceof Player) || SimplySkills.generalConfig.DDRAffectsPlayers) {
                Level world = attacker.level();
                float maxHp = target.getMaxHealth();
                float thresholdCheck = amount / maxHp;
                float ddrAttackSpeedWeight = (float) SimplySkills.generalConfig.DDRAttackSpeedWeight / 100;
                float ddrAmount = (float) SimplySkills.generalConfig.DDRAmount / 100;
                float ddrHealthThreshold = (float) SimplySkills.generalConfig.DDRHealthThreshold / 100;
                float damageFrequency = 0.01f + (world.getGameTime() - lastDamageTime) / (damage * ddrAttackSpeedWeight);
                float healthPercent = Math.min((damage / maxHp) * damage, 0.9f * damage);
                float minimumHp = (float) SimplySkills.generalConfig.DDRHealthRequirement;

                if (thresholdCheck > ddrHealthThreshold && maxHp >= minimumHp) {
                    float damageReduction = Math.min((healthPercent * ddrAmount), (damage / 2));
                    float newAmount = Math.max((damage - damageReduction), 1) * Math.max(Math.min(damageFrequency, 1.0f), 0.3f);
                    if (SimplySkills.generalConfig.enableDDRDebugLog && attacker instanceof Player)
                        attacker.sendSystemMessage(Component.literal("§fDamage reduced from §6" + damage + " §fto§a " + newAmount + " §fusing DR: §6" + damageReduction + "§f & SDR: §b" + damageFrequency));
                    //System.out.println("Damage reduced from " + damage + " to " + newAmount + " using DR: " + damageReduction + " & SDR: " + damageFrequency);
                    return newAmount;
                }
            }
        }
        return amount;
    }

    public static void dynamicAttributeScaling(LivingEntity entity, Holder<Attribute> attribute, String name, double amount, UUID uuid) {
        if (entity.getLastHurtByMob() != null && (entity.getLastHurtByMob() instanceof ServerPlayer player)) {
            AttributeInstance attributeInstance = entity.getAttribute(attribute);
            ResourceLocation modifierId = getModifierId(uuid);
            int pointsSpent = getSpentPoints(player);
            double totalAmount = amount * pointsSpent;
            if (attributeInstance != null && attributeInstance.getModifier(modifierId) == null) {
                AttributeModifier modifier = new AttributeModifier(modifierId, totalAmount, AttributeModifier.Operation.ADD_VALUE);
                attributeInstance.addTransientModifier(modifier);
                if (attribute.equals(Attributes.MAX_HEALTH))
                    entity.heal((float) totalAmount);
            }
        }
    }

    public static void dynamicPlayerCountScaling(LivingEntity livingEntity) {
        double checkDistance = SimplySkills.generalConfig.DASRadius;

        // Collect nearby players
        List<ServerPlayer> nearbyPlayers = livingEntity.level().getEntitiesOfClass(
                ServerPlayer.class,
                livingEntity.getBoundingBox().inflate(checkDistance),
                playerEntity -> true
        );

        int totalPointsSpent = 0;
        int totalPlayerCount = 0;
        float entityTypeModifier = 1.0f;
        if (livingEntity instanceof AgeableMob
                || livingEntity instanceof AbstractSchoolingFish
                || livingEntity instanceof Villager
                || livingEntity instanceof ArmorStand)
            entityTypeModifier = SimplySkills.generalConfig.DASPassiveEntityModifier;

        // Iterate through each player and accumulate the total points spent
        for (ServerPlayer player : nearbyPlayers) {
            int pointsSpent = getSpentPoints(player);
            if (!SimplySkills.generalConfig.DASScaleWithPointsSpent) {
                pointsSpent = getUnspentPoints(player) + getSpentPoints(player);
            }

            totalPlayerCount++;
            //The lower the Player Scaling Weight number, the less impact each successive player has on scaling
            double playerCountDampener = totalPointsSpent * ((double) totalPlayerCount / SimplySkills.generalConfig.DASPlayerScalingWeight);
            totalPointsSpent += (int) Math.max(((pointsSpent - playerCountDampener) * entityTypeModifier), 0);

            if (SimplySkills.generalConfig.enableDASDebugLog) {
                String message = "§fScaling §6" + livingEntity.getName().getString() +
                        " §fusing§a " + player.getName().getString() +
                        " §fspent skill points of§6 " + getSpentPoints(player) +
                        "§f. Total scale factor: §b" + totalPointsSpent;
                if (!SimplySkills.generalConfig.DASScaleWithPointsSpent) {
                    message = "§fScaling §6" + livingEntity.getName().getString() +
                            " §fusing§a " + player.getName().getString() +
                            " §ftotal skill points of§6 " + (getSpentPoints(player) + getUnspentPoints(player)) +
                            "§f. Total scale factor: §b" + totalPointsSpent;
                }
                player.sendSystemMessage(Component.literal(message));
            }

        }

        if (SimplySkills.generalConfig.enableDAS && !(livingEntity instanceof Player)) {
            if (SimplySkills.generalConfig.enableDASDebugLog) {
                String message = "Attempting to apply DAS attributes to " + livingEntity.getName().getString() + ". Player Count: " + totalPlayerCount + ". total points spent: " + totalPointsSpent;
                System.out.println(message);
            }

            float addRequirement = SimplySkills.generalConfig.addMultiHealthRequirement;
            double addHealth = 1;
            double addAttack = 1;
            double addArmor = 1;
            double addToughness = 1;
            double addSpeed = 1;
            double addResistance = 1;
            AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (attributeInstance != null) {
                double maxHealth = attributeInstance.getBaseValue();
                if (maxHealth >= addRequirement) {
                    addHealth = SimplySkills.generalConfig.DASHealthAddMulti;
                    addAttack = SimplySkills.generalConfig.DASAttackAddMulti;
                    addArmor = SimplySkills.generalConfig.DASArmorAddMulti;
                    addToughness = SimplySkills.generalConfig.DASToughnessAddMulti;
                    addSpeed = SimplySkills.generalConfig.DASSpeedAddMulti;
                    addResistance = SimplySkills.generalConfig.DASResistAddMulti;
                }
            }

            if (SimplySkills.generalConfig.DASHealth > 0)
                DynamicDamage.dynamicTotalAttributeScaling(livingEntity, Attributes.MAX_HEALTH, "SimplySkills health DAS",
                        SimplySkills.generalConfig.DASHealth, UUID.fromString("631937f6-bc47-486b-b07a-542823d668a6"), totalPointsSpent * addHealth);
            if (SimplySkills.generalConfig.DASAttack > 0)
                DynamicDamage.dynamicTotalAttributeScaling(livingEntity, Attributes.ATTACK_DAMAGE, "SimplySkills attack damage DAS",
                        SimplySkills.generalConfig.DASAttack, UUID.fromString("8097403f-ed25-4534-b7f9-854e16ef2fbb"), totalPointsSpent * addAttack);
            if (SimplySkills.generalConfig.DASArmor > 0)
                DynamicDamage.dynamicTotalAttributeScaling(livingEntity, Attributes.ARMOR, "SimplySkills armor DAS",
                        SimplySkills.generalConfig.DASArmor, UUID.fromString("eae99963-45c4-4651-b501-4b2e16879705"), totalPointsSpent * addArmor);
            if (SimplySkills.generalConfig.DASArmorToughness > 0)
                DynamicDamage.dynamicTotalAttributeScaling(livingEntity, Attributes.ARMOR_TOUGHNESS, "SimplySkills armor toughness DAS",
                        SimplySkills.generalConfig.DASArmorToughness, UUID.fromString("0794ab19-7dbd-421f-90c7-6b65e3aee495"), totalPointsSpent * addToughness);
            if (SimplySkills.generalConfig.DASSpeed / 100 > 0)
                DynamicDamage.dynamicTotalAttributeScaling(livingEntity, Attributes.MOVEMENT_SPEED, "SimplySkills movement speed DAS",
                        SimplySkills.generalConfig.DASSpeed / 100, UUID.fromString("426d5ed8-020b-484f-93d9-4327a3c05c97"), totalPointsSpent * addSpeed);
            if (SimplySkills.generalConfig.DASKnockbackResist / 100 > 0)
                DynamicDamage.dynamicTotalAttributeScaling(livingEntity, Attributes.KNOCKBACK_RESISTANCE, "SimplySkills knockback resistance DAS",
                        SimplySkills.generalConfig.DASKnockbackResist / 100, UUID.fromString("9ffef649-025d-4c30-98ed-d4378cd07d36"), totalPointsSpent * addResistance);
        }

    }

    public static void dynamicTotalAttributeScaling(LivingEntity entity, Holder<Attribute> attribute, String name, double amount, UUID uuid, double pointsSpent) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        ResourceLocation modifierId = getModifierId(uuid);
        //If pointsSpent is 0 and an existing modifier is found, remove it
        if (pointsSpent == 0) {
            if (attributeInstance != null && attributeInstance.getModifier(modifierId) != null) {
                if (attribute.equals(Attributes.MAX_HEALTH)) {
                    // Remove health attribute while maintaining health to maxHealth ratio
                    float healthRatio = entity.getHealth() / entity.getMaxHealth();
                    attributeInstance.removeModifier(modifierId);
                    entity.setHealth(entity.getMaxHealth() * healthRatio);
                } else {
                    attributeInstance.removeModifier(modifierId);
                }
            }
            return;
        }

        double totalAmount = amount * pointsSpent;
        if (attributeInstance != null) {
            AttributeModifier existingModifier = attributeInstance.getModifier(modifierId);
            if (existingModifier == null) {
                // If the modifier does not exist, add a new one
                AttributeModifier newModifier = new AttributeModifier(modifierId, totalAmount, AttributeModifier.Operation.ADD_VALUE);
                attributeInstance.addTransientModifier(newModifier);
                if (attribute.equals(Attributes.MAX_HEALTH)) {
                    entity.heal((float) totalAmount);
                }
            } else if (existingModifier.amount() != totalAmount) {
                // If the modifier exists but the amount is different, update it by removing & re-adding
                attributeInstance.removeModifier(existingModifier);
                AttributeModifier updatedModifier = new AttributeModifier(modifierId, totalAmount, AttributeModifier.Operation.ADD_VALUE);
                attributeInstance.addTransientModifier(updatedModifier);
                if (attribute.equals(Attributes.MAX_HEALTH)) {
                    // Adjust health accordingly
                    double difference = totalAmount - existingModifier.amount();
                    entity.heal((float) difference);
                }
            }
        }
    }

    private static ResourceLocation getModifierId(UUID uuid) {
        return ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, uuid.toString());
    }

    public static int getSpentPoints(ServerPlayer player) {
        return SkillsAPI.streamUnlockedCategories(player)
                .mapToInt(category -> (int) category.streamUnlockedSkills(player).count())
                .sum();
    }

    public static int getUnspentPoints(ServerPlayer player) {
        return SkillsAPI.streamUnlockedCategories(player)
                .mapToInt(category -> category.getPointsLeft(player))
                .sum();
    }



}
