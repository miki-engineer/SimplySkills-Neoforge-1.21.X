package net.sweenus.simplyskills.client.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomModels;
import net.sweenus.simplyskills.SimplySkills;


public class BarrierRenderer implements CustomModelStatusEffect.Renderer {
    public static final ResourceLocation modelId_base = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/barrier");

    private static final RenderType BASE_RENDER_LAYER =
            RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);


    @Override
    public void renderEffect(long time, int amplifier, LivingEntity livingEntity, float delta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light) {
        float yOffset = 0.68F;
        float proneOffset = 0.30F; // Things get weird when prone, and render code hurts my head
        boolean prone = livingEntity.isFallFlying() || livingEntity.isVisuallyCrawling() || livingEntity.isVisuallySwimming();
        matrixStack.pushPose();

        matrixStack.translate(0, yOffset, 0);
        if (prone)
            matrixStack.translate(0, -proneOffset, 0);

        // Apply rotation to match the entity's orientation
        matrixStack.mulPose(Axis.YP.rotationDegrees(-livingEntity.yBodyRot));
        if (prone)
            matrixStack.mulPose(Axis.XP.rotationDegrees(livingEntity.getXRot() + 90));

        CustomModels.render(BASE_RENDER_LAYER, Minecraft.getInstance().getItemRenderer(), modelId_base,
                matrixStack, vertexConsumers, light, livingEntity.getId());
        matrixStack.popPose();
    }
}
