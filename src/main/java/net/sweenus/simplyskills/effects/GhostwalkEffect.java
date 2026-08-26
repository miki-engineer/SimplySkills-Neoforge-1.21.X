package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class GhostwalkEffect extends MobEffect {
    public GhostwalkEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.GHOSTWALK)) {
                MobEffectInstance ghostwalk = player.getEffect(EffectRegistry.GHOSTWALK);
                if (ghostwalk == null)
                    return true;

                double bullrushVelocity = 0.02 * ( 61 - ghostwalk.getDuration());
                int bullrushRadius = 10;
                double damageModifier = 0.8 + (0.03 * AscendancyAbilities.getAscendancyPoints(player));
                int bullrushHitFrequency = 5;

                HelperMethods.spawnOrbitParticles(player.serverLevel(), player.position(), ParticleTypes.SMOKE, 1, 20);

                if (ghostwalk.getDuration() % 15 == 0) {
                    player.level().playSound(null, player, SoundRegistry.SPELL_RADIANT_EXPIRE,
                            SoundSource.PLAYERS, 0.3f, 1.0f);
                }

                if (ghostwalk.getDuration() > 10 && ghostwalk.getDuration() < 57) {
                    player.setDeltaMovement(livingEntity.getLookAngle().scale(+bullrushVelocity));
                    player.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().z);
                    player.setNoGravity(true);
                    player.hurtMarked = true;
                } else if (ghostwalk.getDuration() > 56) {
                    player.setDeltaMovement(livingEntity.getDeltaMovement().x, livingEntity.getDeltaMovement().y + 0.2, livingEntity.getDeltaMovement().z);
                    player.hurtMarked = true;
                }
                double damage = (HelperMethods.getHighestAttributeValue(player) * damageModifier);

                AABB box = HelperMethods.createBox(player, bullrushRadius);
                int chance = 15;
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                            if (ghostwalk.getDuration() % bullrushHitFrequency == 0 && ((LivingEntity) entities).getRandom().nextInt(100) < chance) {
                                le.invulnerableTime = 0;
                                le.hurt(player.damageSources().playerAttack(player), (float) damage);
                                HelperMethods.spawnWaistHeightParticles((ServerLevel) player.level(), ParticleTypes.SOUL, player, le, 20);
                                le.invulnerableTime = 0;
                                if (AscendancyAbilities.getAscendancyPoints(player) > 29)
                                    player.heal((float)damage / 2);
                                return true;
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
        if (!entity.level().isClientSide()) {
            if (entity instanceof Player player && ModList.get().isLoaded("simplyswords"))
                SimplySwordsGemEffects.warStandard(player);
            entity.setNoGravity(false);
        }
    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (!entity.level().isClientSide() && entity instanceof  Player player) {
            HelperMethods.incrementStatusEffect(player, EffectRegistry.SOULSHOCK, 60, 1 + (AscendancyAbilities.getAscendancyPoints(player) / 10), 9);
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
