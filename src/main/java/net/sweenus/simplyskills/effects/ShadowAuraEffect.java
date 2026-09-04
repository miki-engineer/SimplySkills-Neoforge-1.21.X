package net.sweenus.simplyskills.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.abilities.NecromancerAbilities;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ShadowAuraEffect extends MobEffect {
    public ShadowAuraEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            HelperMethods.spawnOrbitParticles((ServerLevel) livingEntity.level(), livingEntity.position(), ParticleTypes.SMOKE, 0.5, 3);
            if (livingEntity.tickCount % Math.max((22 - (amplifier * 2)), 1) == 0) {
                int radius = 2;
                AABB box = HelperMethods.createBox(livingEntity, radius);
                Player effectivePlayer = getPlayerEntity(livingEntity);

                livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                        .filter(Objects::nonNull)
                        .filter(entity -> entity instanceof LivingEntity)
                        .forEach(entity -> {
                            LivingEntity le = (LivingEntity) entity;

                            if (effectivePlayer != null && HelperMethods.checkFriendlyFireAOE(le, effectivePlayer)) {
                                le.invulnerableTime = 0;
                                le.hurt(effectivePlayer.level().damageSources().indirectMagic(effectivePlayer, effectivePlayer),
                                        (float) SpellPower.getSpellPower(SpellSchools.SOUL, effectivePlayer).randomValue() * ((float) amplifier / 5));
                                HelperMethods.spawnWaistHeightParticles((ServerLevel) livingEntity.level(), ParticleTypes.SMOKE, livingEntity, le, 5);
                                le.invulnerableTime = 0;
                            }
                        });
                if (effectivePlayer != null) {
                    float damage = (1 + (float) SpellPower.getSpellPower(SpellSchools.SOUL, effectivePlayer).randomValue() * ((float) amplifier / 10));
                    if ((livingEntity.getHealth() - (damage * 2)) < 0 && livingEntity instanceof TamableAnimal minion) {
                        NecromancerAbilities.effectShadowCombust(effectivePlayer, minion);
                        minion.removeEffect(EffectRegistry.SHADOWAURA);
                    } else livingEntity.setHealth((livingEntity.getHealth() - damage));
                }
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }

    @Nullable
    private static Player getPlayerEntity(LivingEntity livingEntity) {
        Player effectivePlayer = null;

        if (livingEntity instanceof ServerPlayer) {
            effectivePlayer = (ServerPlayer) livingEntity;
        } else if (livingEntity instanceof TamableAnimal) {
            Entity owner = ((TamableAnimal) livingEntity).getOwner();
            if (owner instanceof ServerPlayer) {
                effectivePlayer = (ServerPlayer) owner;
            }
        }
        return effectivePlayer;
}


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
