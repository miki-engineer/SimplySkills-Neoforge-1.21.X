package net.sweenus.simplyskills.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sweenus.simplyskills.abilities.AscendancyAbilities;
import net.sweenus.simplyskills.abilities.NecromancerAbilities;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.entities.ai.DirectionalFlightMoveControl;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public class WraithEntity extends TamableAnimal implements NeutralMob, FlyingAnimal {
    public static int lifespan = 2400;
    public static Entity lookTarget = null;
    public WraithEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new DirectionalFlightMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createWraithAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.FLYING_SPEED, 1.6f)
                .add(Attributes.MOVEMENT_SPEED, 0.6f)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1)
                .add(Attributes.FOLLOW_RANGE, 10.0);
    }
    @Override
    public void tick() {
        if (!this.level().isClientSide()) {
            boolean ownerNotInWorld = true;

            if (this.getOwnerUUID() != null) {
                Player owner = this.level().getPlayerByUUID(this.getOwnerUUID());
                ownerNotInWorld = (owner == null || !owner.isAlive());
            }

            if (this.tickCount > lifespan || (this.tickCount > 120 && (this.getOwner() == null || ownerNotInWorld))) {
                this.hurt(this.damageSources().generic(), this.getMaxHealth());
                this.remove(RemovalReason.UNLOADED_WITH_PLAYER);
            }

            this.isFree(0, 0, 0);

            if (!this.isNoGravity()) {
                this.setNoGravity(true);
            }

            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();

            if (this.getTarget() == null && this.getOwner() != null)
                this.setTarget(this.getOwner());
            else if (this.getTarget() != null && !this.getTarget().equals(this.getOwner()) && this.distanceTo(this.getTarget()) > 20)
                this.setTarget(this.getOwner());

            AABB box = HelperMethods.createBoxHeight(this, 16);
            int frequency = (20+ this.getRandom().nextInt(30));
            if (this.tickCount % frequency == 0 && this.getOwner() != null && this.getOwner().isAlive() && this.getOwner() instanceof Player player) {
                Level world = this.level();
                Entity closestEntity = world.getEntities(this, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                        .filter(entity -> !(entity instanceof TamableAnimal tameableEntity &&
                                tameableEntity.isTame() &&
                                tameableEntity.getOwnerUUID() != null &&
                                tameableEntity.getOwnerUUID().equals(this.getOwnerUUID())))
                        .filter(entity -> !(entity instanceof Player playerEntity &&
                                playerEntity.getUUID().equals(this.getOwnerUUID())))
                        .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(this)))
                        .orElse(null);

                if (closestEntity != null) {
                    if ((closestEntity instanceof LivingEntity ee) && !(closestEntity instanceof AgeableMob)) {
                        if (HelperMethods.checkFriendlyFire(ee, player)) {

                            if (HelperMethods.isUnlocked("simplyskills:necromancer", SkillReferencePosition.necromancerSpecialisationWitherWraiths, player))
                                SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:minion_soul_spell_wither", 32, ee, null);
                            else if (HelperMethods.isUnlocked("simplyskills:necromancer", SkillReferencePosition.necromancerSpecialisationFrostWraiths, player))
                                SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:minion_soul_spell_frost", 32, ee, null);
                            else
                                SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:minion_soul_spell", 32, ee, null);

                            HelperMethods.spawnWaistHeightParticles((ServerLevel) world, ParticleTypes.SMOKE, this, ee, 20);
                            lookTarget = ee;

                            int chance = this.getRandom().nextInt(100);
                            int chanceCheck = HelperMethods.countHarmfulStatusEffects(this) * 5;

                            if (chance < chanceCheck && HelperMethods.isUnlocked("simplyskills:necromancer", SkillReferencePosition.necromancerSpecialisationWraithLegion, player)) {
                                SimplyStatusEffectInstance agonyEffect = new SimplyStatusEffectInstance(
                                        EffectRegistry.AGONY, 200 + AscendancyAbilities.getAscendancyPoints(player), 0, false,
                                        false, true);
                                agonyEffect.setSourceEntity(player);
                                ee.addEffect(agonyEffect);
                                player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_04,
                                        SoundSource.PLAYERS, 0.1f, 1.0f);
                            }

                            return;
                        }
                    }
                }
            }
            if (lookTarget != null) {
                this.lookAt(lookTarget, 90f, 10f);
                Vec3 direction = new Vec3(lookTarget.getX() - this.getX(), 0, lookTarget.getZ() - this.getZ());
                // Calculate the yaw angle towards the look target (in degrees)
                float targetYaw = (float)(Mth.atan2(direction.z, direction.x) * (180 / Math.PI)) - 90.0F;
                this.setYBodyRot(targetYaw);
                this.yHeadRot = targetYaw;
            }
        }

        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (Objects.equals(source.getEntity(), this.getOwner()))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        MoveControl moveControl = this.getMoveControl();
        if (moveControl instanceof DirectionalFlightMoveControl) {
            ((DirectionalFlightMoveControl) moveControl).onAttack();
        }
        target.invulnerableTime = 0;
        return super.doHurtTarget(target);
    }
    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && this.getOwner() != null && this.getOwner() instanceof Player player) {
            NecromancerAbilities.effectNecromancerEnrage(this, player);
            NecromancerAbilities.effectNecromancerDeathEssence(player);
            NecromancerAbilities.effectShadowCombust(player, this);
            NecromancerAbilities.effectEndlessServitude(player, this);
        }
        super.die(damageSource);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.0D, 8.0F, 12.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        //this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        //this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        //this.targetSelector.add(3, new RevengeGoal(this));
        //this.targetSelector.add(4, new ActiveTargetGoal<>(this, HostileEntity.class, false));

    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {

    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angryAt) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        // Do not call super to prevent fall damage
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        // Return false to prevent fall damage
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world) {

        };
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanFloat(false);
        birdNavigation.setCanPassDoors(false);
        return birdNavigation;
    }
}
