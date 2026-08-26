package net.sweenus.simplyskills.entities.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import net.sweenus.simplyskills.util.HelperMethods;

public class DirectionalFlightMoveControl extends MoveControl {
    private final int maxPitchChange;
    private final boolean noGravity;
    private final double minAltitudeAboveGround = 4.0; // Minimum altitude above the ground
    private long lastAttackTime = 0; // Timestamp of the last attack
    private static final long ATTACK_COOLDOWN = 5000; // 2 seconds cooldown after attack
    private double yOffset = 0;

    public DirectionalFlightMoveControl(Mob entity, int maxPitchChange, boolean noGravity) {
        super(entity);
        this.maxPitchChange = maxPitchChange;
        this.noGravity = noGravity;
    }

    @Override
    public void tick() {
        if (this.noGravity) {
            this.mob.setNoGravity(true);
        }

        if (this.mob.getTarget() != null)
            yOffset = 0;
        else yOffset = 4;

        if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;

            if (this.mob.getTarget() == null) {
                // Choose a random nearby position to move to
                this.wantedX = this.mob.getX() + this.mob.getRandom().nextGaussian() * 5;
                this.wantedY = this.mob.getY() + this.mob.getRandom().nextGaussian() * 5;
                this.wantedZ = this.mob.getZ() + this.mob.getRandom().nextGaussian() * 5;
            } else {
                this.wantedX = this.mob.getTarget().getX();
                this.wantedY = this.mob.getTarget().getY();
                this.wantedZ = this.mob.getTarget().getZ();
            }
            this.wantedY += Math.sin(this.mob.tickCount * 0.3) * 0.5;

            double d = this.wantedX - this.mob.getX();
            double e = (this.wantedY+yOffset) - this.mob.getY();
            double f = this.wantedZ - this.mob.getZ();
            double g = d * d + e * e + f * f;

            if (g < 2.500000277905201E-7) {
                this.mob.setYya(0.0f);
                this.mob.setZza(0.0f);
                // Choose a random nearby position to move to
                this.wantedX = this.mob.getX() + this.mob.getRandom().nextGaussian() * 5;
                this.wantedY = this.mob.getY() + this.mob.getRandom().nextGaussian() * 5;
                this.wantedZ = this.mob.getZ() + this.mob.getRandom().nextGaussian() * 5;
                return;
            }

            float h = (float)(Mth.atan2(f, d) * (180.0 / Math.PI)) - 90.0f;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), h, 90.0f));

            float i = this.mob.onGround() ? (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) : (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
            this.mob.setSpeed(i);

            double j = Math.sqrt(d * d + f * f);

            // Check if the entity has an attack target or is within the cooldown period after an attack
            boolean recentlyAttacked = this.mob.getTarget() != null && (System.currentTimeMillis() - lastAttackTime) > ATTACK_COOLDOWN;
            double groundDistance = HelperMethods.getGroundDistance(this.mob);

            // Check if the entity is too close to the ground
            if (groundDistance < this.minAltitudeAboveGround && !recentlyAttacked) {
                // Adjust targetY to move upwards smoothly
                this.wantedY = this.mob.getY() + (this.minAltitudeAboveGround - groundDistance);
                e = this.wantedY - this.mob.getY();
            }

            if (Math.abs(e) > 1.0E-5f || Math.abs(j) > 1.0E-5f) {
                float k = (float)(-(Mth.atan2(e, j) * (180.0 / Math.PI)));
                this.mob.setXRot(this.rotlerp(this.mob.getXRot(), k, this.maxPitchChange));
                this.mob.setYya(e > 0.0 ? i : -i);
            }
        } else {
            this.mob.setYya(0.1f);
            Vec3 velocity = this.mob.getDeltaMovement();
            this.mob.setXRot((float) (-(Mth.atan2(velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)) * (180.0 / Math.PI))));
            this.mob.setZza(0.1f);
            this.mob.setYRot((float) (Mth.atan2(velocity.z, velocity.x) * (180.0 / Math.PI)) - 90.0F);
        }
    }
    // Call this method when the entity hits an attack target
    public void onAttack() {
        this.lastAttackTime = System.currentTimeMillis();
    }
}
