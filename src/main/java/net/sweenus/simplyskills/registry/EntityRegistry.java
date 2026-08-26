package net.sweenus.simplyskills.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.entities.DreadglareEntity;
import net.sweenus.simplyskills.entities.GreaterDreadglareEntity;
import net.sweenus.simplyskills.entities.SpellTargetEntity;
import net.sweenus.simplyskills.entities.WraithEntity;
import net.neoforged.neoforge.registries.RegisterEvent;

public class EntityRegistry {

    public static EntityType<SpellTargetEntity> SPELL_TARGET_ENTITY;
    public static EntityType<DreadglareEntity> DREADGLARE;
    public static EntityType<GreaterDreadglareEntity> GREATER_DREADGLARE;
    public static EntityType<WraithEntity> WRAITH;

    public static void registerEntities(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ENTITY_TYPE))
            return;

        SPELL_TARGET_ENTITY = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "custom_entity_1"),
                EntityType.Builder.of(SpellTargetEntity::new, MobCategory.MISC).sized(0.75f, 0.75f).build(SimplySkills.MOD_ID + ":custom_entity_1")
        );

        DREADGLARE = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "dreadglare"),
                EntityType.Builder.of(DreadglareEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build(SimplySkills.MOD_ID + ":dreadglare")
        );

        GREATER_DREADGLARE = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "greater_dreadglare"),
                EntityType.Builder.of(GreaterDreadglareEntity::new, MobCategory.CREATURE).sized(0.85f, 0.85f).build(SimplySkills.MOD_ID + ":greater_dreadglare")
        );

        WRAITH = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "wraith"),
                EntityType.Builder.of(WraithEntity::new, MobCategory.CREATURE).sized(1f, 1f).build(SimplySkills.MOD_ID + ":wraith")
        );
        SimplySkills.LOGGER.info("Registering Entities for " + SimplySkills.MOD_ID);
    }


}
