package net.sweenus.simplyskills.network;

import net.minecraft.network.FriendlyByteBuf;

public class UpdateUnspentPointsPacket {
    private final int unspentPoints;

    public UpdateUnspentPointsPacket(int unspentPoints) {
        this.unspentPoints = unspentPoints;
    }

    public int getUnspentPoints() {
        return unspentPoints;
    }

    public static void encode(UpdateUnspentPointsPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.unspentPoints);
    }

    public static UpdateUnspentPointsPacket decode(FriendlyByteBuf buf) {
        return new UpdateUnspentPointsPacket(buf.readInt());
    }

}
