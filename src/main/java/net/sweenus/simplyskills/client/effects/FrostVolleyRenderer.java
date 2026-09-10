package net.sweenus.simplyskills.client.effects;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.sweenus.simplyskills.SimplySkills;

import java.util.List;

public class FrostVolleyRenderer extends OrbitingEffectRenderer {
    public static final ResourceLocation modelId_base = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_projectile/ice_projectile");
    public static final ResourceLocation modelId_overlay = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_projectile/ice_projectile");

    private static final RenderType BASE_RENDER_LAYER =
            RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType OVERLAY_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.GLOW, true);


    @Override
    public void setSpeed(float newSpeed) {
        newSpeed = 7f;
        super.setSpeed(newSpeed);
    }

    public FrostVolleyRenderer() {
        super(List.of(
                new Model(OVERLAY_RENDER_LAYER, modelId_overlay)),
                0.4F,
                1.55F);
    }

}
