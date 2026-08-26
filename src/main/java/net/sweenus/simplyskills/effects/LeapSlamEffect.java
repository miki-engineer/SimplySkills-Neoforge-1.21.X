package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsRequiredMethods;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.Objects;

public class LeapSlamEffect extends MobEffect {
    public LeapSlamEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof Player player) {
                int ability_timer = Objects.requireNonNull(player.getEffect(EffectRegistry.LEAPSLAM)).getDuration();
                int radius = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamRadius;
                int immobilizeDuration = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamImmobilizeDuration;
                double leapVelocity = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamVelocity;
                double height = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamHeight;
                double descentVelocity = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamDescentVelocity;
                double damage_multiplier = SimplySkills.berserkerConfig.signatureBerserkerLeapSlamDamageModifier;
                double damage = (HelperMethods.getAttackDamage(livingEntity.getMainHandItem()) * damage_multiplier);

                if (ability_timer >= 60) {
                    player.setDeltaMovement(livingEntity.getLookAngle().scale(+leapVelocity));
                player.setDeltaMovement(livingEntity.getDeltaMovement().x, height, livingEntity.getDeltaMovement().z);
                player.hurtMarked = true;
                }
                else if (ability_timer <= 50) {
                    //player.setVelocity(livingEntity.getRotationVector().multiply(+1.01));
                    player.setDeltaMovement(livingEntity.getDeltaMovement().x, -descentVelocity, livingEntity.getDeltaMovement().z);
                    player.hurtMarked = true;

                    if (player.onGround()) {

                        AABB box = HelperMethods.createBox(player, radius * 2);
                        for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                            if (entities != null) {
                                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                                    if (HelperMethods.isUnlocked("simplyskills:berserker",
                                            SkillReferencePosition.berserkerSpecialisationBerserkingLeapPull, player))
                                        le.setDeltaMovement((player.getX() - le.getX()) / 4, (player.getY() - le.getY()) / 4, (player.getZ() - le.getZ()) / 4);
                                    else
                                        le.setDeltaMovement((le.getX() - player.getX()) / 4, (le.getY() - player.getY()) / 4, (le.getZ() - player.getZ()) / 4);

                                    le.hurt(player.damageSources().playerAttack(player), (float) damage);
                                    player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT14,
                                            SoundSource.PLAYERS, 0.3f, 1.1f);
                                    if (HelperMethods.isUnlocked("simplyskills:berserker",
                                            SkillReferencePosition.berserkerSpecialisationBerserkingLeapImmob, player))
                                        le.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZE, immobilizeDuration, 0, false, false, true));
                                }
                            }
                        }
                        HelperMethods.spawnParticlesPlane(
                                player.level(),
                                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                player.blockPosition(),
                                radius, 0, 1, 0);
                        player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT14,
                                SoundSource.PLAYERS, 0.5f, 0.9f);
                        if (ModList.get().isLoaded("simplyswords")
                                && SimplySwordsGemEffects.passVersionCheck()) {
                            int resetChance = SimplySwordsRequiredMethods.leapingChance;
                            if (SimplySwordsGemEffects.doSignatureGemEffects(player, "leaping")
                                    && player.getRandom().nextInt(100) < resetChance) {
                                player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT15,
                                        SoundSource.PLAYERS, 0.5f, 1.1f);
                                player.addEffect(new MobEffectInstance(EffectRegistry.LEAPSLAM,
                                        SimplySkills.berserkerConfig.signatureBerserkerLeapSlamDuration, 0, false, false, true));
                            }
                            else player.removeEffect(EffectRegistry.LEAPSLAM);
                        }
                        else player.removeEffect(EffectRegistry.LEAPSLAM);
                    }
                }
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
