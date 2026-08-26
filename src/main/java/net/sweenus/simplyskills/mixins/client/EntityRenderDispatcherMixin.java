package net.sweenus.simplyskills.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.spell_engine.api.effect.Synchronized;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    //Prevents shadow flickering visible when effects are reapplied

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void simplyskills$renderShadow(PoseStack matrices, MultiBufferSource vertexConsumers, Entity entity, float opacity, float tickDelta, LevelReader world, float radius, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            if (Synchronized.effectsOf(livingEntity).toString().contains("StealthEffect")) {
                ci.cancel();
            }
        }
    }
}