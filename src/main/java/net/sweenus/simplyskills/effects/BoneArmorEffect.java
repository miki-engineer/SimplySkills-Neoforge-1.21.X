package net.sweenus.simplyskills.effects;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;

public class BoneArmorEffect extends MobEffect {
    public BoneArmorEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        return true;
}


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {

        if (amplifier < 1 && entity instanceof Player player) {
            if (AscendancyAbilities.getAscendancyPoints(player) > 29) {
                player.addEffect(new MobEffectInstance(EffectRegistry.UNDYING, 160, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 3, false, false, true));
            }
        }

    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.MAGIC_SHAMANIC_SPELL_01,
                SoundSource.PLAYERS, 0.2f, 1 + ((float) amplifier /10));
    }

}
