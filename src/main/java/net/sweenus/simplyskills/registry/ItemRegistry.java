package net.sweenus.simplyskills.registry;

import net.neoforged.fml.ModList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.items.GraciousManuscript;
import net.sweenus.simplyskills.items.MalevolentManuscript;
import net.sweenus.simplyskills.items.SkillChronicle;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ItemRegistry {

    private static final boolean isProminenceLoaded = ModList.get().isLoaded("prominent");

    public static Item MALEVOLENTMANUSCRIPT;
    public static Item GRACIOUSMANUSCRIPT;
    public static Item SKILLCHRONICLE;

    private static Item registerItem(String name, Item item) {
        if (item == null) return null; // Prevent registration if the item is null
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, name), item);
    }

    public static void registerItems(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM))
            return;

        MALEVOLENTMANUSCRIPT = isProminenceLoaded ? null : registerItem("malevolent_manuscript",
                new MalevolentManuscript(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .stacksTo(1)
                        .fireResistant()));
        GRACIOUSMANUSCRIPT = registerItem("gracious_manuscript",
                new GraciousManuscript(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .stacksTo(1)
                        .fireResistant()));
        SKILLCHRONICLE = registerItem("skill_chronicle",
                new SkillChronicle(new Item.Properties()
                        .rarity(Rarity.RARE)
                        .stacksTo(1)
                        .fireResistant()));
        SimplySkills.LOGGER.info("Registering Items for " + SimplySkills.MOD_ID);
    }
}
