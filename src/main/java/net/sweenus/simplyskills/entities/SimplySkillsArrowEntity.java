package net.sweenus.simplyskills.entities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class SimplySkillsArrowEntity extends Arrow {
    public SimplySkillsArrowEntity(EntityType<? extends Arrow> entityType, Level world) {
        super(entityType, world);
    }

    private int life;

    @Override
    public void tick() {

        if (this.inGround && this.getOwner() != null) {

            this.tickDespawn();

            if ((this.getOwner() instanceof ServerPlayer serverPlayer)
                    && HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationArrowRainExplosive, serverPlayer)) {
                AABB box = HelperMethods.createBox(this, 1);
                for (Entity entities : this.level().getEntities(this, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                    if (entities != null && (this.getOwner() instanceof Player player)) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                            Explosion explosion = this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                                    1.0f, false, Level.ExplosionInteraction.NONE);
                            this.doPostHurtEffects(le);

                            if (HelperMethods.isUnlocked("simplyskills:ranger",
                                    SkillReferencePosition.rangerSpecialisationArrowRainElemental, player)) {
                                if (this.random.nextInt(100) < 25)
                                    le.hurt(SpellDamageSource.player(SpellSchools.FIRE, player), 5);
                                else if (this.random.nextInt(100) < 45)
                                    le.hurt(SpellDamageSource.player(SpellSchools.FROST, player), 5);
                                else if (this.random.nextInt(100) < 65)
                                    le.hurt(SpellDamageSource.player(SpellSchools.LIGHTNING, player), 5);
                            }

                            this.discard();

                        }
                    }
                }
            }
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {

        if (this.random.nextInt(100) < 80)
            this.discard();

        super.onHitBlock(blockHitResult);
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 600) {
            this.discard();
        }
    }


}
