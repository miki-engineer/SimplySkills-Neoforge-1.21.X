package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class CyclonicCleaveEffect extends MobEffect {
    public CyclonicCleaveEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.CYCLONICCLEAVE)) {
                MobEffectInstance cyclonicCleave = player.getEffect(EffectRegistry.CYCLONICCLEAVE);
                if (cyclonicCleave == null)
                    return true;

                double bullrushVelocity = 0.05 * ( 39 - cyclonicCleave.getDuration());
                int bullrushRadius = 2;
                double damageModifier = 0.8 + (0.03 * AscendancyAbilities.getAscendancyPoints(player));
                int bullrushHitFrequency = 5;

                if (cyclonicCleave.getDuration() % 5 == 0 && cyclonicCleave.getDuration() < 25)
                    player.level().playSound(null, player, SoundEvents.PLAYER_ATTACK_STRONG,
                            SoundSource.PLAYERS, 1f, 1.1f);

                if (cyclonicCleave.getDuration() > 10) {
                    player.setDeltaMovement(livingEntity.getLookAngle().scale(+bullrushVelocity));
                    player.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().z);
                    player.hurtMarked = true;
                }
                double damage;
                if (ModList.get().isLoaded("prominent"))
                    damage = (player.getAttributeValue(Attributes.ATTACK_DAMAGE)
                            + player.getAttributeValue(Attributes.ATTACK_SPEED))
                            * damageModifier;
                else
                    damage = (HelperMethods.getHighestAttributeValue(player) * damageModifier);

                AABB box = HelperMethods.createBox(player, bullrushRadius);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null && cyclonicCleave.getDuration() < 30) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                            if (AscendancyAbilities.getAscendancyPoints(player) > 29)
                                le.setDeltaMovement((player.getX() - le.getX()) /4,  (player.getY() - le.getY()) /4, (player.getZ() - le.getZ()) /4);
                            if (cyclonicCleave.getDuration() % bullrushHitFrequency == 0) {
                                le.invulnerableTime = 0;
                                le.hurt(player.damageSources().playerAttack(player), (float) damage);
                                le.invulnerableTime = 0;
                                ParticleOptions particleType = ParticleTypes.CLOUD;
                                if (ModList.get().isLoaded("prominent"))
                                    particleType = ParticleTypes.PORTAL;

                                HelperMethods.spawnParticlesPlane(
                                        player.level(),
                                        particleType,
                                        player.blockPosition(),
                                        bullrushRadius -1, 0, 1, 0 );
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
