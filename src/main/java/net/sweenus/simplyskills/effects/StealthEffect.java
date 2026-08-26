package net.sweenus.simplyskills.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class StealthEffect extends MobEffect {
    public StealthEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {


        if (!livingEntity.level().isClientSide()) {
            if (livingEntity instanceof ServerPlayer serverPlayer) {

                int regenerationFrequency = SimplySkills.rogueConfig.passiveRogueRecoveryRegenerationFrequency;
                int regenerationAmplifier = SimplySkills.rogueConfig.passiveRogueRecoveryRegenerationAmplifier;
                int resistanceFrequency = SimplySkills.rogueConfig.passiveRogueShadowVeilResistanceFrequency;
                int resistanceStacks = SimplySkills.rogueConfig.passiveRogueShadowVeilResistanceStacks;
                int resistanceMaxStacks = SimplySkills.rogueConfig.passiveRogueShadowVeilResistanceMaxStacks;


                if (serverPlayer.hasEffect(EffectRegistry.REVEALED)) {
                    livingEntity.removeEffect(EffectRegistry.STEALTH);
                }
                if (serverPlayer.hasEffect(EffectRegistry.STEALTH)
                        && serverPlayer.getEffect(EffectRegistry.STEALTH).getDuration() < 10) {
                    livingEntity.addEffect(new MobEffectInstance(EffectRegistry.REVEALED,
                            180, 2, false, false, true));
                }


                if (HelperMethods.isUnlocked("simplyskills:rogue",
                        SkillReferencePosition.rogueRecovery, serverPlayer)
                        && serverPlayer.tickCount % regenerationFrequency == 0)
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                            regenerationFrequency + 5, regenerationAmplifier, false, false, true));

                if (HelperMethods.isUnlocked("simplyskills:rogue",
                        SkillReferencePosition.rogueShadowVeil, serverPlayer)
                        && serverPlayer.tickCount % regenerationFrequency == 0)
                    HelperMethods.incrementStatusEffect(serverPlayer, MobEffects.DAMAGE_RESISTANCE,
                            resistanceFrequency + 5, resistanceStacks, resistanceMaxStacks);

            }
        }

        super.applyEffectTick(livingEntity, amplifier);
        return true;
}


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
