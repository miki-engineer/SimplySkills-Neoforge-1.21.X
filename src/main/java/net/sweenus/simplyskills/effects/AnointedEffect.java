package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.sweenus.simplyskills.abilities.ClericAbilities;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class AnointedEffect extends MobEffect {
    public AnointedEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);

        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player) {

                //Cleric Signature Anoint Weapon Cleanse
                if (HelperMethods.isUnlocked("simplyskills:cleric",
                        SkillReferencePosition.clericSpecialisationAnointWeaponCleanse, player)
                        && ModList.get().isLoaded("paladins")) {
                    ClericAbilities.signatureClericAnointWeaponCleanse(player);
                }

            }
        }
        return true;
}


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.SPELL_CELESTIAL_HIT,
                SoundSource.PLAYERS, 0.1f, 1.4f);
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.level().playSound(null, entity, SoundRegistry.SPELL_RADIANT_EXPIRE,
                SoundSource.PLAYERS, 0.4f, 1);
    }

}
