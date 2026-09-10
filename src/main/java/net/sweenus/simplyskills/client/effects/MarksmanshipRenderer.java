package net.sweenus.simplyskills.client.effects;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.LightEmission;
import net.sweenus.simplyskills.SimplySkills;

import java.util.List;

public class MarksmanshipRenderer extends OrbitingEffectRenderer {
    public static final ResourceLocation modelId_base = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_projectile/arrow");
    public static final ResourceLocation modelId_overlay = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "spell_projectile/arrow");

    private static final RenderType BASE_RENDER_LAYER =
            RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
    private static final RenderType GLOWING_RENDER_LAYER =
            CustomLayers.spellEffect(LightEmission.NONE, false);

    @Override
    public void setSpeed(float newSpeed) {
        newSpeed = 2f;
        super.setSpeed(newSpeed);
    }

    public MarksmanshipRenderer() {
        super(List.of(
                new Model(BASE_RENDER_LAYER, modelId_base)),
                1.2F,
                1.1F);
    }

}
