package net.sweenus.simplyskills.registry;

import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.render.CustomModels;

import java.util.List;

import static net.sweenus.simplyskills.SimplySkills.MOD_ID;

public class ModelRegistry {

    public static void registerModels() {
        // For projectiles '(N) North' in blockbench is 'down' in-game with default rotations
        // For projectiles 'Up' in blockbench is 'forward' in-game with default rotations
        CustomModels.registerModelIds(List.of(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/swordfall"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/sword"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/ice_projectile"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/fire_projectile"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/lightning_projectile"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/arcane_projectile"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/meteor_projectile"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/comet_projectile"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/arcane_slash"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/arrow"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/righteous_shield"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/righteous_hammers"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell_projectile/eldritch_hammers")
        ));
    }

}
