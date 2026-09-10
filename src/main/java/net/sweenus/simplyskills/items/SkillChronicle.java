package net.sweenus.simplyskills.items;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.puffish.skillsmod.api.Category;
import net.puffish.skillsmod.api.SkillsAPI;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.client.SimplySkillsClient;
import net.sweenus.simplyskills.network.ModPacketHandler;
import net.sweenus.simplyskills.network.UpdateUnspentPointsPacket;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

import java.util.List;

public class SkillChronicle extends Item {
    public SkillChronicle(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() +10) {
            return InteractionResultHolder.fail(itemStack);
        }

        world.playSound(null, user.blockPosition(), SoundRegistry.SOUNDEFFECT6, SoundSource.PLAYERS, 0.3f, 0.7f);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 60;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClientSide && user.getItemBySlot(EquipmentSlot.MAINHAND) == stack) {
            if (remainingUseTicks < 35)
                user.releaseUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClientSide && (user instanceof Player player)) {
            if ((user instanceof ServerPlayer serverUser) && remainingUseTicks < 35) {

                int pointsRemaining = 0;
                boolean hasSpentPoints = false;
                boolean success = false;

                for (Category uc : (Iterable<Category>) SkillsAPI.streamUnlockedCategories(serverUser)::iterator) {

                    //Check for points spent in base tree
                    if (uc.getId().toString().equals("simplyskills:tree")) {
                        pointsRemaining = uc.getPointsLeft(serverUser);
                        hasSpentPoints = uc.streamUnlockedSkills(serverUser).findAny().isPresent();
                        //System.out.println("Checking if we have skills unlocked");
                    }
                }

                //STORE
                if (hasSpentPoints) {
                    //System.out.println("Found skills. Trying to store build.");
                    if (HelperMethods.storeBuildTemplate(serverUser, stack)) {
                        world.playSound(null, user.blockPosition(), SoundRegistry.SOUNDEFFECT44, SoundSource.PLAYERS, 0.6f, 1.0f);
                        player.getCooldowns().addCooldown(this, SimplySkills.generalConfig.skillChronicleCooldown);
                        user.sendSystemMessage(Component.literal("Build Stored Successfully"));
                        success = true;
                    }
                }
                //APPLY
                else {
                    //System.out.println("Did not find skills. Trying to retrieve build.");
                    if (HelperMethods.applyBuildTemplate(serverUser, stack)) {
                        world.playSound(null, user.blockPosition(), SoundRegistry.SOUNDEFFECT43, SoundSource.PLAYERS, 0.7f, 1.0f);
                        player.getCooldowns().addCooldown(this, SimplySkills.generalConfig.skillChronicleCooldown);
                        user.sendSystemMessage(Component.literal("Build Retrieved Successfully"));
                        success = true;
                    }
                }
                if (!success) {
                    user.sendSystemMessage(Component.literal("You do not meet the requirements"));
                    player.getCooldowns().addCooldown(this, 60);
                }
            }
            if (player instanceof ServerPlayer serverPlayer)
                ModPacketHandler.sendStopSoundPacket(serverPlayer, SoundRegistry.SOUNDEFFECT6.getLocation());
            if (!player.getCooldowns().isOnCooldown(this))
                player.getCooldowns().addCooldown(this, 60);
        }
    }
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity.tickCount %60 == 0) {
            if (entity instanceof ServerPlayer user) {
                int unspentPoints = HelperMethods.getUnspentPoints(user);
                ModPacketHandler.sendTo(user, new UpdateUnspentPointsPacket(unspentPoints));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipContext) {
        CompoundTag nbt = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (nbt != null) {
            if (!nbt.getString("player_uuid").isEmpty()) {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip10"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip8"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip14"));
                HelperMethods.printNBT(itemStack, tooltip, "category");
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip15"));
                HelperMethods.printNBT(itemStack, tooltip, "skill");
                tooltip.add(Component.literal(""));
                HelperMethods.printNBT(itemStack, tooltip, "name");
                if (SimplySkillsClient.unspentPoints > 0) {
                    tooltip.add(Component.literal(""));
                    tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip11", SimplySkillsClient.unspentPoints));
                    tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip12"));
                }
                    tooltip.add(Component.literal(""));
            } else {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip9"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip7"));
                if (SimplySkillsClient.unspentPoints > 0) {
                    tooltip.add(Component.literal(""));
                    tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip11", SimplySkillsClient.unspentPoints));
                    tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip13"));
                }
                tooltip.add(Component.literal(""));
            }
        }
        tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip16"));
        if (Screen.hasAltDown()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip1"));
            tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip2"));
            tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip3"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip4"));
            tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip5"));
            tooltip.add(Component.translatable("item.simplyskills.skill_chronicle.tooltip6"));
        }
    }


}
