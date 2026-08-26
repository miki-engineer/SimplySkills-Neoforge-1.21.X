package net.sweenus.simplyskills.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class EarthshakerEffect extends MobEffect {
    public EarthshakerEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
    private float fallDistance;


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            int radius = 3;
            float damageIncrease = SimplySkills.warriorConfig.passiveWarriorHeavyWeightDamageIncreasePerTick;
            double damage_multiplier = 0.5;
            double baseDamage = 1 + (livingEntity.getArmorValue() * damage_multiplier);
            DamageSource damageSource = livingEntity.damageSources().generic();
            fallDistance += damageIncrease;

            if (livingEntity.onGround()) {

                AABB box = HelperMethods.createBox(livingEntity, radius);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && !livingEntity.hasEffect(MobEffects.SLOW_FALLING)){
                            double damage = baseDamage;
                            if (livingEntity instanceof Player player) {
                                damageSource = player.damageSources().playerAttack(player);
                                boolean heavyWeight = HelperMethods.isUnlocked("simplyskills:tree",
                                        SkillReferencePosition.warriorHeavyWeight, player);
                                if (heavyWeight)
                                    damage +=fallDistance;
                                if (!HelperMethods.checkFriendlyFire(le, player))
                                    continue;
                            }

                            le.setDeltaMovement((le.getX() - livingEntity.getX()) /4,  (le.getY() - livingEntity.getY()) /4, (le.getZ() - livingEntity.getZ()) /4);
                            le.invulnerableTime = 0;
                            le.hurt(damageSource, (float) damage);
                            le.invulnerableTime = 0;
                        }
                    }
                }
                livingEntity.level().playSound(null, livingEntity, SoundRegistry.SOUNDEFFECT14,
                        SoundSource.PLAYERS, 0.3f, 1.1f);
                fallDistance = 0;
                HelperMethods.spawnParticlesPlane(
                        livingEntity.level(),
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        livingEntity.blockPosition(),
                        radius, 0, 1, 0 );
                livingEntity.removeEffect(EffectRegistry.EARTHSHAKER);
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
