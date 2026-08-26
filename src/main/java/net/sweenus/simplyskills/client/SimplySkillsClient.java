package net.sweenus.simplyskills.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.CustomModels;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.client.effects.*;
import net.sweenus.simplyskills.client.events.ClientEvents;
import net.sweenus.simplyskills.client.renderer.DreadglareRenderer;
import net.sweenus.simplyskills.client.renderer.GreaterDreadglareRenderer;
import net.sweenus.simplyskills.client.renderer.SpellTargetEntityRenderer;
import net.sweenus.simplyskills.client.renderer.WraithRenderer;
import net.sweenus.simplyskills.client.renderer.model.DreadglareModel;
import net.sweenus.simplyskills.client.renderer.model.GreaterDreadglareModel;
import net.sweenus.simplyskills.client.renderer.model.WraithModel;
import net.sweenus.simplyskills.network.CooldownPacket;
import net.sweenus.simplyskills.network.ModPacketHandler;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.EntityRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;

public class SimplySkillsClient {

    public static int abilityCooldown = 500;
    public static int abilityCooldown2 = 500;
    public static long lastUseTime;
    public static long lastUseTime2;
    public static int unspentPoints = 0;
    public static KeyMapping bindingAbility1 = new KeyMapping("key.simplyskills.ability1", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.simplyskills");
    public static KeyMapping bindingAbility2 = new KeyMapping("key.simplyskills.ability2", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.category.simplyskills");

    public static ModelLayerLocation SPELLTARGETENTITY_MODEL = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("spell_target_entity", "cube"), "main");
    public static ModelLayerLocation DREADGLARE_MODEL = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("dreadglare", "cube"), "main");
    public static ModelLayerLocation GREATER_DREADGLARE_MODEL = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("greater_dreadglare", "cube"), "main");
    public static ModelLayerLocation WRAITH_MODEL = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("wraith", "cube"), "main");

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(SimplySkillsClient::registerKeyMappings);
        modEventBus.addListener(SimplySkillsClient::registerEntityRenderers);
        modEventBus.addListener(SimplySkillsClient::registerLayerDefinitions);
        modEventBus.addListener(ClientEvents::registerClientEvents);
        modEventBus.addListener(SimplySkillsClient::clientSetup);
        NeoForge.EVENT_BUS.addListener(SimplySkillsClient::clientTick);

    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

        CustomModels.registerModelIds(List.of(
                BladestormRenderer.modelId_base,
                BladestormRenderer.modelId_overlay,
                ArcaneVolleyRenderer.modelId_base,
                ArcaneVolleyRenderer.modelId_overlay,
                FrostVolleyRenderer.modelId_base,
                FrostVolleyRenderer.modelId_overlay,
                VitalityBondRenderer.modelId_base,
                VitalityBondRenderer.modelId_overlay,
                UndyingRenderer.modelId_base,
                UndyingRenderer.modelId_overlay,
                BarrierRenderer.modelId_base,
                ImmobilizeRenderer.modelId_base,
                DeathMarkRenderer.modelId_overlay,
                TauntedRenderer.modelId_overlay,
                RighteousHammersRenderer.modelId_overlay,
                BoneArmorRenderer.modelId_overlay,
                MagicCircleRenderer.modelId_overlay,
                CurseRenderer.modelId_overlay
        ));


        CustomModelStatusEffect.register(EffectRegistry.BLADESTORM.value(), new BladestormRenderer());
        CustomModelStatusEffect.register(EffectRegistry.ARCANEVOLLEY.value(), new ArcaneVolleyRenderer());
        CustomModelStatusEffect.register(EffectRegistry.FROSTVOLLEY.value(), new FrostVolleyRenderer());
        CustomModelStatusEffect.register(EffectRegistry.VITALITYBOND.value(), new VitalityBondRenderer());
        CustomModelStatusEffect.register(EffectRegistry.UNDYING.value(), new UndyingRenderer());
        CustomParticleStatusEffect.register(EffectRegistry.UNDYING.value(), new UndyingParticles(2));
        CustomModelStatusEffect.register(EffectRegistry.BARRIER.value(), new BarrierRenderer());
        CustomParticleStatusEffect.register(EffectRegistry.BARRIER.value(), new BarrierParticles(1));
        CustomParticleStatusEffect.register(EffectRegistry.RAGE.value(), new RageParticles(1));
        CustomParticleStatusEffect.register(EffectRegistry.EVASION.value(), new EvasionParticles(1));
        CustomModelStatusEffect.register(EffectRegistry.IMMOBILIZE.value(), new ImmobilizeRenderer());
        CustomModelStatusEffect.register(EffectRegistry.DEATHMARK.value(), new DeathMarkRenderer());
        CustomModelStatusEffect.register(EffectRegistry.TAUNTED.value(), new TauntedRenderer());
        CustomModelStatusEffect.register(EffectRegistry.MARKSMANSHIP.value(), new MarksmanshipRenderer());
        CustomModelStatusEffect.register(EffectRegistry.RIGHTEOUSHAMMERS.value(), new RighteousHammersRenderer());
        CustomModelStatusEffect.register(EffectRegistry.BONEARMOR.value(), new BoneArmorRenderer());
        CustomModelStatusEffect.register(EffectRegistry.MAGICCIRCLE.value(), new MagicCircleRenderer());
        CustomModelStatusEffect.register(EffectRegistry.AGONY.value(), new CurseRenderer());
        CustomModelStatusEffect.register(EffectRegistry.TORMENT.value(), new CurseRenderer());
        });
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(bindingAbility1);
        event.register(bindingAbility2);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        var client = net.minecraft.client.Minecraft.getInstance();
        if (client.player == null)
            return;

            while (bindingAbility1.consumeClick()) {
                if (System.currentTimeMillis() > (lastUseTime + abilityCooldown)) {

                    SignatureAbilities.sendKeybindPacket("signature");

                    lastUseTime = System.currentTimeMillis();
                    client.player.level().playSound(client.player, client.player.blockPosition(), SoundRegistry.SOUNDEFFECT7, SoundSource.PLAYERS, 0.4f, 1.5f);

                } else {
                    client.player.displayClientMessage(Component.literal("Ability can be used again in " + (((lastUseTime + abilityCooldown) - System.currentTimeMillis()) / 1000) + "s"), true);
                    client.player.level().playSound(client.player, client.player.blockPosition(), SoundRegistry.GONG_WARBLY, SoundSource.PLAYERS, 0.1f, 1.5f);
                }
            }

            while (bindingAbility2.consumeClick()) {
                if (System.currentTimeMillis() > (lastUseTime2 + abilityCooldown2)) {

                    SignatureAbilities.sendKeybindPacket("ascendancy");

                    lastUseTime2 = System.currentTimeMillis();
                    client.player.level().playSound(client.player, client.player.blockPosition(), SoundRegistry.SOUNDEFFECT7, SoundSource.PLAYERS, 0.4f, 1.5f);

                } else {
                    client.player.displayClientMessage(Component.literal("Ability can be used again in " + (((lastUseTime2 + abilityCooldown2) - System.currentTimeMillis()) / 1000) + "s"), true);
                    client.player.level().playSound(client.player, client.player.blockPosition(), SoundRegistry.GONG_WARBLY, SoundSource.PLAYERS, 0.1f, 1.5f);
                }
            }

            /* Toggle abilities disabled for now (To be implemented)

            if (bindingAbility3.isPressed()) {
                client.player.sendMessage(Text.literal("Toggle Ability is active"), false);
            }
            */

    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.SPELL_TARGET_ENTITY, SpellTargetEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DREADGLARE, DreadglareRenderer::new);
        event.registerEntityRenderer(EntityRegistry.WRAITH, WraithRenderer::new);
        event.registerEntityRenderer(EntityRegistry.GREATER_DREADGLARE, GreaterDreadglareRenderer::new);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DREADGLARE_MODEL, DreadglareModel::getTexturedModelData);
        event.registerLayerDefinition(WRAITH_MODEL, WraithModel::getTexturedModelData);
        event.registerLayerDefinition(GREATER_DREADGLARE_MODEL, GreaterDreadglareModel::getTexturedModelData);
    }

}
