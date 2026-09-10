package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class BullrushEffect extends MobEffect {
    public BullrushEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.onGround() && (livingEntity instanceof Player player)) {

                int bullrushVelocity = SimplySkills.berserkerConfig.signatureBerserkerBullrushVelocity;
                int bullrushRadius = SimplySkills.berserkerConfig.signatureBerserkerBullrushRadius;
                double bullrushDamageModifier = SimplySkills.berserkerConfig.signatureBerserkerBullrushDamageModifier;
                int bullrushHitFrequency = SimplySkills.berserkerConfig.signatureBerserkerBullrushHitFrequency;
                int bullrushImmobilizeDuration = SimplySkills.berserkerConfig.signatureBerserkerBullrushImmobilizeDuration;
                int bullrushStrengthDuration = SimplySkills.berserkerConfig.signatureBerserkerBullrushRelentlessDuration;
                int bullrushExhaustionPerStrength = SimplySkills.berserkerConfig.signatureBerserkerBullrushRelentlessExhaustPerStrength;

                player.setDeltaMovement(livingEntity.getLookAngle().scale(+bullrushVelocity));
                player.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().z);
                player.hurtMarked = true;
                int radius = bullrushRadius;
                double damage_multiplier = bullrushDamageModifier;
                double damage = (HelperMethods.getAttackDamage(livingEntity.getMainHandItem()) * damage_multiplier);

                AABB box = HelperMethods.createBox(player, radius*2);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFireAOE(le, player)) {
                            le.setDeltaMovement((player.getX() - le.getX()) /4,  (player.getY() - le.getY()) /4, (player.getZ() - le.getZ()) /4);
                            if (player.tickCount % bullrushHitFrequency == 0) {
                                le.hurt(player.damageSources().playerAttack(player), (float) damage);
                                player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT32,
                                        SoundSource.PLAYERS, 0.6f, 1.0f);
                                if (HelperMethods.isUnlocked("simplyskills:berserker",
                                        SkillReferencePosition.berserkerSpecialisationRampageChargeImmob, player))
                                    le.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZE, bullrushImmobilizeDuration, 0, false, false, true));

                                if (HelperMethods.isUnlocked("simplyskills:berserker",
                                        SkillReferencePosition.berserkerSignatureRampageChargeRelentless, player)
                                        &&player.hasEffect(EffectRegistry.EXHAUSTION)) {
                                    int stacks = player.getEffect(EffectRegistry.EXHAUSTION).getAmplifier();
                                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                                            bullrushStrengthDuration, stacks / bullrushExhaustionPerStrength, false, false, true));
                                    player.removeEffect(EffectRegistry.EXHAUSTION);
                                }
                                HelperMethods.spawnParticlesPlane(
                                        player.level(),
                                        ParticleTypes.CLOUD,
                                        player.blockPosition(),
                                        radius-2, 0, 1, 0 );
                            }
                        }
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
