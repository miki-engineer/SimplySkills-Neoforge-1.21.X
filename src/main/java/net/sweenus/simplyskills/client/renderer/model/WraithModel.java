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
import net.sweenus.simplyskills.entities.WraithEntity;

// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class WraithModel extends EntityModel<WraithEntity> {
	private final ModelPart all;
	private final ModelPart hands;
	private final ModelPart spine;
	public WraithModel(ModelPart root) {
		this.all = root.getChild("all");
		this.hands = root.getChild("hands");
		this.spine = root.getChild("spine");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition weapon = modelPartData.addOrReplaceChild("weapon", CubeListBuilder.create().texOffs(46, 25).addBox(4.0F, -18.0F, -10.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 37).addBox(5.0F, -15.0F, -9.0F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition all = modelPartData.addOrReplaceChild("all", CubeListBuilder.create().texOffs(19, 21).addBox(-3.0F, -16.0F, -4.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(6, 44).addBox(-1.0F, -10.0F, 1.0F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 13).addBox(-3.0F, -10.0F, 1.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(44, 44).addBox(1.0F, -10.0F, 1.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(47, 13).addBox(-4.0F, -10.0F, 1.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 44).addBox(3.0F, -10.0F, 1.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(38, 38).addBox(3.0F, -10.0F, -4.0F, 1.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 13).addBox(4.0F, -10.0F, -8.0F, 3.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 29).addBox(6.0F, -6.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(34, 14).addBox(6.0F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 32).addBox(4.0F, -6.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 26).addBox(5.0F, -6.0F, -7.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-7.0F, -10.0F, -8.0F, 3.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(15, 13).addBox(-6.0F, -6.0F, -7.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 16).addBox(-6.0F, -6.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 18).addBox(-6.0F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(19, 7).addBox(-5.0F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 14).addBox(3.0F, -10.0F, -2.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(16, 44).addBox(3.0F, -10.0F, 0.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(47, 23).addBox(-4.0F, -10.0F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-4.0F, -10.0F, -2.0F, 1.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(32, 40).addBox(-4.0F, -10.0F, -4.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(44, 34).addBox(-4.0F, -10.0F, -5.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 24).addBox(-3.0F, -10.0F, -5.0F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-1.0F, -10.0F, -5.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(26, 40).addBox(1.0F, -10.0F, -5.0F, 2.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(48, 0).addBox(3.0F, -10.0F, -5.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(22, 32).addBox(-4.0F, -17.0F, 1.0F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(34, 7).addBox(-3.0F, -18.0F, 2.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 26).addBox(-2.0F, -18.0F, 3.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 7).addBox(-2.0F, -17.0F, 4.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 0).addBox(-1.0F, -16.0F, 5.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 32).addBox(-4.0F, -16.0F, -5.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 26).addBox(3.0F, -16.0F, -5.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(15, 0).addBox(-4.0F, -17.0F, -5.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(15, 13).addBox(-3.0F, -18.0F, -5.0F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition hands = modelPartData.addOrReplaceChild("hands", CubeListBuilder.create().texOffs(18, 15).addBox(-7.0F, -9.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-7.0F, -9.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-5.0F, -9.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-7.0F, -9.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-5.0F, -9.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-7.0F, -8.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-5.0F, -8.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-5.0F, -10.0F, -11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-7.0F, -10.0F, -11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-6.0F, -9.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-6.0F, -8.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(-5.0F, -9.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(4.0F, -9.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(4.0F, -9.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(6.0F, -9.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(4.0F, -9.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(6.0F, -9.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(4.0F, -8.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(6.0F, -8.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(6.0F, -10.0F, -11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(4.0F, -10.0F, -11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(5.0F, -9.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(5.0F, -8.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 15).addBox(6.0F, -9.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition spine = modelPartData.addOrReplaceChild("spine", CubeListBuilder.create().texOffs(22, 40).addBox(-0.5F, -10.0F, -1.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(-1.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(-1.5F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(0.5F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(0.5F, -5.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(-1.5F, -6.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(-1.5F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 7).addBox(0.5F, -7.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}
	@Override
	public void setupAnim(WraithEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int packedColor) {
		all.render(matrices, vertexConsumer, light, overlay, packedColor);
		hands.render(matrices, vertexConsumer, light, overlay, packedColor);
		spine.render(matrices, vertexConsumer, light, overlay, packedColor);
	}
}