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
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;

public class ArcaneSlashEffect extends MobEffect {
    public ArcaneSlashEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.ARCANESLASH)) {
                MobEffectInstance arcaneSlash = player.getEffect(EffectRegistry.ARCANESLASH);
                if (arcaneSlash == null)
                    return true;

                if (arcaneSlash.getDuration() == 10 && AscendancyAbilities.getAscendancyPoints(player) < 30) {
                    player.level().playSound(null, player, SoundEvents.PLAYER_ATTACK_STRONG,
                            SoundSource.PLAYERS, 1f, 1.1f);
                    SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:arcane_slash_projectile", 3, player, null);
                }
                else if (arcaneSlash.getDuration() == 15 && AscendancyAbilities.getAscendancyPoints(player) > 29) {
                    //player.getWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                    //        SoundCategory.PLAYERS, 1f, 1.0f);
                    SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:arcane_slash_projectile_2", 3, player, null);
                }

            }
        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (entity instanceof Player player && ModList.get().isLoaded("simplyswords"))
            SimplySwordsGemEffects.warStandard(player);
        if (entity instanceof Player player) {
            int chance = entity.getRandom().nextInt(100);
            if (chance < 80)
                AscendancyAbilities.arcaneSlash(player);
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
