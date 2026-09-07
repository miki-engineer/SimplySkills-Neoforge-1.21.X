package net.sweenus.simplyskills.client.effects;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.Synchronized;
import net.sweenus.simplyskills.registry.EffectRegistry;

public final class FirstPersonHammersRenderer {
    private FirstPersonHammersRenderer() {
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        var client = Minecraft.getInstance();
        var player = client.player;
        var camera = event.getCamera();
        if (player == null || player.isSpectator() || camera.getEntity() != player || camera.isDetached())
            return;

        for (var effect : Synchronized.effectsOf(player)) {
            if (effect.effect() != EffectRegistry.RIGHTEOUSHAMMERS.value())
                continue;

            var entry = CustomModelStatusEffect.entryOf(effect.effect());
            if (entry == null)
                return;

            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            var position = player.getPosition(partialTick);
            var cameraPosition = camera.getPosition();
            var poseStack = event.getPoseStack();
            var buffers = client.renderBuffers().bufferSource();
            int light = client.getEntityRenderDispatcher().getPackedLightCoords(player, partialTick);

            // The world stage already has the camera rotation; translate from camera to player feet.
            poseStack.pushPose();
            try {
                poseStack.translate(position.x - cameraPosition.x, position.y - cameraPosition.y,
                        position.z - cameraPosition.z);
                if (entry.args().scaleWithEntity()) {
                    float scale = player.getScale();
                    poseStack.scale(scale, scale, scale);
                }
                entry.renderer().renderEffect(effect.appliedAtWorldTime(), effect.amplifier(), player,
                        partialTick, poseStack, buffers, light);
                buffers.endLastBatch();
            } finally {
                poseStack.popPose();
            }
            return;
        }
    }
}
