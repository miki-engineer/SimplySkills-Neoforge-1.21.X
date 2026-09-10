package net.sweenus.simplyskills.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.puffish.attributesmod.AttributesMod;
import net.spell_engine.api.spell.ExternalSpellSchools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExternalSpellSchools.class)
public abstract class ExternalSpellSchoolsMixin {

    @Inject(method = "rangedDamageAttribute", at = @At("HEAD"), cancellable = true)
    private static void changeRangedDamageAttribute(CallbackInfoReturnable<Holder<Attribute>> cir) {
        boolean isModLoaded = net.neoforged.fml.ModList.get().isLoaded("ranged_weapon_api");
        if (isModLoaded) {
            Holder<Attribute> rangedDamage = BuiltInRegistries.ATTRIBUTE
                    .getHolder(ResourceLocation.parse("ranged_weapon:damage")).orElse(null);
            cir.setReturnValue(rangedDamage);
        } else {
            // Attribute used when Ranged Weapon API is not loaded
            cir.setReturnValue(AttributesMod.RANGED_DAMAGE);
        }
    }

}
