package net.sweenus.simplyskills.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.Synchronized;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class ArmorFeatureRendererMixin {



    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    public void simplyskills$renderArmor(PoseStack matrices, MultiBufferSource vertexConsumers,
                                         LivingEntity livingEntity, EquipmentSlot equipmentSlot, int i,
                                         HumanoidModel<LivingEntity> bipedEntityModel, CallbackInfo ci) {

        // Status Effects are not synchronised correctly between clients in a server environment.
        // We piggyback off Daedelus' SpellEngine synchronisation technique to get around this issue.

        if(livingEntity.isInvisible()) {
            //System.out.println("List of synchronised effects: " + Synchronized.effectsOf(livingEntity));
            if (Synchronized.effectsOf(livingEntity).toString().contains("StealthEffect")) {
                ci.cancel();
            }
        }
    }

}
