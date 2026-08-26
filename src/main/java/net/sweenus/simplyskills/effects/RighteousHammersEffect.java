package net.sweenus.simplyskills.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.util.HelperMethods;

import java.util.List;
import java.util.Objects;

public class RighteousHammersEffect extends MobEffect {
    public RighteousHammersEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % Math.max((22 - (amplifier * 2)), 1) == 0) {
            int radius = 4;
            AABB box = HelperMethods.createBox(livingEntity, radius);

            livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                    .filter(Objects::nonNull)
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> livingEntity instanceof ServerPlayer)
                    .forEach(entity -> {
                        LivingEntity le = (LivingEntity) entity;
                        ServerPlayer playerEntity = (ServerPlayer) livingEntity;
                        if (HelperMethods.checkFriendlyFire(le, playerEntity)) {
                            le.invulnerableTime = 0;
                            le.hurt(playerEntity.level().damageSources().playerAttack(playerEntity),
                                    (float) playerEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8f);
                            le.invulnerableTime = 0;
                        }
                    });

            if (livingEntity instanceof Player player && AscendancyAbilities.getAscendancyPoints(player) > 29) {
                int radiusThrow = (int) (radius * 2.5);
                AABB box2 = HelperMethods.createBox(livingEntity, radiusThrow);
                ServerLevel world = (ServerLevel) livingEntity.level();
                List<Entity> nearbyEntities = world.getEntities(livingEntity, box2, EntitySelector.LIVING_ENTITY_STILL_ALIVE);

                if (!nearbyEntities.isEmpty()) {
                    Entity randomEntity = nearbyEntities.get(player.getRandom().nextInt(nearbyEntities.size()));

                    if (randomEntity instanceof LivingEntity ee) {
                        if (HelperMethods.checkFriendlyFire(ee, player)) {
                            BlockPos blockPos = ee.blockPosition().above(1);
                            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:righteous_hammer_projectile", 20, ee, blockPos);
                        }
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
