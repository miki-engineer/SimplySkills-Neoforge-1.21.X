package net.sweenus.simplyskills.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.sweenus.simplyskills.config.ConfigWrapper;

public class ModMenuIntegration {

    public static Screen createConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(ConfigWrapper.class, parent).get();
    }
}
