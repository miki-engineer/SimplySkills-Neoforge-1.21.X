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
import net.sweenus.simplyskills.entities.SpellTargetEntity;

// Made with Blockbench 4.6.0
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class SpellTargetEntityModel extends EntityModel<SpellTargetEntity> {
	private final ModelPart supports;
	private final ModelPart bb_main;
	public SpellTargetEntityModel(ModelPart root) {
		this.supports = root.getChild("supports");
		this.bb_main = root.getChild("bb_main");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition supports = modelPartData.addOrReplaceChild("supports", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -30.0F, 0.0F, 1.0F, 21.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 17).addBox(0.0F, -9.0F, 0.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(14, 17).addBox(-4.0F, -28.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 2).addBox(-2.0F, -22.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 0).addBox(1.0F, -22.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 17).addBox(1.0F, -28.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -29.75F, -0.25F, 9.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}
	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int packedColor) {
		supports.render(matrices, vertexConsumer, light, overlay, packedColor);
		bb_main.render(matrices, vertexConsumer, light, overlay, packedColor);
	}

	@Override
	public void setupAnim(SpellTargetEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}