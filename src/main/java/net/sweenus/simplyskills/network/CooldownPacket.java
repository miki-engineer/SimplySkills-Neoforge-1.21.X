package net.sweenus.simplyskills.network;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.client.SimplySkillsClient;

public record CooldownPacket(int cooldown, String cooldownType) implements CustomPacketPayload {

    public static final Type<CooldownPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "cooldown"));
    public static final StreamCodec<io.netty.buffer.ByteBuf, CooldownPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CooldownPacket::cooldown,
            ByteBufCodecs.STRING_UTF8,
            CooldownPacket::cooldownType,
            CooldownPacket::new);

    public static void handle(CooldownPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.cooldownType().contains("signature"))
                SimplySkillsClient.abilityCooldown = packet.cooldown();
            else if (packet.cooldownType().contains("ascendancy"))
                SimplySkillsClient.abilityCooldown2 = packet.cooldown();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
