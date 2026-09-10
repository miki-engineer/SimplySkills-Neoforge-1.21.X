package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class CataclysmEffect extends MobEffect {
    public CataclysmEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.CATACLYSM)) {
                MobEffectInstance cataclysmEffect = player.getEffect(EffectRegistry.CATACLYSM);
                if (cataclysmEffect == null)
                    return true;
                String spellId = "simplyskills:cataclysm_comet";
                if (SpellPower.getSpellPower(SpellSchools.FIRE, player).baseValue()
                        > SpellPower.getSpellPower(SpellSchools.FROST, player).baseValue())
                    spellId = "simplyskills:cataclysm_meteor";

                int initialDuration = 90; // Initial max duration of the effect
                int distanceIncrement = 5; // The number of blocks to increment the distance each time
                int frequency = 20 - Math.min(12, (AscendancyAbilities.getAscendancyPoints(player) / 10));
                int currentDistance = (initialDuration - cataclysmEffect.getDuration()) / frequency * distanceIncrement;

                if (cataclysmEffect.getDuration() % frequency == 0) {
                    player.level().playSound(null, player, SoundRegistry.SPELL_ENERGY,
                            SoundSource.PLAYERS, 0.5f, 1.1f);

                    Vec3 lookVector = player.getViewVector(1.0F);
                    Vec3 spellPosition = player.position().add(lookVector.multiply(currentDistance, 0, currentDistance));
                    BlockPos castPosition = new BlockPos((int)spellPosition.x, (int)spellPosition.y, (int)spellPosition.z);
                    SignatureAbilities.castSpellEngineIndirectTarget(player,
                            spellId,
                            45, null, castPosition);
                    if (AscendancyAbilities.getAscendancyPoints(player) > 29)
                        HelperMethods.incrementStatusEffect(player, EffectRegistry.SPELLFORGED, 60, 1, 10);
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
