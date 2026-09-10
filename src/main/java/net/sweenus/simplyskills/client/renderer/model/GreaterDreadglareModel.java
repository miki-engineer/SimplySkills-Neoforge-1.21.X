package net.sweenus.simplyskills.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.sweenus.simplyskills.entities.GreaterDreadglareEntity;

public class GreaterDreadglareModel extends EntityModel<GreaterDreadglareEntity> {
	private final ModelPart bb_main;
	public GreaterDreadglareModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -8.0F, -4.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(10, 11).addBox(-2.0F, -7.0F, 1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(1.0F, -7.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 16).addBox(-1.0F, -7.0F, 2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 2).addBox(-1.0F, -6.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 0).addBox(0.0F, -4.0F, 2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(1.0F, -5.0F, 2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(13, 16).addBox(-1.0F, -5.0F, 2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-2.0F, -4.0F, 2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -7.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 16).addBox(-2.0F, -6.0F, 2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int packedColor) {
		bb_main.render(matrices, vertexConsumer, light, overlay, packedColor);
	}

	@Override
	public void setupAnim(GreaterDreadglareEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}