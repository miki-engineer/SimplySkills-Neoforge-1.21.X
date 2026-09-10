package net.sweenus.simplyskills.network;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.SignatureAbilities;

public record KeybindPacket(String abilityType) implements CustomPacketPayload {

    public static final Type<KeybindPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "ability1"));
    public static final StreamCodec<io.netty.buffer.ByteBuf, KeybindPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            KeybindPacket::abilityType,
            KeybindPacket::new);

    public static void handle(KeybindPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> SignatureAbilities.signatureAbilityManager(context.player(), packet.abilityType()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
