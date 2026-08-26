package net.sweenus.simplyskills.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sweenus.simplyskills.abilities.AbilityEffects;
import net.sweenus.simplyskills.abilities.WayfarerAbilities;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @Shadow public abstract int getUseDuration(ItemStack stack, LivingEntity livingEntity);

    @Invoker
    public static float callGetPowerForTime(int useTicks) {
        throw new AssertionError();
    }

    @Inject(at = @At("HEAD"), method = "releaseUsing", cancellable = true)
    public void simplyskills$onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                float requiredPullProgress = 1.0F;
                if (stack.getHoverName().toString().contains("Shortbow") || stack.getHoverName().toString().contains("shortbow")
                        || stack.getHoverName().toString().contains("love"))
                    requiredPullProgress = 0.5F;

                // Calculate the use ticks
                int useTicks = this.getUseDuration(stack, user) - remainingUseTicks;

                // Effect - Elemental Arrows
                if (player.hasEffect(EffectRegistry.ELEMENTALARROWS)) {
                    if (callGetPowerForTime(useTicks) >= requiredPullProgress) {

                        if (AbilityEffects.effectRangerElementalArrows(player))
                            ci.cancel();
                    }
                }


                // Effect - Arrow Rain
                else if (player.hasEffect(EffectRegistry.ARROWRAIN)) {
                    if (callGetPowerForTime(useTicks) >= requiredPullProgress) {

                        if (AbilityEffects.effectRangerArrowRain(player))
                            ci.cancel();
                    }
                }

                // Effect - Marksman Snipe
                else if (player.hasEffect(EffectRegistry.MARKSMAN)) {
                    if (callGetPowerForTime(useTicks) >= requiredPullProgress) {

                        if (AbilityEffects.effectRangerMarksman(player))
                            ci.cancel();
                    }
                }


                //Effect Stealth
                if (player.hasEffect(EffectRegistry.STEALTH)) {
                    WayfarerAbilities.passiveWayfarerBreakStealth(null, player, false, false);
                }

                // Use the proxy method to call the actual getPullProgress method
                if (callGetPowerForTime(useTicks) >= requiredPullProgress && HelperMethods.isUnlocked("simplyskills:tree",
                        SkillReferencePosition.wayfarerQuickfire, player)) {
                    HelperMethods.incrementStatusEffect(user ,EffectRegistry.MARKSMANSHIP,40, 1, 6);
                }

            }
        }
    }
}
