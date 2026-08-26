package net.sweenus.simplyskills.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.client.SimplySkillsClient;
import net.sweenus.simplyskills.client.gui.CustomHud;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.List;

public class ModPacketHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(KeybindPacket.TYPE, KeybindPacket.STREAM_CODEC, KeybindPacket::handle);
        registrar.playToClient(CooldownPacket.TYPE, CooldownPacket.STREAM_CODEC, CooldownPacket::handle);
        registrar.playToClient(UpdatePointsPayload.TYPE, UpdatePointsPayload.STREAM_CODEC, UpdatePointsPayload::handle);
        registrar.playToClient(StopSoundPayload.TYPE, StopSoundPayload.STREAM_CODEC, StopSoundPayload::handle);
        registrar.playToClient(SyncItemStackPayload.TYPE, SyncItemStackPayload.STREAM_CODEC, SyncItemStackPayload::handle);
        registrar.playToClient(SignaturePayload.TYPE, SignaturePayload.STREAM_CODEC, SignaturePayload::handle);
    }

    public static void sendTo(ServerPlayer player, UpdateUnspentPointsPacket packet) {
        PacketDistributor.sendToPlayer(player, new UpdatePointsPayload(packet.getUnspentPoints()));
    }

    public static void sendCooldown(ServerPlayer player, int cooldown, String cooldownType) {
        PacketDistributor.sendToPlayer(player, new CooldownPacket(cooldown, cooldownType));
    }

    public static void sendStopSoundPacket(ServerPlayer player, ResourceLocation soundId) {
        PacketDistributor.sendToPlayer(player, new StopSoundPayload(soundId));
    }

    public static void syncItemStackNbt(ServerPlayer player, int slot, ItemStack stack) {
        PacketDistributor.sendToPlayer(player, new SyncItemStackPayload(slot, stack));
    }

    public static void sendSignatureAbility(ServerPlayer player) {
        ResourceLocation identifier = ResourceLocation.parse("simplyskills:empty");
        ResourceLocation identifier2 = ResourceLocation.parse("simplyskills:empty");
        String stringSend = "empty";
        String stringSend2 = "empty";
        List<SignatureOption> signatures = List.of(
                new SignatureOption("simplyskills:rogue", SkillReferencePosition.rogueSpecialisationPreparation),
                new SignatureOption("simplyskills:rogue", SkillReferencePosition.rogueSpecialisationEvasion),
                new SignatureOption("simplyskills:rogue", SkillReferencePosition.rogueSpecialisationSiphoningStrikes),
                new SignatureOption("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationDisengage),
                new SignatureOption("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationArrowRain),
                new SignatureOption("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationElementalArrows),
                new SignatureOption("simplyskills:berserker", SkillReferencePosition.berserkerSpecialisationRampage),
                new SignatureOption("simplyskills:berserker", SkillReferencePosition.berserkerSpecialisationBerserking),
                new SignatureOption("simplyskills:berserker", SkillReferencePosition.berserkerSpecialisationBloodthirsty),
                new SignatureOption("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationConsecration),
                new SignatureOption("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationHeavensmithsCall),
                new SignatureOption("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationSacredOnslaught),
                new SignatureOption("simplyskills:cleric", SkillReferencePosition.clericSpecialisationAnointWeapon),
                new SignatureOption("simplyskills:cleric", SkillReferencePosition.clericSpecialisationSacredOrb),
                new SignatureOption("simplyskills:cleric", SkillReferencePosition.clericSpecialisationDivineIntervention),
                new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationIceComet),
                new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationMeteorShower),
                new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationArcaneBolt),
                new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationStaticDischarge),
                new SignatureOption("simplyskills:spellblade", SkillReferencePosition.spellbladeSpecialisationElementalSurge),
                new SignatureOption("simplyskills:spellblade", SkillReferencePosition.spellbladeSpecialisationElementalImpact),
                new SignatureOption("simplyskills:spellblade", SkillReferencePosition.spellbladeSpecialisationSpellweaver),
                new SignatureOption("simplyskills:necromancer", SkillReferencePosition.necromancerSpecialisationSummoningRitual));

        for (SignatureOption signature : signatures) {
            if (HelperMethods.isUnlocked(signature.category(), null, player)
                    && HelperMethods.isUnlocked(signature.category(), signature.skill(), player)) {
                identifier = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, signature.skill());
                stringSend = signature.category();
                break;
            }
        }

        List<String> ascendancies = List.of(
                SkillReferencePosition.ascendancyRighteousHammers, SkillReferencePosition.ascendancyBoneArmor,
                SkillReferencePosition.ascendancyCyclonicCleave, SkillReferencePosition.ascendancyMagicCircle,
                SkillReferencePosition.ascendancyArcaneSlash, SkillReferencePosition.ascendancyAgony,
                SkillReferencePosition.ascendancyTorment, SkillReferencePosition.ascendancyRapidfire,
                SkillReferencePosition.ascendancyCataclysm, SkillReferencePosition.ascendancyGhostwalk,
                SkillReferencePosition.ascendancySkywardSunder, SkillReferencePosition.ascendancyRighteousShield,
                SkillReferencePosition.ascendancyChainbreaker);
        String ascendancyTree = net.neoforged.fml.ModList.get().isLoaded("prominent") ? "puffish_skills:prom" : "simplyskills:ascendancy";
        for (String skill : ascendancies) {
            if (HelperMethods.isUnlocked(ascendancyTree, skill, player)) {
                identifier2 = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, skill);
                stringSend2 = skill;
                break;
            }
        }
        PacketDistributor.sendToPlayer(player, new SignaturePayload(identifier, identifier2, stringSend, stringSend2));
    }

    private record SignatureOption(String category, String skill) {
    }

    private static String signatureSprite(String className, String skill) {
        if (className.equals("simplyskills:rogue")) {
            if (skill.equals(SkillReferencePosition.rogueSpecialisationSiphoningStrikes)) return "siphoning_strikes";
            if (skill.equals(SkillReferencePosition.rogueSpecialisationEvasion)) return "evasion";
            if (skill.equals(SkillReferencePosition.rogueSpecialisationPreparation)) return "preparation";
        }
        if (className.equals("simplyskills:ranger")) {
            if (skill.equals(SkillReferencePosition.rangerSpecialisationElementalArrows)) return "elemental_arrows";
            if (skill.equals(SkillReferencePosition.rangerSpecialisationArrowRain)) return "arrow_rain";
            if (skill.equals(SkillReferencePosition.rangerSpecialisationDisengage)) return "disengage";
        }
        if (className.equals("simplyskills:berserker")) {
            if (skill.equals(SkillReferencePosition.berserkerSpecialisationBloodthirsty)) return "bloodthirsty";
            if (skill.equals(SkillReferencePosition.berserkerSpecialisationBerserking)) return "berserking";
            if (skill.equals(SkillReferencePosition.berserkerSpecialisationRampage)) return "rampage";
        }
        if (className.equals("simplyskills:crusader")) {
            if (skill.equals(SkillReferencePosition.crusaderSpecialisationConsecration)) return "consecration";
            if (skill.equals(SkillReferencePosition.crusaderSpecialisationHeavensmithsCall)) return "heavensmiths_call";
            if (skill.equals(SkillReferencePosition.crusaderSpecialisationSacredOnslaught)) return "sacred_onslaught";
        }
        if (className.equals("simplyskills:cleric")) {
            if (skill.equals(SkillReferencePosition.clericSpecialisationSacredOrb)) return "sacred_orb";
            if (skill.equals(SkillReferencePosition.clericSpecialisationAnointWeapon)) return "anoint_weapon";
            if (skill.equals(SkillReferencePosition.clericSpecialisationDivineIntervention)) return "divine_intervention";
        }
        if (className.equals("simplyskills:wizard")) {
            if (skill.equals(SkillReferencePosition.wizardSpecialisationArcaneBolt)) return "arcane_bolt";
            if (skill.equals(SkillReferencePosition.wizardSpecialisationIceComet)) return "ice_comet";
            if (skill.equals(SkillReferencePosition.wizardSpecialisationMeteorShower)) return "meteor_shower";
            if (skill.equals(SkillReferencePosition.wizardSpecialisationStaticDischarge)) return "lightning_beam";
        }
        if (className.equals("simplyskills:spellblade")) {
            if (skill.equals(SkillReferencePosition.spellbladeSpecialisationElementalImpact)) return "elemental_impact";
            if (skill.equals(SkillReferencePosition.spellbladeSpecialisationElementalSurge)) return "elemental_surge";
            if (skill.equals(SkillReferencePosition.spellbladeSpecialisationSpellweaver)) return "spellweaver";
        }
        if (className.equals("simplyskills:necromancer")
                && skill.equals(SkillReferencePosition.necromancerSpecialisationSummoningRitual)) return "summoning_ritual";
        return null;
    }

    private static String ascendancySprite(String skill) {
        if (skill.equals(SkillReferencePosition.ascendancyRighteousHammers)) return "righteous_hammers";
        if (skill.equals(SkillReferencePosition.ascendancyBoneArmor)) return "bone_armor";
        if (skill.equals(SkillReferencePosition.ascendancyCyclonicCleave)) return "cyclonic_cleave";
        if (skill.equals(SkillReferencePosition.ascendancyMagicCircle)) return "magic_circle";
        if (skill.equals(SkillReferencePosition.ascendancyArcaneSlash)) return "arcane_slash";
        if (skill.equals(SkillReferencePosition.ascendancyAgony)) return "agony";
        if (skill.equals(SkillReferencePosition.ascendancyTorment)) return "torment";
        if (skill.equals(SkillReferencePosition.ascendancyRapidfire)) return "rapidfire";
        if (skill.equals(SkillReferencePosition.ascendancyCataclysm)) return "cataclysm";
        if (skill.equals(SkillReferencePosition.ascendancyGhostwalk)) return "ghostwalk";
        if (skill.equals(SkillReferencePosition.ascendancySkywardSunder)) return "skyward_sunder";
        if (skill.equals(SkillReferencePosition.ascendancyRighteousShield)) return "righteous_shield";
        if (skill.equals(SkillReferencePosition.ascendancyChainbreaker)) return "chainbreaker";
        return null;
    }

    public record UpdatePointsPayload(int points) implements CustomPacketPayload {
        static final Type<UpdatePointsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "update_unspent_points"));
        static final StreamCodec<io.netty.buffer.ByteBuf, UpdatePointsPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, UpdatePointsPayload::points, UpdatePointsPayload::new);
        static void handle(UpdatePointsPayload packet, IPayloadContext context) { context.enqueueWork(() -> SimplySkillsClient.unspentPoints = packet.points()); }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record StopSoundPayload(ResourceLocation soundId) implements CustomPacketPayload {
        static final Type<StopSoundPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "stop_sound"));
        static final StreamCodec<io.netty.buffer.ByteBuf, StopSoundPayload> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, StopSoundPayload::soundId, StopSoundPayload::new);
        static void handle(StopSoundPayload packet, IPayloadContext context) { context.enqueueWork(() -> Minecraft.getInstance().getSoundManager().stop(packet.soundId(), SoundSource.PLAYERS)); }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record SyncItemStackPayload(int slot, ItemStack stack) implements CustomPacketPayload {
        static final Type<SyncItemStackPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "sync_item_stack"));
        static final StreamCodec<RegistryFriendlyByteBuf, SyncItemStackPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, SyncItemStackPayload::slot, ItemStack.OPTIONAL_STREAM_CODEC, SyncItemStackPayload::stack, SyncItemStackPayload::new);
        static void handle(SyncItemStackPayload packet, IPayloadContext context) { context.enqueueWork(() -> { if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.getInventory().setItem(packet.slot(), packet.stack()); }); }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record SignaturePayload(ResourceLocation skill, ResourceLocation ascendancy, String className, String ascendancyName) implements CustomPacketPayload {
        static final Type<SignaturePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "sync_signature_ability"));
        static final StreamCodec<io.netty.buffer.ByteBuf, SignaturePayload> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, SignaturePayload::skill, ResourceLocation.STREAM_CODEC, SignaturePayload::ascendancy,
                ByteBufCodecs.STRING_UTF8, SignaturePayload::className, ByteBufCodecs.STRING_UTF8, SignaturePayload::ascendancyName, SignaturePayload::new);
        static void handle(SignaturePayload packet, IPayloadContext context) { context.enqueueWork(() -> {
            String sprite = signatureSprite(packet.className(), packet.skill().getPath());
            String sprite2 = ascendancySprite(packet.ascendancy().getPath());
            CustomHud.setSprite(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, sprite == null ? "textures/gui/cooldown_overlay.png" : "textures/icons/alternate_reduced/" + packet.className().replace("simplyskills:", "") + "_signature_" + sprite + ".png"));
            CustomHud.setSprite2(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, sprite2 == null ? "textures/gui/cooldown_overlay.png" : "textures/icons/alternate_reduced/ascendancy_" + sprite2 + ".png"));
        }); }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
}
