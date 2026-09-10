package net.sweenus.simplyskills.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.puffish.skillsmod.SkillsMod;
import net.puffish.skillsmod.api.SkillsAPI;
import net.puffish.skillsmod.server.network.packets.in.SkillClickInPacket;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.AbilityLogic;
import net.sweenus.simplyskills.network.ModPacketHandler;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SkillsMod.class)
public class SkillsModMixin {

    @Inject(at = @At("HEAD"), method = "tryUnlockSkill")
    public void simplyskills$tryUnlockSkill(ServerPlayer player, ResourceLocation categoryId, String skillId, boolean force, CallbackInfo ci) {

        //Sound Event on skill unlock
        var category = SkillsAPI.getCategory(categoryId).orElseThrow();
        if (!HelperMethods.hasUnlockedSkill(category, skillId, player) && category.getPointsLeft(player) > 0) {

            double choose_pitch = Math.random() * 1.3;
            List<SoundEvent> sounds = new ArrayList<>();
            sounds.add(SoundRegistry.PLACE_STONE_06);
            sounds.add(SoundRegistry.PLACE_STONE_07);
            sounds.add(SoundRegistry.PLACE_STONE_08);
            sounds.add(SoundRegistry.PLACE_STONE_09);
            sounds.add(SoundRegistry.PLACE_STONE_10);
            SoundEvent sound = sounds.get(player.getRandom().nextInt(sounds.size()));

            player.level().playSound(null, player, sound,
                    SoundSource.PLAYERS, 0.3f, (float) choose_pitch);

            if (category.streamUnlockedSkills(player).count() > 40 && categoryId.equals(ResourceLocation.parse("simplyskills:tree"))) {
                SkillsAPI.getCategory(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "ascendancy"))
                        .ifPresent(ascendancy -> ascendancy.unlock(player));
            }


            }
    }

    @Inject(at = @At("TAIL"), method = "tryUnlockSkill")
    public void simplyskills$tryUnlockSkillTail(ServerPlayer player, ResourceLocation categoryId, String skillId, boolean force, CallbackInfo ci) {
        ModPacketHandler.sendSignatureAbility(player);
    }
    @Inject(at = @At("TAIL"), method = "resetSkills")
    public void simplyskills$resetSkills(ServerPlayer player, ResourceLocation categoryId, CallbackInfo ci) {
        ModPacketHandler.sendSignatureAbility(player);
    }
    @Inject(at = @At("TAIL"), method = "eraseCategory")
    public void simplyskills$eraseCategory(ServerPlayer player, ResourceLocation categoryId, CallbackInfo ci) {
        ModPacketHandler.sendSignatureAbility(player);
    }

    @Inject(at = @At("TAIL"), method = "onSkillClickPacket")
    public void simplyskills$onSkillClickPacket(ServerPlayer player, SkillClickInPacket packet, CallbackInfo ci) {
        AbilityLogic.performJunctionLogic(player, packet.getSkillId(), packet.getCategoryId());
    }

    @Inject(at = @At("HEAD"), method = "unlockCategory", cancellable = true)
    public void simplyskills$unlockCategory(ServerPlayer player, ResourceLocation categoryIdentifier, CallbackInfo ci) {
        String categoryId = categoryIdentifier.toString();
        if (AbilityLogic.skillTreeUnlockManager(player, categoryId))
            ci.cancel();
    }

    }
