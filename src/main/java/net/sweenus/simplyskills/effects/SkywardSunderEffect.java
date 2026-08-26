package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
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
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class SkywardSunderEffect extends MobEffect {
    public SkywardSunderEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.SKYWARDSUNDER)) {
                MobEffectInstance skywardSunder = player.getEffect(EffectRegistry.SKYWARDSUNDER);
                if (skywardSunder == null)
                    return true;

                double bullrushVelocity = 0.1 * ( 46 - skywardSunder.getDuration());
                int bullrushRadius = 2;
                double damageModifier = 0.9 + (0.03 * AscendancyAbilities.getAscendancyPoints(player));
                int slash_1 = 20+10;
                int slash_2 = 5+10;

                if (skywardSunder.getDuration() > slash_1) {
                    player.setDeltaMovement(livingEntity.getLookAngle().scale(+bullrushVelocity));
                    player.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().z);
                    player.hurtMarked = true;
                }
                if (skywardSunder.getDuration() == slash_1 + 12) {
                    SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:skyward_sunder", 3, player, null);
                    player.level().playSound(null, player, SoundRegistry.OBJECT_IMPACT_THUD_REPEAT,
                            SoundSource.PLAYERS, 0.6f, 1.0f);
                }
                if (skywardSunder.getDuration() == slash_1) {
                    player.setDeltaMovement(0, 1.2, 0);
                    player.hurtMarked = true;
                }
                if (skywardSunder.getDuration() == slash_2) {
                    player.setDeltaMovement(0, -1.2, 0);
                    player.hurtMarked = true;
                }
                if (skywardSunder.getDuration() == slash_2 + 5) {
                    SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:skyward_sunder_slam", 3, player, null);
                    if (ModList.get().isLoaded("prominent")) // iframes for fall dmg
                        HelperMethods.incrementStatusEffect(player, EffectRegistry.BARRIER, 60, 0, 1);
                }
                if (skywardSunder.getDuration() == slash_2 - 2) {
                    player.level().playSound(null, player, SoundRegistry.DAMAGE_03,
                            SoundSource.PLAYERS, 0.8f, 1.0f);
                }
                if (skywardSunder.getDuration() == 12) {
                    player.level().playSound(null, player, SoundRegistry.SPELL_EARTH_PUNCH,
                            SoundSource.PLAYERS, 0.6f, 1.0f);
                }
                if (skywardSunder.getDuration() == 2) {
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            player.blockPosition(),
                            bullrushRadius, 0, 1, 0);
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            ParticleTypes.POOF,
                            player.blockPosition(),
                            bullrushRadius, 0, 1, 0);
                }


                double damage = (HelperMethods.getHighestAttributeValue(player) * damageModifier);

                AABB box = new AABB(player.getX() + bullrushRadius, player.getY() + (float) bullrushRadius, player.getZ() + bullrushRadius,
                        player.getX() - bullrushRadius, player.getY() - (float) bullrushRadius, player.getZ() - bullrushRadius);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player) && (skywardSunder.getDuration() == 1 || skywardSunder.getDuration() == slash_2 || skywardSunder.getDuration() == slash_1)) {
                            le.invulnerableTime = 0;
                            le.hurt(player.damageSources().playerAttack(player), (float) damage);
                            le.invulnerableTime = 0;
                            le.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                            le.hurtMarked = true;

                            HelperMethods.spawnParticlesPlane(
                                    player.level(),
                                    ParticleTypes.CLOUD,
                                    player.blockPosition(),
                                    bullrushRadius - 1, 0, 1, 0);
                        }
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)
                                && skywardSunder.getDuration() > slash_1 && skywardSunder.getDuration() % 2 == 0) {

                            if (AscendancyAbilities.getAscendancyPoints(player) > 30 && le.isAlive())
                                le.addEffect(new MobEffectInstance(EffectRegistry.DEATHMARK, 60, 0));

                            le.invulnerableTime = 0;
                            le.hurt(player.damageSources().playerAttack(player), 0.5f);
                            le.invulnerableTime = 0;
                            le.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                            le.hurtMarked = true;
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
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (!entity.level().isClientSide() && entity instanceof  Player player) {
            if (player.hasEffect(EffectRegistry.MIGHT)) {
                MobEffectInstance mightEffect = player.getEffect(EffectRegistry.MIGHT);
                if (mightEffect !=null) {
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.BARRIER, mightEffect.getDuration(), mightEffect.getAmplifier(), 9);
                }
            }
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
