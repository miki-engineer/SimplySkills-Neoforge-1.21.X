package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

public class ImmobilizingAuraEffect extends MobEffect {
    public ImmobilizingAuraEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.tickCount % 20 == 0) {
                int radius = 2;

                AABB box = HelperMethods.createBox(livingEntity, radius);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if ((entities instanceof LivingEntity le)) {
                            if (livingEntity instanceof OwnableEntity te) {

                                if (te.getOwner() == null)
                                    break;
                                if (te.getOwner() instanceof Player pe) {
                                    if (HelperMethods.checkFriendlyFire(le, pe)) {
                                        le.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZE, 25, 0, false, false, true));
                                    }
                                }
                            }
                            else if (livingEntity instanceof Player playerEntity) {

                                if (HelperMethods.checkFriendlyFire(le, playerEntity)) {
                                    le.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZE, 25, 0, false, false, true));
                                }
                            }
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
