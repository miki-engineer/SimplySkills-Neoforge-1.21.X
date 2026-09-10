package net.sweenus.simplyskills.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;

public class OrbitingRotatingEffectRenderer implements CustomModelStatusEffect.Renderer {
    public float speed = 16.25F;
    private final List<Model> models;
    private final float scale;
    private final float horizontalOffset;

    public void setSpeed (float newSpeed) {
        speed = newSpeed;
    }

    public OrbitingRotatingEffectRenderer(List<Model> models, float scale, float horizontalOffset) {
        this.models = models;
        this.scale = scale;
        this.horizontalOffset = horizontalOffset;
    }

    public void renderEffect(long time, int amplifier, LivingEntity livingEntity, float delta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light) {
        matrixStack.pushPose();
        float effectTime = livingEntity.level().getGameTime() + delta;
        float initialAngle = effectTime * speed - 45.0F;
        float horizontalOffset = this.horizontalOffset * livingEntity.getScale();
        float verticalOffset = livingEntity.getBbHeight() / 2.0F;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int stacks = amplifier + 1;
        float turnAngle = 360.0F / stacks;

        for(int i = 0; i < stacks; ++i) {
            float angle = initialAngle + turnAngle * i;
            renderModel(matrixStack, this.scale, verticalOffset, horizontalOffset, angle, itemRenderer, vertexConsumers, light, livingEntity, delta);
        }

        matrixStack.popPose();
    }

    private void renderModel(PoseStack matrixStack, float scale, float verticalOffset, float horizontalOffset, float rotation, ItemRenderer itemRenderer, MultiBufferSource vertexConsumers, int light, LivingEntity livingEntity, float delta) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
        matrixStack.translate(0.0F, verticalOffset, -horizontalOffset);
        matrixStack.scale(scale, scale, scale);

        float selfRotationSpeed = 10.0F; // Adjust this speed as necessary
        float selfRotationAngle = (livingEntity.tickCount + delta) * selfRotationSpeed;
        matrixStack.mulPose(Axis.YP.rotationDegrees(selfRotationAngle));

        for(Model model : models) {
            matrixStack.pushPose();
            CustomModels.render(model.layer(), itemRenderer, model.modelId(), matrixStack, vertexConsumers, light, livingEntity.getId());
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    public static record Model(RenderType layer, ResourceLocation modelId) {
        public Model(RenderType layer, ResourceLocation modelId) {
            this.layer = layer;
            this.modelId = modelId;
        }

        public RenderType layer() {
            return this.layer;
        }

        public ResourceLocation modelId() {
            return this.modelId;
        }
    }
}
