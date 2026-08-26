package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.spell_engine.fx.SpellEngineParticles;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

import java.util.UUID;

public class FocusEffect extends MobEffect {
    public FocusEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
    private static boolean hasUsed = false;

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            if (livingEntity.getMainHandItem().getItem() instanceof BowItem && livingEntity.isUsingItem()) {
                livingEntity.setDeltaMovement(0, 0, 0);
                livingEntity.setSpeed(0);
                livingEntity.hurtMarked = true;
                MobEffectInstance focusEffectInstance = livingEntity.getEffect(EffectRegistry.FOCUS);
                if (focusEffectInstance != null && focusEffectInstance.getDuration() < 180)
                    hasUsed = true;
                if (livingEntity.tickCount % 10 == 0) {
                    HelperMethods.incrementStatusEffect(livingEntity, EffectRegistry.MARKSMANSHIP, 16, 1, 15);
                    MobEffectInstance marksmanshipInstance = livingEntity.getEffect(EffectRegistry.MARKSMANSHIP);
                    if (marksmanshipInstance != null)
                        HelperMethods.spawnParticlesInFrontOfPlayer((ServerLevel) livingEntity.level(),
                                livingEntity, SpellEngineParticles.magic_holy.type(),
                                8, Math.max(-1.0 ,-0.1 * (marksmanshipInstance.getAmplifier())),
                                1+marksmanshipInstance.getAmplifier());
                }
            }
            if (hasUsed && !livingEntity.isUsingItem()) {
                MobEffectInstance focusEffectInstance = livingEntity.getEffect(EffectRegistry.FOCUS);
                if (focusEffectInstance != null) {
                    livingEntity.level().playSound(null, livingEntity, SoundRegistry.SOUNDEFFECT31,
                            SoundSource.PLAYERS, 1.4f, 1.0f);
                    livingEntity.removeEffect(EffectRegistry.FOCUS);
                    livingEntity.removeEffect(EffectRegistry.MARKSMANSHIP);
                    hasUsed = false;
                }
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }
    public void onEffectAddedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (ModList.get().isLoaded("prominent") && entity instanceof Player player) {
            if (BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("zenith_attributes:draw_speed")) != null && BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("zenith_attributes:arrow_velocity")) != null) {
                AttributeInstance attributeInstance = entity.getAttribute(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("zenith_attributes:draw_speed")).orElseThrow());
                AttributeInstance attributeInstance2 = entity.getAttribute(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("zenith_attributes:arrow_velocity")).orElseThrow());
                if (attributeInstance != null && attributeInstance2 != null) {
                    AttributeModifier modifier = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("simplyskills", "focus_draw_speed"), -300, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    AttributeModifier modifier2 = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("simplyskills", "focus_arrow_velocity"), 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    attributeInstance.addTransientModifier(modifier);
                    attributeInstance2.addTransientModifier(modifier2);
                }
            }
        }
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (ModList.get().isLoaded("prominent") && entity instanceof Player player) {
            if (BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("zenith_attributes:draw_speed")) != null && BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("zenith_attributes:arrow_velocity")) != null) {
                AttributeInstance attributeInstance = entity.getAttribute(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("zenith_attributes:draw_speed")).orElseThrow());
                AttributeInstance attributeInstance2 = entity.getAttribute(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("zenith_attributes:arrow_velocity")).orElseThrow());
                if (attributeInstance != null && attributeInstance2 != null) {
                    AttributeModifier modifier = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("simplyskills", "focus_draw_speed"), -300, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    AttributeModifier modifier2 = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("simplyskills", "focus_arrow_velocity"), 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    attributeInstance.removeModifier(modifier);
                    attributeInstance2.removeModifier(modifier2);
                }
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
