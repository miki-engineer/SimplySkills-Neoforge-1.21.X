package net.sweenus.simplyskills.mixins;

import net.neoforged.fml.ModList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sweenus.simplyskills.abilities.*;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "killedEntity")
    public void simplyskills$onKilledOther(ServerLevel world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player)(Object)this;
        if (player instanceof ServerPlayer) {

            // Effect Bloodthirsty
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerSpecialisationBloodthirsty, player)) {
                AbilityEffects.effectBerserkerBloodthirsty(player);
            }
            // Effect Elemental Arrows Renewal
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsRenewal, player)) {
                RangerAbilities.passiveRangerElementalArrowsRenewal(player);
            }
            // Effect Fan of Blades Renewal
            if (HelperMethods.isUnlocked("simplyskills:rogue",
                    SkillReferencePosition.rogueSpecialisationEvasionFanOfBladesRenewal, player)) {
                player.addEffect(new MobEffectInstance(EffectRegistry.FANOFBLADES, 500, 1, false, false, true));
            }


        }
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
            if (ModList.get().isLoaded("prominent")
                    && HelperMethods.isUnlocked("puffish_skills:prom",
                    SkillReferencePosition.ascendancyRighteousShield, player)) {
                AscendancyAbilities.goldenAegis(player);
            }

        }
    }


}