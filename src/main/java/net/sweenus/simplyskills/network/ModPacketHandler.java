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

    private record SignatureOption(String category, String skill, String sprite) {}
    private record AscendancyOption(String skill, String sprite) {}

    private static final List<SignatureOption> SIGNATURES = List.of(
            new SignatureOption("simplyskills:rogue", SkillReferencePosition.rogueSpecialisationPreparation, "preparation"),
            new SignatureOption("simplyskills:rogue", SkillReferencePosition.rogueSpecialisationEvasion, "evasion"),
            new SignatureOption("simplyskills:rogue", SkillReferencePosition.rogueSpecialisationSiphoningStrikes, "siphoning_strikes"),
            new SignatureOption("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationDisengage, "disengage"),
            new SignatureOption("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationArrowRain, "arrow_rain"),
            new SignatureOption("simplyskills:ranger", SkillReferencePosition.rangerSpecialisationElementalArrows, "elemental_arrows"),
            new SignatureOption("simplyskills:berserker", SkillReferencePosition.berserkerSpecialisationRampage, "rampage"),
            new SignatureOption("simplyskills:berserker", SkillReferencePosition.berserkerSpecialisationBerserking, "berserking"),
            new SignatureOption("simplyskills:berserker", SkillReferencePosition.berserkerSpecialisationBloodthirsty, "bloodthirsty"),
            new SignatureOption("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationConsecration, "consecration"),
            new SignatureOption("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationHeavensmithsCall, "heavensmiths_call"),
            new SignatureOption("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationSacredOnslaught, "sacred_onslaught"),
            new SignatureOption("simplyskills:cleric", SkillReferencePosition.clericSpecialisationAnointWeapon, "anoint_weapon"),
            new SignatureOption("simplyskills:cleric", SkillReferencePosition.clericSpecialisationSacredOrb, "sacred_orb"),
            new SignatureOption("simplyskills:cleric", SkillReferencePosition.clericSpecialisationDivineIntervention, "divine_intervention"),
            new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationIceComet, "ice_comet"),
            new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationMeteorShower, "meteor_shower"),
            new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationArcaneBolt, "arcane_bolt"),
            new SignatureOption("simplyskills:wizard", SkillReferencePosition.wizardSpecialisationStaticDischarge, "lightning_beam"),
            new SignatureOption("simplyskills:spellblade", SkillReferencePosition.spellbladeSpecialisationElementalSurge, "elemental_surge"),
            new SignatureOption("simplyskills:spellblade", SkillReferencePosition.spellbladeSpecialisationElementalImpact, "elemental_impact"),
            new SignatureOption("simplyskills:spellblade", SkillReferencePosition.spellbladeSpecialisationSpellweaver, "spellweaver"),
            new SignatureOption("simplyskills:necromancer", SkillReferencePosition.necromancerSpecialisationSummoningRitual, "summoning_ritual"));

    private static final List<AscendancyOption> ASCENDANCIES = List.of(
            new AscendancyOption(SkillReferencePosition.ascendancyRighteousHammers, "righteous_hammers"),
            new AscendancyOption(SkillReferencePosition.ascendancyBoneArmor, "bone_armor"),
            new AscendancyOption(SkillReferencePosition.ascendancyCyclonicCleave, "cyclonic_cleave"),
            new AscendancyOption(SkillReferencePosition.ascendancyMagicCircle, "magic_circle"),
            new AscendancyOption(SkillReferencePosition.ascendancyArcaneSlash, "arcane_slash"),
            new AscendancyOption(SkillReferencePosition.ascendancyAgony, "agony"),
            new AscendancyOption(SkillReferencePosition.ascendancyTorment, "torment"),
            new AscendancyOption(SkillReferencePosition.ascendancyRapidfire, "rapidfire"),
            new AscendancyOption(SkillReferencePosition.ascendancyCataclysm, "cataclysm"),
            new AscendancyOption(SkillReferencePosition.ascendancyGhostwalk, "ghostwalk"),
            new AscendancyOption(SkillReferencePosition.ascendancySkywardSunder, "skyward_sunder"),
            new AscendancyOption(SkillReferencePosition.ascendancyRighteousShield, "righteous_shield"),
            new AscendancyOption(SkillReferencePosition.ascendancyChainbreaker, "chainbreaker"));

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
        ResourceLocation signatureId = ResourceLocation.parse("simplyskills:empty");
        ResourceLocation ascendancyId = ResourceLocation.parse("simplyskills:empty");
        String signatureCategory = "empty";
        String ascendancySkill = "empty";

        for (SignatureOption signature : SIGNATURES) {
            if (HelperMethods.isUnlocked(signature.category(), null, player)
                    && HelperMethods.isUnlocked(signature.category(), signature.skill(), player)) {
                signatureId = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, signature.skill());
                signatureCategory = signature.category();
                break;
            }
        }

        String ascendancyTree = net.neoforged.fml.ModList.get().isLoaded("prominent") ? "puffish_skills:prom" : "simplyskills:ascendancy";
        for (AscendancyOption ascendancy : ASCENDANCIES) {
            String skill = ascendancy.skill();
            if (HelperMethods.isUnlocked(ascendancyTree, skill, player)) {
                ascendancyId = ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, skill);
                ascendancySkill = skill;
                break;
            }
        }
        PacketDistributor.sendToPlayer(player, new SignaturePayload(signatureId, ascendancyId, signatureCategory, ascendancySkill));
    }

    private static String signatureSprite(String category, String skill) {
        for (SignatureOption signature : SIGNATURES) {
            if (signature.category().equals(category) && signature.skill().equals(skill)) {
                return signature.sprite();
            }
        }
        return null;
    }

    private static String ascendancySprite(String skill) {
        for (AscendancyOption ascendancy : ASCENDANCIES) {
            if (ascendancy.skill().equals(skill)) {
                return ascendancy.sprite();
            }
        }
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
