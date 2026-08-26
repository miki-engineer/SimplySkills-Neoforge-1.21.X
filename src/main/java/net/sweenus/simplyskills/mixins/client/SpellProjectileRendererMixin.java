package net.sweenus.simplyskills.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.render.SpellProjectileRenderer;
import net.spell_engine.entity.SpellProjectile;
import net.sweenus.simplyskills.util.HelperMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpellProjectileRenderer.class)
public class SpellProjectileRendererMixin <T extends Entity & ItemSupplier> extends EntityRenderer<T> {

    protected SpellProjectileRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void simplyskills$render(T entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof SpellProjectile projectile) {
            if (projectile.renderModels() != null) {
                String[] modelList = new String[]{"swordfall", "sword"};
                for (Spell.ProjectileModelComposite.Model model : projectile.renderModels().models) {
                    String modelId = model.fx.model_id;
                    if (modelId != null && HelperMethods.stringContainsAny(modelId, modelList))
                        model.rotate_degrees_per_tick = 0;
                }
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
