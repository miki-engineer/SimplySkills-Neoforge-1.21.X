package net.sweenus.simplyskills.client.effects;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.sweenus.simplyskills.SimplySkills;

import java.util.List;

public class CurseRenderer extends OrbitingEffectRenderer {
    public static final ResourceLocation modelId_base = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/curse");
    public static final ResourceLocation modelId_overlay = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/curse");

    private static final RenderType BASE_RENDER_LAYER =
            RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType GLOWING_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.GLOW, false);

    @Override
    public void setSpeed(float newSpeed) {
        newSpeed = 6.0f;
        super.setSpeed(newSpeed);
    }

    public CurseRenderer() {
        super(List.of(
                        new Model(GLOWING_RENDER_LAYER, modelId_overlay)),
                0.9F,
                1.2F);
        setSpeed(6f);
    }

}
