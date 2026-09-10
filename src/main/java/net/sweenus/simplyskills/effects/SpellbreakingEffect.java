package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.spell_engine.entity.SpellProjectile;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.util.HelperMethods;

public class SpellbreakingEffect extends MobEffect {
    public SpellbreakingEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int frequency = SimplySkills.warriorConfig.passiveWarriorSpellbreakerFrequency;
            if (livingEntity.tickCount % frequency == 0) {
                int radius = SimplySkills.warriorConfig.passiveWarriorSpellbreakerRadius;

                AABB box = HelperMethods.createBox(livingEntity, radius);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if (entities instanceof SpellProjectile pe) {
                            if (pe.getOwner() instanceof LivingEntity livingOwner) {
                                if (livingEntity instanceof Player player) {
                                    if (!HelperMethods.checkFriendlyFireAOE(livingOwner, player))
                                        continue;
                                }
                            }
                            if (livingEntity instanceof Player player) {
                                pe.level().explode(
                                        pe,
                                        pe.level().damageSources().explosion(pe, player),
                                        HelperMethods.getFriendlyFireExplosionDamageCalculator(player),
                                        pe.getX(), pe.getY(), pe.getZ(),
                                        0.2f, false, Level.ExplosionInteraction.NONE);
                            }
                            else pe.level().explode(pe, pe.getX(), pe.getY(), pe.getZ(), 0.2f, false, Level.ExplosionInteraction.NONE);
                            pe.discard();
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
