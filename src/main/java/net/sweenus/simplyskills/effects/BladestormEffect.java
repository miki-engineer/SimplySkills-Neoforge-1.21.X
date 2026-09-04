package net.sweenus.simplyskills.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.Objects;

public class BladestormEffect extends MobEffect {
    public BladestormEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % Math.max((22 - amplifier), 1) == 0) {
            int radius = 2;
            AABB box = HelperMethods.createBox(livingEntity, radius);

            livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                    .filter(Objects::nonNull)
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> livingEntity instanceof ServerPlayer)
                    .forEach(entity -> {
                        LivingEntity le = (LivingEntity) entity;
                        ServerPlayer playerEntity = (ServerPlayer) livingEntity;
                        if (HelperMethods.checkFriendlyFireAOE(le, playerEntity)) {
                            le.invulnerableTime = 0;
                            le.hurt(playerEntity.level().damageSources().playerAttack(playerEntity),
                                    (float) playerEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.3f);
                            le.invulnerableTime = 0;

                            if (playerEntity.getRandom().nextInt(100) < 3
                                    && HelperMethods.isUnlocked("simplyskills:rogue",
                                    SkillReferencePosition.rogueBladestormSiphon, playerEntity))
                                playerEntity.heal(1);

                        }
                    });
        }
        super.applyEffectTick(livingEntity, amplifier);
        return true;
}


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
