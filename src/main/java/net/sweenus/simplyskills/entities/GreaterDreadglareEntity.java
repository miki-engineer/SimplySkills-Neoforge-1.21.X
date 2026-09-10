package net.sweenus.simplyskills.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.sweenus.simplyskills.abilities.NecromancerAbilities;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.entities.ai.DirectionalFlightMoveControl;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class GreaterDreadglareEntity extends TamableAnimal implements NeutralMob, FlyingAnimal {
    public static int lifespan = 2400;
    public GreaterDreadglareEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new DirectionalFlightMoveControl(this, 1, true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createGreaterDreadglareAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.FLYING_SPEED, 1.6f)
                .add(Attributes.MOVEMENT_SPEED, 0.6f)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 48.0);
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

            if (this.getTarget() != null && this.getOwnerUUID() != null) {
                Player owner = this.level().getPlayerByUUID(this.getOwnerUUID());
                if (owner != null && this.getTarget() != owner) {
                    Vec3 entityLookVec = this.getViewVector(1.0F);
                    Vec3 toTargetVec = this.getTarget().position().subtract(this.position()).normalize();
                    double dotProduct = entityLookVec.dot(toTargetVec);
                    double threshold = Math.cos(Math.toRadians(20)); // Tolerance

                    if (dotProduct > threshold && this.distanceTo(getTarget()) > 1.5) {
                        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2;
                        float distance = this.distanceTo(getTarget());
                        DamageSource damageSource = this.damageSources().playerAttack(owner);
                        Level world = this.level();

                        int timeDifference = this.getRandom().nextInt(10);

                        if (this.tickCount % (10 + timeDifference) == 0) {
                            world.playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM,
                                    this.getSoundSource(), 0.5f, 1.6f);
                            HelperMethods.spawnDirectionalParticles((ServerLevel) world, ParticleTypes.SONIC_BOOM, this, 5, distance);
                            HelperMethods.spawnDirectionalParticles((ServerLevel) world, ParticleTypes.POOF, this, 5, distance);
                            HelperMethods.damageEntitiesInTrajectory((ServerLevel) world, this, owner, distance, damage, damageSource);
                            this.setDeltaMovement(this.getLookAngle().reverse().scale(+0.8));
                            this.setDeltaMovement(this.getDeltaMovement().x, 0, this.getDeltaMovement().z);
                        }
                    }
                }
            }


            if (!this.isNoGravity()) {
                this.setNoGravity(true);
            }

            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();

            if (this.getTarget() == null && this.getOwner() != null)
                this.setTarget(this.getOwner());
            else if (this.getTarget() != null && !this.getTarget().equals(this.getOwner()) && this.distanceTo(this.getTarget()) > 20)
                this.setTarget(this.getOwner());
        }

        super.tick();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        MoveControl moveControl = this.getMoveControl();
        if (moveControl instanceof DirectionalFlightMoveControl) {
            ((DirectionalFlightMoveControl) moveControl).onAttack();
        }

        // Necromancer Blood Harvest
        if (this.getOwner() != null && this.getOwner() instanceof Player player) {

            if (target.equals(player))
                return false;

            float siphonAmount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6f;
            if (HelperMethods.isUnlocked("simplyskills:necromancer", SkillReferencePosition.necromancerSpecialisationBloodHarvest, player)) {
                this.heal(siphonAmount);
                player.heal(siphonAmount / 2);
            }
            if (target instanceof LivingEntity livingTarget)
                NecromancerAbilities.effectPestilence(player, this, livingTarget);
        }

        if (target instanceof LivingEntity livingTarget) {
            SimplyStatusEffectInstance tauntEffect = new SimplyStatusEffectInstance(
                    EffectRegistry.TAUNTED, 100, 0, false,
                    false, true);
            tauntEffect.setSourceEntity(this);
            livingTarget.addEffect(tauntEffect);
        }
        float random = (float) ((float) this.random.nextInt(3) * 0.1);
        this.level().playSound(null, this, SoundRegistry.MAW,
                SoundSource.PLAYERS, 0.1f, 0.8f + random);
        int mightStacks = HelperMethods.countHarmfulStatusEffects(this);
        if (mightStacks > 0)
            this.addEffect(new MobEffectInstance(EffectRegistry.MIGHT, 220, mightStacks - 1, false, false, false));

        target.invulnerableTime = 0;
        return super.doHurtTarget(target);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.0D, 20.0F, 2.0F));
        //this.goalSelector.add(2, new AttackWithOwnerGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false));

    }

    @Override
    public void aiStep() {
        super.aiStep();

        Vec3 velocity = this.getDeltaMovement();
        if (!velocity.equals(Vec3.ZERO)) {
            float yaw = (float) (Mth.atan2(velocity.z, velocity.x) * (180.0 / Math.PI)) - 90.0F;
            float pitch = (float) (-(Mth.atan2(velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)) * (180.0 / Math.PI)));

            this.setYRot(yaw);
            this.yBodyRot = yaw;
            this.setXRot(pitch);

        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (Objects.equals(source.getEntity(), this.getOwner()))
            return false;
        return super.hurt(source, amount);
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
