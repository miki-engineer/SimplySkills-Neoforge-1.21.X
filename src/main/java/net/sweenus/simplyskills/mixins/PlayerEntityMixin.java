package net.sweenus.simplyskills.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sweenus.simplyskills.abilities.*;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin {

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    private float simplyskills$rageDamage(float amount) {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer) {
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.berserkerPath, serverPlayer))
                HelperMethods.incrementStatusEffect(player, EffectRegistry.RAGE, 300, 1, 99);

            if (!player.hasEffect(EffectRegistry.RAGE))
                return amount;

            float damageModifier = (float) 1 + ((float) player.getEffect(EffectRegistry.RAGE).getAmplifier() / 200);
            float modifiedAmount = amount * damageModifier;
            return Float.isFinite(modifiedAmount) ? modifiedAmount : amount;
        }
        return amount;
    }

    @Inject(at = @At("HEAD"), method = "blockUsingShield")
    public void simplyskills$takeShieldHit(LivingEntity attacker, CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (player instanceof ServerPlayer) {

            // Rebuke (Warrior)
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.bulwarkRebuke, player)) {
                WarriorAbilities.passiveWarriorRebuke(player, attacker);
            }

            //Retribution (Crusader)
            if (HelperMethods.isUnlocked("simplyskills:crusader",
                    SkillReferencePosition.crusaderRetribution, player)) {
                CrusaderAbilities.passiveCrusaderRetribution(player, attacker);
            }

            //Exhaustive Recovery (Crusader)
            if (HelperMethods.isUnlocked("simplyskills:crusader",
                    SkillReferencePosition.crusaderExhaustiveRecovery, player)) {
                CrusaderAbilities.passiveCrusaderExhaustiveRecovery(player, attacker);
            }
            // Golden Aegis
            if (HelperMethods.isUnlocked("simplyskills:ascendancy",
                    SkillReferencePosition.ascendancyRighteousShield, player)) {
                AscendancyAbilities.goldenAegis(player);
            }


        }
    }


}
