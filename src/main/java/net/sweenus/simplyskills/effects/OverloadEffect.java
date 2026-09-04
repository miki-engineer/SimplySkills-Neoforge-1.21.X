package net.sweenus.simplyskills.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class OverloadEffect extends MobEffect {
    public OverloadEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            int radius = 3;
            double damage = Math.min((livingEntity.getMaxHealth() / 6) * (1 + SpellPower.getSpellPower(SpellSchools.SOUL, livingEntity).randomValue()), livingEntity.getMaxHealth());
            DamageSource damageSource = livingEntity.damageSources().generic();
            DamageSource damageSourceMagic = livingEntity.damageSources().indirectMagic(livingEntity, livingEntity);

            if (livingEntity.getEffect(EffectRegistry.OVERLOAD).getAmplifier() >= 5) {

                AABB box = HelperMethods.createBox(livingEntity, radius);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if (entities instanceof LivingEntity le){
                            if (livingEntity instanceof Player player) {
                                damageSource = player.damageSources().playerAttack(player);
                                if (!HelperMethods.checkFriendlyFireAOE(le, player))
                                    continue;
                            }

                            le.setDeltaMovement((le.getX() - livingEntity.getX()) /4,  (le.getY() - livingEntity.getY()) /4, (le.getZ() - livingEntity.getZ()) /4);
                            le.invulnerableTime = 0;
                            le.hurt(damageSourceMagic, (float) damage);
                            le.invulnerableTime = 0;
                        }
                    }
                }
                livingEntity.hurt(damageSourceMagic, (livingEntity.getMaxHealth() - 2));
                livingEntity.level().playSound(null, livingEntity, SoundRegistry.SOUNDEFFECT14,
                        SoundSource.PLAYERS, 0.8f, 0.9f);
                HelperMethods.spawnParticlesPlane(
                        livingEntity.level(),
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        livingEntity.blockPosition(),
                        radius, 0, 1, 0 );
                livingEntity.removeEffect(EffectRegistry.OVERLOAD);
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
