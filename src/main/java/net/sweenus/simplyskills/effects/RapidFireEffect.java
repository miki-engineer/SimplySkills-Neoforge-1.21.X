package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class RapidFireEffect extends MobEffect {
    public RapidFireEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    private int arrowCount = 0;

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.RAPIDFIRE)) {
                if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {

                    MobEffectInstance rapidFire = player.getEffect(EffectRegistry.RAPIDFIRE);
                    if (rapidFire == null)
                        return true;

                    if (rapidFire.getDuration() % 4 == 0) {
                        player.level().playSound(null, player, SoundEvents.PLAYER_ATTACK_WEAK,
                                SoundSource.PLAYERS, 0.6f, 1.4f);
                        if (player.getMainHandItem().getItem() instanceof BowItem)
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:rapidfire", 3, player, null);
                        else if (player.getMainHandItem().getItem() instanceof CrossbowItem)
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:rapidfire_crossbow", 3, player, null);
                    } else if (rapidFire.getDuration() % 5 == 0) {
                        arrowCount++;
                        SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:rapidfire_projectile", 3, player, null);
                    }
                    if (arrowCount > 2 && AscendancyAbilities.getAscendancyPoints(player) > 29) {
                        arrowCount = 0;
                        HelperMethods.incrementStatusEffect(player, EffectRegistry.MARKSMANSHIP, 60, 1, 12);
                    }

                }
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (entity instanceof Player player && ModList.get().isLoaded("simplyswords"))
            SimplySwordsGemEffects.warStandard(player);
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
