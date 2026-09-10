package net.sweenus.simplyskills.client.renderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sweenus.simplyskills.client.SimplySkillsClient;
import net.sweenus.simplyskills.client.renderer.model.WraithModel;
import net.sweenus.simplyskills.entities.WraithEntity;


@OnlyIn(Dist.CLIENT)
public class WraithRenderer extends MobRenderer<WraithEntity, WraithModel> {


     private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("simplyskills","textures/entity/wraith.png");

    public WraithRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WraithModel(ctx.bakeLayer(SimplySkillsClient.WRAITH_MODEL)), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(WraithEntity entity) {
        return TEXTURE;
    }

}
