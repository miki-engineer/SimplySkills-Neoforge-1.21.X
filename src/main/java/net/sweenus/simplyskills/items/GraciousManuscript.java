package net.sweenus.simplyskills.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

import java.util.List;

public class GraciousManuscript extends Item {
    public GraciousManuscript(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {

        if ((user instanceof ServerPlayer serverUser)) {
            if (HelperMethods.levelAll(serverUser)) {
                user.swing(hand);
                world.playSound(null, user.blockPosition(), SoundRegistry.SOUNDEFFECT12, SoundSource.PLAYERS, 0.5f, 1.0f);
                //user.getStackInHand(hand).decrement(1);
                serverUser.getCooldowns().addCooldown(this, 60);
            }
        }
        return super.use(world,user,hand);
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.simplyskills.gracious_manuscript.tooltip1"));
        tooltip.add(Component.translatable("item.simplyskills.gracious_manuscript.tooltip2"));
        tooltip.add(Component.translatable("item.simplyskills.gracious_manuscript.tooltip3"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.simplyskills.gracious_manuscript.tooltip4").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.UNDERLINE));
    }


}
