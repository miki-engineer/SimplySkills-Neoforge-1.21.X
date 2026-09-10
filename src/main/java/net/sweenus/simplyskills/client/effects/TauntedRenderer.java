package net.sweenus.simplyskills.client.effects;

import com.mojang.blaze3d.vertex.PoseStack;
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


public class TauntedRenderer implements CustomModelStatusEffect.Renderer {
    public static final ResourceLocation modelId_overlay = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/taunted");

    private static final RenderType OVERLAY_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.GLOW, false);


    @Override
    public void renderEffect(long time, int amplifier, LivingEntity livingEntity, float delta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light) {
        float entitySize = livingEntity.getBbHeight();
        float yOffset = 0F +entitySize - 0.40F;

        float overlayScale = 1.2F;
        matrixStack.pushPose();
        matrixStack.translate(0, yOffset, 0);
        matrixStack.scale(overlayScale, overlayScale, overlayScale);

        CustomModels.render(OVERLAY_RENDER_LAYER, Minecraft.getInstance().getItemRenderer(), modelId_overlay,
                matrixStack, vertexConsumers, light, livingEntity.getId());
        matrixStack.popPose();
    }
}
