package net.sweenus.simplyskills.util;

import net.neoforged.fml.ModList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.puffish.attributesmod.AttributesMod;
import net.puffish.attributesmod.util.Sign;
import net.puffish.skillsmod.api.Category;
import net.puffish.skillsmod.api.Experience;
import net.puffish.skillsmod.api.Skill;
import net.puffish.skillsmod.api.SkillsAPI;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.NecromancerAbilities;
import net.sweenus.simplyskills.network.ModPacketHandler;
import net.sweenus.simplyskills.util.compat.opac.OpacCompat;

import java.util.*;

import static net.puffish.skillsmod.api.SkillsAPI.getCategory;

public class HelperMethods {


    // Check if we should be able to hit the target
    public static boolean checkFriendlyFire (LivingEntity livingEntity, Player player) {
        if (livingEntity == null || player == null)
            return false;
        if (!checkEntityBlacklist(livingEntity, player))
            return false;
        if (livingEntity == player)
            return false;

        // Check if the player and the living entity are on the same team
        Team playerTeam = player.getTeam();
        Team entityTeam = livingEntity.getTeam();
        if (livingEntity instanceof Player playerEntity && !player.canHarmPlayer(playerEntity))
            return false;
        if (HelperMethods.isOpacLoaded() && livingEntity instanceof Player) {
            // Is OpenPAC loaded? And are they team/ally member?
            return OpacCompat.checkOpacFriendlyFire(livingEntity, player);
        }
        if (playerTeam != null && entityTeam != null && livingEntity.isAlliedTo(player)) {
            // They are on the same team, so friendly fire should not be allowed
            return false;
        }

        if (livingEntity instanceof Player playerEntity) {
            if (playerEntity == player)
                return false;
            return playerEntity.canHarmPlayer(player);
        }
        if (livingEntity instanceof OwnableEntity tameable) {
            if (tameable.getOwner() != null) {
                if (tameable.getOwner() != player
                        && (tameable.getOwner() instanceof Player ownerPlayer)) {
                    if (!player.canHarmPlayer(ownerPlayer))
                        return false;
                    if (HelperMethods.isOpacLoaded()) {
                        // Is OpenPAC loaded? And is the pet owner a team/ally member?
                        return OpacCompat.checkOpacFriendlyFire(ownerPlayer, player);
                    }
                    return player.canHarmPlayer(ownerPlayer);
                }
                return tameable.getOwner() != player;
            }
            return true;
        }
        return true;
    }

    public static boolean checkFriendlyFireAOE(LivingEntity livingEntity, Player player) {
        if (livingEntity == null || player == null)
            return false;
        if (!checkEntityBlacklist(livingEntity, player))
            return false;
        if (livingEntity == player)
            return false;

        if (HelperMethods.isOpacLoaded() && livingEntity instanceof Player
                && !OpacCompat.checkOpacFriendlyFire(livingEntity, player))
            return false;
        if (HelperMethods.isOpacLoaded() && livingEntity instanceof OwnableEntity tameable
                && tameable.getOwner() instanceof Player ownerPlayer
                && !OpacCompat.checkOpacFriendlyFire(ownerPlayer, player))
            return false;

        return EntityRelations.actionAllowed(
                SpellTarget.FocusMode.AREA,
                SpellTarget.Intent.HARMFUL,
                player,
                livingEntity);
    }

    // Check for back attack
    public static boolean isBehindTarget(LivingEntity attacker, LivingEntity target) {
        return Math.abs(Mth.wrapDegrees(target.getVisualRotationYInDegrees()
                - attacker.getVisualRotationYInDegrees())) < 32;
    }

    //Checks if skill is unlocked with presence checks.
    //If provided null for the skill argument, it will instead return if the category is unlocked.
    public static boolean isUnlocked(String skillTreeId, String skillId, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            if (skillId == null){
                // check if category is unlocked
                return SkillsAPI.getCategory(ResourceLocation.parse(skillTreeId))
                        .map(category -> category.isUnlocked(serverPlayer))
                        .orElse(false);
            } else {
                // check if skill is unlocked
                return SkillsAPI.getCategory(ResourceLocation.parse(skillTreeId))
                        .flatMap(category -> category.getSkill(skillId))
                        .map(skill -> skill.getState(serverPlayer) == Skill.State.UNLOCKED)
                        .orElse(false);
            }
        }
        return false;
    }
    
    //Checks if category has given skill unlocked
    public static boolean hasUnlockedSkill(Category category, String skillId, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            return category.getSkill(skillId)
                    .map(skill -> skill.getState(serverPlayer) == Skill.State.UNLOCKED)
                    .orElse(false);
        }
        return false;
    }

    public static int countUnlockedSkills(String skillTreeId, ServerPlayer serverPlayer) {
        return SkillsAPI.getCategory(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, skillTreeId))
                .map(category -> (int) category.streamUnlockedSkills(serverPlayer).count())
                .orElse(0);
    }

    //Check if the target matches blacklisted entities (expand this to be configurable if there is demand)
    public static boolean checkEntityBlacklist (LivingEntity livingEntity, Player player) {
        if (livingEntity == null || player == null) {
            return false;
        }
        return !(livingEntity instanceof ArmorStand)
                && !(livingEntity instanceof Villager);
    }

    //Get Item attack damage
    public static double getAttackDamage(ItemStack stack){
        double[] attackDamage = {0};
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE))
                attackDamage[0] += modifier.amount();
        });
        return attackDamage[0];
    }

    //Create Box
    public static AABB createBox(Entity entity, int radius) {
        AABB box = new AABB(entity.getX() + radius, entity.getY() + (float) radius / 3, entity.getZ() + radius,
                entity.getX() - radius, entity.getY() - (float) radius / 3, entity.getZ() - radius);

        return box;
    }
    public static AABB createBoxHeight(Entity entity, int radius) {
        AABB box = new AABB(entity.getX() + radius, entity.getY() + (float) radius, entity.getZ() + radius,
                entity.getX() - radius, entity.getY() - (float) radius, entity.getZ() - radius);

        return box;
    }
    public static AABB createBoxAtBlock(BlockPos blockpos, int radius) {
        AABB box = new AABB(blockpos.getX() + radius, blockpos.getY() + radius, blockpos.getZ() + radius,
                blockpos.getX() - radius, blockpos.getY() - radius, blockpos.getZ() - radius);

        return box;
    }
    public static AABB createBoxBetween(BlockPos blockpos, BlockPos blockpos2, int radius) {
        AABB box = new AABB(blockpos.getX() + radius, blockpos.getY() + radius, blockpos.getZ() + radius,
                blockpos2.getX() - radius, blockpos2.getY() - radius, blockpos2.getZ() - radius);

        return box;
    }


    /*
     * getTargetedEntity taken heavily from ZsoltMolnarrr's CombatSpells
     * https://github.com/ZsoltMolnarrr/CombatSpells/blob/main/common/src/main/java/net/combatspells/utils/TargetHelper.java#L72
     */
    public static Entity getTargetedEntity(Entity user, int range) {
        Vec3 rayCastOrigin = user.getEyePosition();
        Vec3 userView = user.getViewVector(1.0F).normalize().scale(range);
        Vec3 rayCastEnd = rayCastOrigin.add(userView);
        AABB searchBox = user.getBoundingBox().inflate(range, range, range);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(user, rayCastOrigin, rayCastEnd, searchBox,
                (target) -> !target.isSpectator() && target.isPickable() && target instanceof LivingEntity, range * range);
        if (hitResult != null) {
            return hitResult.getEntity();
        }
        return null;
    }

    public static Vec3 getPositionLookingAt(Player player, int range) {
        HitResult result = player.pick(range, 0, false);
        if (!(result.getType() == HitResult.Type.BLOCK)) return null;

        BlockHitResult blockResult = (BlockHitResult) result;
        return blockResult.getLocation();
    }

    // Checks for the block we are looking at. If there are no blocks, we instead look for the furthest air block relative to the range argument.
    public static BlockPos getBlockLookingAt(Player player, int range) {
        HitResult result = player.pick(range, 0, false);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) result;
            return blockResult.getBlockPos();
        }
        return getFirstAirBlockLookingAt(player, range);
    }

    public static BlockPos getFirstAirBlockLookingAt(Player player, int range) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        for (int i = range - 4; i < range; i++) {
            Vec3 step = start.add(look.x * i, look.y * i, look.z * i);
            BlockPos pos = new BlockPos((int) step.x, (int) step.y, (int) step.z);
            if (player.level().isEmptyBlock(pos)) {
                return pos;
            }
        }
        return null;
    }

    public static void incrementStatusEffect(
            LivingEntity livingEntity,
            Holder<MobEffect> statusEffect,
            int duration,
            int stacks,
            int maxStacks) {

        int stacksToAdd = Math.max(1, stacks);
        int currentStacks = livingEntity.hasEffect(statusEffect)
                ? livingEntity.getEffect(statusEffect).getAmplifier() + 1
                : 0;
        int newStacks = Math.min(currentStacks + stacksToAdd, Math.max(1, maxStacks));

        livingEntity.addEffect(new MobEffectInstance(
                statusEffect, duration, newStacks - 1, false, false, true));

    }

    public static void capStatusEffect (LivingEntity livingEntity) {

        int spellforgedCap = 5;
        int mightCap = 30;
        int marksmanshipCap = 30;

        List<MobEffectInstance> list = livingEntity.getActiveEffects().stream().toList();
        if (!list.isEmpty()) {
            for (MobEffectInstance statusEffectInstance : list) {
                Holder<MobEffect> statusEffect = statusEffectInstance.getEffect();

                switch (statusEffect.value().getDisplayName().getString()) {

                    case "Spellforged":
                        if (statusEffectInstance.getAmplifier() > spellforgedCap)
                            decrementStatusEffects(livingEntity, statusEffect,
                                    statusEffectInstance.getAmplifier() - spellforgedCap);
                    case "Might":
                        if (statusEffectInstance.getAmplifier() > mightCap)
                            decrementStatusEffects(livingEntity, statusEffect,
                                    statusEffectInstance.getAmplifier() - mightCap);
                    case "Marksmanship":
                        if (statusEffectInstance.getAmplifier() > marksmanshipCap)
                            decrementStatusEffects(livingEntity, statusEffect,
                                    statusEffectInstance.getAmplifier() - marksmanshipCap);

                }
            }
        }
    }

    public static boolean stringContainsAny (String string, String[] stringList) {
        for (String s : stringList) {
            if (string.contains(s))
                return true;
        }
        return false;
    }

    public static void decrementStatusEffect(
            LivingEntity livingEntity,
            Holder<MobEffect> statusEffect) {

        if (livingEntity.hasEffect(statusEffect)) {
            int currentAmplifier = livingEntity.getEffect(statusEffect).getAmplifier();
            int currentDuration = livingEntity.getEffect(statusEffect).getDuration();

            if (currentAmplifier < 1 ) {
                livingEntity.removeEffect(statusEffect);
                return;
            }

            livingEntity.removeEffect(statusEffect);
            livingEntity.addEffect(new MobEffectInstance(
                    statusEffect, currentDuration, currentAmplifier - 1, false, false, true));
        }
    }

    public static void decrementStatusEffects(
            LivingEntity livingEntity,
            Holder<MobEffect> statusEffect,
            int stacksRemoved) {

        if (livingEntity.hasEffect(statusEffect)) {
            int currentAmplifier = livingEntity.getEffect(statusEffect).getAmplifier();
            int currentDuration = livingEntity.getEffect(statusEffect).getDuration();

            if (currentAmplifier < 1 ) {
                livingEntity.removeEffect(statusEffect);
                return;
            }

            livingEntity.removeEffect(statusEffect);
            livingEntity.addEffect(new MobEffectInstance(
                    statusEffect, currentDuration, currentAmplifier - stacksRemoved, false, false, true));
        }
    }

    public static boolean buffSteal(
            LivingEntity user,
            LivingEntity target,
            boolean strip,
            boolean singular,
            boolean debuff,
            boolean cleanse) {

        // Strip - removes the status effect
        // Singular - affects one status effect per method call
        // Debuff - affects non-beneficial status effects instead of beneficial
        // Cleanse - does not increment the effect on the user (effectively cleansing when debuff & strip are true)

        List<MobEffectInstance> list = target.getActiveEffects().stream().toList();
        if (list.isEmpty())
            return false;

        for (MobEffectInstance statusEffectInstance : list) {
            Holder<MobEffect> statusEffect = statusEffectInstance.getEffect();
            int duration = statusEffectInstance.getDuration();
            int amplifier = statusEffectInstance.getAmplifier();

            if (statusEffect.value().isBeneficial() && !debuff) {
                if (user != null && !cleanse)
                    HelperMethods.incrementStatusEffect(user, statusEffect, duration, 1, amplifier);
                if (strip)
                    HelperMethods.decrementStatusEffect(target, statusEffectInstance.getEffect());
                if (singular)
                    return true;
            }
            else if (!statusEffect.value().isBeneficial() && debuff) {
                if (user != null && !cleanse)
                    HelperMethods.incrementStatusEffect(user, statusEffect, duration, 1, amplifier);
                if (strip)
                    HelperMethods.decrementStatusEffect(target, statusEffectInstance.getEffect());
                if (singular)
                    return true;
            }
        }


        return true;
    }

    //Spawns particles across both client & server
    public static void spawnParticle(Level world, ParticleOptions particle, double  xpos, double ypos, double zpos,
                                     double xvelocity, double yvelocity, double zvelocity) {

        if (world.isClientSide) {
            world.addParticle(particle, xpos, ypos, zpos, xvelocity, yvelocity, zvelocity);
        } else {
            if (world instanceof ServerLevel serverWorld) {
                serverWorld.sendParticles(particle, xpos, ypos, zpos, 1, xvelocity, yvelocity, zvelocity, 0);
            }
        }
    }

    //Spawn particles at plane
    public static void spawnParticlesPlane(
            Level world,
            ParticleOptions particle,
            BlockPos blockpos,
            int radius,
            double xvelocity,
            double yvelocity,
            double zvelocity) {

        double xpos = blockpos.getX() - (radius + 1);
        double ypos = blockpos.getY();
        double zpos = blockpos.getZ() - (radius + 1);
        for (int i = radius * 2; i > 0; i--) {
            for (int j = radius * 2; j > 0; j--) {
                float choose = (float) (Math.random() * 1);
                HelperMethods.spawnParticle(world, particle, xpos + i + choose,
                        ypos,
                        zpos + j + choose,
                        xvelocity, yvelocity, zvelocity);
            }
        }
    }
    public static void spawnParticlesInFrontOfPlayer(ServerLevel world, LivingEntity livingEntity, ParticleOptions particle, int distance, double speed, int count) {
        Vec3 lookVec = livingEntity.getViewVector(1.0F).normalize();
        Vec3 startPosition = livingEntity.getEyePosition().add(lookVec.scale(distance)); // Starting position in front of the player

        for (int i = 0; i < count; i++) {
            // Random offset to spread particles around the starting position
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0; // Spread of 2 blocks around the starting position
            double offsetY = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;

            // Calculate spawn position with offset
            double xPos = startPosition.x + offsetX;
            double yPos = startPosition.y + offsetY;
            double zPos = startPosition.z + offsetZ;

            // Apply velocity to move particles in the player's look direction
            double xVelocity = lookVec.x * speed;
            double yVelocity = lookVec.y * speed;
            double zVelocity = lookVec.z * speed;

            // Spawn the particle
            world.sendParticles(particle, xPos, yPos, zPos, 0, xVelocity, yVelocity, zVelocity, 1.0);
        }
    }


    public static boolean respecialise( ServerPlayer user ) {

        List<String> specialisations = SimplySkills.getSpecialisationsAsArray();
        for (String specialisation : specialisations) {
            getCategory(ResourceLocation.parse(specialisation)).get().erase(user);
        }
        getCategory(ResourceLocation.parse("simplyskills:tree")).get().resetSkills(user);
        ResourceLocation ascendancyTree = ResourceLocation.parse("simplyskills:ascendancy");
        if (getCategory(ascendancyTree).isPresent())
            getCategory(ascendancyTree).get().resetSkills(user);

        if (ModList.get().isLoaded("prominent")) {
            ResourceLocation prom = ResourceLocation.parse("puffish_skills:prom");
            if (getCategory(prom).isPresent())
                getCategory(prom).get().resetSkills(user);
        }

        return true;
    }
    public static boolean levelAll( ServerPlayer user ) {

        List<String> specialisations = SimplySkills.getSpecialisationsAsArray();
        if (!ModList.get().isLoaded("prominent")) {
            for (String specialisation : specialisations) {
                getCategory(ResourceLocation.parse(specialisation)).get().unlock(user);
                getCategory(ResourceLocation.parse(specialisation)).get().addExtraPoints(user, 99);
            }
        }
        getCategory(ResourceLocation.parse("simplyskills:tree")).get().addExtraPoints(user, 99);
        if (!ModList.get().isLoaded("prominent")) {
            getCategory(ResourceLocation.parse("simplyskills:ascendancy")).get().unlock(user);
            getCategory(ResourceLocation.parse("simplyskills:ascendancy")).get().addExtraPoints(user, 99);
        }
        if (ModList.get().isLoaded("prominent"))
            getCategory(ResourceLocation.parse("puffish_skills:prom")).get().addExtraPoints(user, 99);
        return true;
    }

    public static void treeResetOnDeath(ServerPlayer user ) {
        int expLoss = SimplySkills.generalConfig.treeExpLossOnDeath;
        if (expLoss > 0) {
            loseExpOnDeath(user, expLoss);
        }
        else if (SimplySkills.generalConfig.treeResetOnDeath) {
            resetAllTrees(user);
        }
    }

    public static void resetAllTrees (ServerPlayer user) {
        List<String> specialisations = SimplySkills.getSpecialisationsAsArray();
        for (String specialisation : specialisations) {
            getCategory(ResourceLocation.parse(specialisation)).get().erase(user);
            getCategory(ResourceLocation.parse("simplyskills:ascendancy")).get().erase(user);
            if (ModList.get().isLoaded("prominent"))
                getCategory(ResourceLocation.parse("puffish_skills:prom")).get().erase(user);
            else getCategory(ResourceLocation.parse("simplyskills:tree")).get().erase(user);
        }
    }

    public static void loseExpOnDeath(ServerPlayer player, int percent) {
        List<String> specialisations = SimplySkills.getSpecialisationsAsArray();
        specialisations.add("simplyskills:ascendancy");
        specialisations.add("simplyskills:tree");
        for (String specialisation : specialisations) {
            Optional<Category> categoryOpt = getCategory(ResourceLocation.parse(specialisation));
            if (categoryOpt.isPresent() && categoryOpt.get().isUnlocked(player)) {
                Category category = categoryOpt.get();
                Optional<Experience> experienceOpt = category.getExperience();
                if (experienceOpt.isPresent()) {
                    Experience experience = experienceOpt.get();
                    int currentExp = experience.getCurrent(player);
                    int currentLevel = experience.getLevel(player);
                    int requiredExpForCurrentLevel = experience.getRequired(player, currentLevel - 1);
                    int requiredExpForNextLevel = experience.getRequired(player, currentLevel);
                    float experienceProgress = ((float) currentExp / requiredExpForNextLevel) * 100;

                    // Calculate the value of X percent of experienceProgress
                    double expToLose = (percent / 100.0) * currentExp;
                    int newExp = currentExp - (int) expToLose;
                    if (newExp < 0) {
                        newExp = 0; // Ensure that experience does not go below zero
                    }

                    // Update the player's experience
                    //experience.setTotal(player, newExp);
                    if ((experience.getTotal(player)) - expToLose > 0) {
                        experience.addTotal(player, (int) -expToLose);
                        player.sendSystemMessage(Component.literal("You have lost " + percent + "% of your skill level progress"));
                    }
                } else {
                    // Debug
                    System.out.println("Experience object not present for category: " + specialisation);
                }
            } else {
                // Debug
                System.out.println("Category not present: " + specialisation);
            }
        }
    }

    public static int getSlotWithStack(Player player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (ItemStack.matches(player.getInventory().getItem(i), stack)) {
                return i;
            }
        }
        return -1; // Return -1 if the stack is not found in the inventory
    }

    private static CompoundTag getCustomData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setCustomData(ItemStack stack, CompoundTag nbt) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static boolean storeBuildTemplate( ServerPlayer user, ItemStack stack ) {
        int categoryCount = 0;
        int skillCount = 0;
        String userUUID = user.getStringUUID();

        CompoundTag nbt = getCustomData(stack);
        nbt.putString("player_name", user.getName().getString());

        if (!nbt.getString("player_uuid").isEmpty()) {
            return false;
        }

        for (Category category : (Iterable<Category>) SkillsAPI.streamUnlockedCategories(user)::iterator) {
            String categoryKey = "category" + categoryCount;
            nbt.putString(categoryKey, category.getId().toString());

            // Store the total experience for the category
            String expKey = "exp" + categoryCount;
            category.getExperience().ifPresent(experience -> nbt.putInt(expKey, experience.getTotal(user)));

            for (Skill skill : (Iterable<Skill>) category.streamUnlockedSkills(user)::iterator) {
                String skillKey = "skill" + skillCount;
                nbt.putString(skillKey, skill.getId());
                skillCount++;
            }
            categoryCount++;
        }

        resetAllTrees(user);
        nbt.putString("player_uuid", userUUID);
        setCustomData(stack, nbt);
        int slot = getSlotWithStack(user, stack);
        if (slot != -1) {
            ModPacketHandler.syncItemStackNbt(user, slot, stack);
        }

        return true;
    }

    public static boolean applyBuildTemplate( ServerPlayer user, ItemStack stack ) {

        CompoundTag nbt = getCustomData(stack);
        String uuid = user.getStringUUID();

        if (!nbt.getString("player_uuid").equals(uuid) && !SimplySkills.generalConfig.enableBuildSharing) {
            return false;
        }

        resetAllTrees(user);
        int size = nbt.size();
        for (int i = 0; i < size; i++) {
            String categoryKey = "category" + i;
            String category = nbt.getString(categoryKey);
            if (category.isEmpty()) continue;

            int finalI = i;
            getCategory(ResourceLocation.parse(category)).ifPresent(categoryObj -> {
                categoryObj.unlock(user);

                // Retrieve and set the total experience for the category
                String expKey = "exp" + finalI;
                if (nbt.contains(expKey)) {
                    categoryObj.getExperience().ifPresent(experience -> experience.setTotal(user, nbt.getInt(expKey)));
                }

                for (int s = 0; s < size; s++) {
                    String skillKey = "skill" + s;
                    String skill = nbt.getString(skillKey);
                    if (skill.isEmpty()) continue;

                    categoryObj.getSkill(skill).ifPresent(skillObj -> skillObj.unlock(user));

                }
            });
        }

        //Clear NBT
        if (!nbt.isEmpty()) {
            int tempSize = nbt.size();
            for (int i = 0; i < tempSize; i++) {
                String categoryKey = "category" + i;
                if (!nbt.getString(categoryKey).isEmpty()) {
                    nbt.remove(categoryKey);

                    for (int s = 0; s < tempSize; s++) {
                        String skillKey = "skill" + s;
                        if (!nbt.getString(skillKey).isEmpty())
                            nbt.remove(skillKey);
                    }
                }

                // Remove the experience key
                String expKey = "exp" + i;
                if (nbt.contains(expKey, CompoundTag.TAG_INT)) {
                    nbt.remove(expKey);
                }
            }
            nbt.remove("player_uuid");
            nbt.remove("player_name");
        }
        setCustomData(stack, nbt);
        int slot = getSlotWithStack(user, stack);
        if (slot != -1) {
            ModPacketHandler.syncItemStackNbt(user, slot, stack);
        }
        return true;
    }

    public static void printNBT(ItemStack stack, List<Component> tooltip, String type) {
        CompoundTag nbt = getCustomData(stack);

        if (!nbt.isEmpty()) {
            int tempSize = nbt.size();
            int skillPrintCount = 0;
            for (int i = 0; i < tempSize; i++) {

                if (!nbt.getString("category" + i).isEmpty()) {
                    if (type.equals("category") && !nbt.getString("category" + i).contains("tree"))
                        tooltip.add(Component.literal("  §6◇ §f" + nbt.getString("category" + i).
                                replace("simplyskills:", "").replace("puffish_skills:prom", "Talent Tree")));
                }
                if (!nbt.getString("skill" + i).isEmpty())
                    skillPrintCount++;
            }

            if (type.equals("skill"))
                tooltip.add(Component.literal("  §b◇ §f" + skillPrintCount));

            if (!nbt.getString("player_name").isEmpty()) {
                String name = nbt.getString("player_name");
                if (type.equals("name"))
                    tooltip.add(Component.literal("§7Bound to: " + name));
            }
        }
    }
    public static int getUnspentPoints(ServerPlayer player) {
        return SkillsAPI.streamUnlockedCategories(player)
                .mapToInt(category -> category.getPointsLeft(player))
                .sum();
    }

    public static double getHighestAttributeValue(Player player) {
        double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double toughness = SpellPower.getSpellPower(SpellSchools.FROST, player).baseValue();
        double fire = SpellPower.getSpellPower(SpellSchools.FIRE, player).baseValue();
        double arcane = SpellPower.getSpellPower(SpellSchools.ARCANE, player).baseValue();
        double soul = SpellPower.getSpellPower(SpellSchools.SOUL, player).baseValue();
        double healing = SpellPower.getSpellPower(SpellSchools.HEALING, player).baseValue();
        double lightning = SpellPower.getSpellPower(SpellSchools.LIGHTNING, player).baseValue();

        Double[] attributeValues = {attackDamage, toughness, fire, arcane, soul, healing, lightning};

        return Arrays.stream(attributeValues).max(Comparator.naturalOrder()).orElse(Double.MIN_VALUE);
    }

    public static double getRelativeRangedAttribute(Player player) {
        var dealtDamage = 1;
        var rangedDamage = AttributesMod.applyAttributeModifiers(
                dealtDamage,
                Sign.POSITIVE.wrap(player.getAttribute(AttributesMod.RANGED_DAMAGE))
        );
        //System.out.println(rangedDamage);
        return player.getAttributeValue(AttributesMod.RANGED_DAMAGE);
    }

    @SafeVarargs
    public static double getHighestSpecificAttributeValue(Player player, Holder<Attribute>... attributes) {
        double highestValue = Double.MIN_VALUE;

        for (Holder<Attribute> attribute : attributes) {
            double attributeValue = player.getAttributeValue(attribute);
            if (attributeValue > highestValue) {
                highestValue = attributeValue;
            }
        }

        return highestValue;
    }

    public static void spawnWaistHeightParticles(ServerLevel world, ParticleOptions particle, Entity entity1, Entity entity2, int count) {
        Vec3 startPos = entity1.position().add(0, entity1.getBbHeight() / 2.0, 0); // Waist height of entity1
        Vec3 endPos = entity2.position().add(0, entity2.getBbHeight() / 2.0, 0); // Waist height of entity2
        Vec3 direction = endPos.subtract(startPos);
        double distance = direction.length();
        Vec3 normalizedDirection = direction.normalize();

        for (int i = 0; i < count; i++) {
            double lerpFactor = (double) i / (count - 1);
            Vec3 currentPos = startPos.add(normalizedDirection.scale(distance * lerpFactor));
            world.sendParticles(particle,
                    currentPos.x, currentPos.y, currentPos.z,
                    1,
                    0, 0, 0,
                    0.0);
        }
    }

    public static void spawnOrbitParticles(ServerLevel world, Vec3 center, ParticleOptions particleType, double radius, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            // Calculate the angle for this particle
            double angle = 2 * Math.PI * i / particleCount;

            // Calculate the x and z coordinates on the orbit
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y;

            world.sendParticles(particleType, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public static double getGroundDistance(Entity entity) {
        BlockPos pos = entity.blockPosition();
        while (pos.getY() > 0 && !entity.level().getBlockState(pos).isRedstoneConductor(entity.level(), pos)) {
            pos = pos.below();
        }
        return entity.getY() - pos.getY();
    }

    public static boolean hasHarmfulStatusEffect(LivingEntity entity) {
        for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
            if (effectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                return true;
            }
        }
        return false;
    }

    public static int countHarmfulStatusEffects(LivingEntity entity) {
        int harmfulEffectCount = 0;
        for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
            if (effectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                harmfulEffectCount++;
            }
        }
        return harmfulEffectCount;
    }

    public static boolean isDualWielding(LivingEntity livingEntity) {
        return (livingEntity.getMainHandItem().getItem() instanceof SwordItem || livingEntity.getMainHandItem().getItem() instanceof AxeItem)
                && (livingEntity.getOffhandItem().getItem() instanceof SwordItem || livingEntity.getOffhandItem().getItem() instanceof AxeItem);
    }

    public static void spawnDirectionalParticles(ServerLevel world, ParticleOptions particle, Entity entity, int count, double distance) {
        Vec3 startPos = entity.position().add(0, entity.getBbHeight() / 2.0, 0);

        float pitch = entity.getViewXRot(1.0F);
        float yaw = entity.getViewYRot(1.0F);

        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        double xDirection = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double yDirection = -Math.sin(pitchRadians);
        double zDirection = Math.cos(yawRadians) * Math.cos(pitchRadians);
        Vec3 direction = new Vec3(xDirection, yDirection, zDirection).normalize();

        for (int i = 0; i < count; i++) {
            double lerpFactor = (double) i / (count - 1);
            Vec3 currentPos = startPos.add(direction.scale(distance * lerpFactor));
            world.sendParticles(particle,
                    currentPos.x, currentPos.y, currentPos.z,
                    1,
                    0, 0, 0,
                    0.0);
        }
    }

    public static void damageEntitiesInTrajectory(ServerLevel world, Entity sourceEntity, Player playerEntity, double distance, float damage, DamageSource damageSource) {
        Vec3 startPos = sourceEntity.position().add(0, sourceEntity.getBbHeight() / 2.0, 0);
        float pitch = sourceEntity.getViewXRot(1.0F);
        float yaw = sourceEntity.getViewYRot(1.0F);

        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        double xDirection = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        double yDirection = -Math.sin(pitchRadians);
        double zDirection = Math.cos(yawRadians) * Math.cos(pitchRadians);
        Vec3 direction = new Vec3(xDirection, yDirection, zDirection).normalize();

        Vec3 endPos = startPos.add(direction.scale(distance));

        double boxSize = 0.5;
        AABB searchBox = new AABB(startPos, endPos).inflate(boxSize);

        for (Entity entity : world.getEntities(sourceEntity, searchBox)) {
            AABB entityBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            if (entityBox.intersects(searchBox)) {
                if ((entity instanceof LivingEntity livingTarget)
                        && checkFriendlyFire(livingTarget, playerEntity)) {
                    livingTarget.hurt(damageSource, damage);

                    //Class specific stuff
                    if (sourceEntity instanceof LivingEntity livingSource)
                        NecromancerAbilities.effectPestilence(playerEntity, livingSource, livingTarget);

                }
            }
        }
    }

    public static boolean isOpacLoaded() {
        return ModList.get().isLoaded("openpartiesandclaims");
    }

}
