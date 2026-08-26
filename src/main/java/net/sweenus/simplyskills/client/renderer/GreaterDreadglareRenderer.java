package net.sweenus.simplyskills.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sweenus.simplyskills.client.SimplySkillsClient;
import net.sweenus.simplyskills.client.renderer.model.GreaterDreadglareModel;
import net.sweenus.simplyskills.entities.GreaterDreadglareEntity;


@OnlyIn(Dist.CLIENT)
public class GreaterDreadglareRenderer extends MobRenderer<GreaterDreadglareEntity, GreaterDreadglareModel> {


     private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("simplyskills","textures/entity/dreadglare.png");

    public GreaterDreadglareRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GreaterDreadglareModel(ctx.bakeLayer(SimplySkillsClient.GREATER_DREADGLARE_MODEL)), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(GreaterDreadglareEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(GreaterDreadglareEntity entity, PoseStack matrixStack, float partialTickTime) {
        matrixStack.scale(1.5f, 1.5f, 1.5f);
    }

    @Override
    public void render(GreaterDreadglareEntity greaterDreadglareEntity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int packedLightIn) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.XP.rotationDegrees(greaterDreadglareEntity.getViewXRot(partialTicks)));
        matrixStack.mulPose(Axis.YP.rotationDegrees(greaterDreadglareEntity.getViewYRot(partialTicks)));

        super.render(greaterDreadglareEntity, entityYaw, partialTicks, matrixStack, vertexConsumerProvider, packedLightIn);

        matrixStack.popPose();
    }
}
