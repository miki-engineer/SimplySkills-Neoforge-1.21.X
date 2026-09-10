package net.sweenus.simplyskills.client.renderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.sweenus.simplyskills.entities.SpellTargetEntity;

@OnlyIn(Dist.CLIENT)
public class SpellTargetEntityRenderer extends EntityRenderer<SpellTargetEntity> {


     private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("simplyswords","textures/entity/battlestandard/battlestandard_texture.png");

    public SpellTargetEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Override
    public ResourceLocation getTextureLocation(SpellTargetEntity entity) {
        return TEXTURE;
    }
}
