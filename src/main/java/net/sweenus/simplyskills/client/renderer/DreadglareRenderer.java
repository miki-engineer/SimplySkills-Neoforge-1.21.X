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
import net.sweenus.simplyskills.client.renderer.model.DreadglareModel;
import net.sweenus.simplyskills.entities.DreadglareEntity;


@OnlyIn(Dist.CLIENT)
public class DreadglareRenderer extends MobRenderer<DreadglareEntity, DreadglareModel> {


     private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("simplyskills","textures/entity/dreadglare.png");

    public DreadglareRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DreadglareModel(ctx.bakeLayer(SimplySkillsClient.DREADGLARE_MODEL)), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(DreadglareEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(DreadglareEntity dreadglareEntity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int packedLightIn) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.XP.rotationDegrees(dreadglareEntity.getViewXRot(partialTicks)));
        matrixStack.mulPose(Axis.YP.rotationDegrees(dreadglareEntity.getViewYRot(partialTicks)));

        super.render(dreadglareEntity, entityYaw, partialTicks, matrixStack, vertexConsumerProvider, packedLightIn);

        matrixStack.popPose();
    }
}
