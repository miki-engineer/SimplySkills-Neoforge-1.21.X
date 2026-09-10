package net.sweenus.simplyskills.client.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.LightEmission;
import net.sweenus.simplyskills.SimplySkills;


public class MagicCircleRenderer implements CustomModelStatusEffect.Renderer {
    public static final ResourceLocation modelId_overlay = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/magic_circle");


    private static final RenderType OVERLAY_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.GLOW, true);


    @Override
    public void renderEffect(long time, int amplifier, LivingEntity livingEntity, float delta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light) {
        float entitySize = livingEntity.getBbWidth() / 2;
        float yOffset = 0.90F +entitySize;
        float effectTime = livingEntity.level().getGameTime() + delta;
        float speed = 4F;
        boolean prone = livingEntity.isFallFlying() || livingEntity.isVisuallyCrawling() || livingEntity.isVisuallySwimming();

        float overlayScale = 1.9F + entitySize;
        matrixStack.pushPose();
        matrixStack.translate(0, yOffset, 0);
        if (prone)
            overlayScale = 0;
        matrixStack.scale(overlayScale, overlayScale, overlayScale);

        matrixStack.mulPose(Axis.YP.rotationDegrees(time * speed - 45.0F));

        CustomModels.render(OVERLAY_RENDER_LAYER, Minecraft.getInstance().getItemRenderer(), modelId_overlay,
                matrixStack, vertexConsumers, light, livingEntity.getId());
        matrixStack.popPose();
    }
}
