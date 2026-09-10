package net.sweenus.simplyskills.mixins;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.sweenus.simplyskills.entities.DreadglareEntity;
import net.sweenus.simplyskills.entities.GreaterDreadglareEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FollowOwnerGoal.class)
public abstract class FollowOwnerGoalMixin {

    @Shadow @Final private TamableAnimal tamable;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;shouldTryTeleportToOwner()Z"))
    public boolean simplyskills$canTarget(TamableAnimal tamable) {
        if (!tamable.level().isClientSide()
                && (tamable instanceof DreadglareEntity || tamable instanceof GreaterDreadglareEntity)
                && tamable.getOwner() != null && tamable.distanceToSqr(tamable.getOwner()) < 576)
            return false;

        return tamable.shouldTryTeleportToOwner();
    }
}
