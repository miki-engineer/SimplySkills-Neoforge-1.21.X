package net.sweenus.simplyskills.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.sweenus.simplyskills.client.gui.CustomHud;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.sweenus.simplyskills.SimplySkills;

public class ClientEvents {

    private static final CustomHud customHud = new CustomHud();

    public static void registerClientEvents(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "signature_abilities"),
                (context, deltaTracker) -> customHud.render(context, 0, 0, deltaTracker.getGameTimeDeltaPartialTick(false)));
    }
}
