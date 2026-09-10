package net.sweenus.simplyskills.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.client.SimplySkillsClient;

public class CustomHud {

    public static ResourceLocation ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "textures/gui/cooldown_overlay.png");
    public static ResourceLocation ICON_TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "textures/gui/cooldown_overlay.png");
    public static ResourceLocation FRAME_TEXTURE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    public static ResourceLocation COOLDOWN_OVERLAY = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "textures/gui/cooldown_overlay.png");

    public static void setSprite(ResourceLocation sprite) {
        ICON_TEXTURE = sprite;
    }

    public static void setSprite2(ResourceLocation sprite2) {
        ICON_TEXTURE_2 = sprite2;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null || client.player.isSpectator() || client.isPaused() || client.screen != null) {
            return;
        }

        int scaledWidth = client.getWindow().getGuiScaledWidth();
        int scaledHeight = client.getWindow().getGuiScaledHeight();
        int guiAnchorX = ((scaledWidth / 2) + 86) + SimplySkills.generalConfig.signatureHudX;
        int guiAnchorY = (scaledHeight - 29) + SimplySkills.generalConfig.signatureHudY;

        // Render the first ability
        renderAbility(context, client, guiAnchorX, guiAnchorY, SimplySkillsClient.abilityCooldown, SimplySkillsClient.lastUseTime, ICON_TEXTURE, SimplySkillsClient.bindingAbility1);
        // Render the second ability, positioned 22 pixels to the right of the first
        renderAbility(context, client, guiAnchorX + 22, guiAnchorY, SimplySkillsClient.abilityCooldown2, SimplySkillsClient.lastUseTime2, ICON_TEXTURE_2, SimplySkillsClient.bindingAbility2);
    }

    private void renderAbility(GuiGraphics context, Minecraft client, int guiAnchorX, int guiAnchorY, int cooldown, long lastUseTime, ResourceLocation iconTexture, KeyMapping keybind) {
        long currentTime = SimplySkillsClient.getCooldownTime();
        long remainingCooldownMillis = Math.max(0, (lastUseTime + cooldown) - currentTime);
        int remainingCooldownSecs = (int) (remainingCooldownMillis / 1000);
        Component remainingCooldownText = Component.nullToEmpty(String.valueOf(remainingCooldownSecs));

        // Set blend & shader to prevent cooldown render alpha issues
        RenderSystem._setShaderTexture(0,0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Calculate the height of the cooldown overlay based on the remaining cooldown time
        int cooldownOverlayHeight = (int) (16 * (remainingCooldownMillis / (float) cooldown));

        Component keybindText = keybind.getTranslatedKeyMessage();

        if (client.player != null && !iconTexture.toString().contains("cooldown_overlay")) {
            // Draw the frame behind the ability icon
            context.blitSprite(FRAME_TEXTURE, guiAnchorX + 6, guiAnchorY + 6, 24, 24);
            // Draw the ability icon
            context.blit(iconTexture, guiAnchorX + 10, guiAnchorY + 10, 0, 0, 16, 16, 16, 16);
            // Draw the cooldown overlay if there is a remaining cooldown
            if (remainingCooldownMillis > 0) {
                int overlayY = guiAnchorY + 10 + (16 - cooldownOverlayHeight);
                context.blit(COOLDOWN_OVERLAY, guiAnchorX + 10, overlayY, 0, 16 - cooldownOverlayHeight, 16, cooldownOverlayHeight, 16, 16);
                context.drawCenteredString(client.font, remainingCooldownText, guiAnchorX + 18, guiAnchorY + 14, 16777215);
            }

            // Draw the keybind text
            context.drawCenteredString(client.font, keybindText, guiAnchorX + 18, guiAnchorY + 0, 16777215);
        }
    }
}
