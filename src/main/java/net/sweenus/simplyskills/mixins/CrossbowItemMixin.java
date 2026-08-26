package net.sweenus.simplyskills.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sweenus.simplyskills.abilities.WayfarerAbilities;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @Inject(at = @At("HEAD"), method = "releaseUsing", cancellable = true)
    public void simplyskills$onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer) {

                //Break Stealth
                if (player.hasEffect(EffectRegistry.STEALTH) && remainingUseTicks < 3) {
                    WayfarerAbilities.passiveWayfarerBreakStealth(null, player, false, false);
                }



            }
        }
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void simplyskills$use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!world.isClientSide && user != null) {
            if (user instanceof ServerPlayer) {

                //Gain Stealth
                if (HelperMethods.isUnlocked("simplyskills:tree",
                        SkillReferencePosition.wayfarerUnseen, user)) {
                    if (!user.hasEffect(EffectRegistry.STEALTH)) {
                        user.addEffect(new MobEffectInstance(EffectRegistry.STEALTH, 45, 0, false, false, true));
                    } else {
                        HelperMethods.incrementStatusEffect(user ,EffectRegistry.MARKSMANSHIP, 60, 1, 10);
                    }
                }

            }
        }
    }


}
