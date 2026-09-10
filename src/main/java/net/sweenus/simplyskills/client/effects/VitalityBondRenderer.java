package net.sweenus.simplyskills.client.effects;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.sweenus.simplyskills.SimplySkills;

import java.util.List;

public class VitalityBondRenderer extends OrbitingEffectRenderer {
    public static final ResourceLocation modelId_base = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/vitality_bond");
    public static final ResourceLocation modelId_overlay = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_effect/vitality_bond");

    private static final RenderType BASE_RENDER_LAYER =
            RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType GLOWING_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.RADIATE, false);


    @Override
    public void setSpeed(float newSpeed) {
        newSpeed = 3f;
        super.setSpeed(newSpeed);
    }

    public VitalityBondRenderer() {
        super(List.of(
                        new Model(GLOWING_RENDER_LAYER, modelId_overlay),
                        new Model(BASE_RENDER_LAYER, modelId_base)),
                0.6F,
                1.10F);
    }

}
