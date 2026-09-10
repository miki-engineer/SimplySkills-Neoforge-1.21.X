package net.sweenus.simplyskills.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class StopSoundPacket {
    private final ResourceLocation soundId;

    public StopSoundPacket(ResourceLocation soundId) {
        this.soundId = soundId;
    }

    public ResourceLocation getSoundId() {
        return soundId;
    }

    public static void encode(StopSoundPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.getSoundId());
    }

    public static StopSoundPacket decode(FriendlyByteBuf buf) {
        ResourceLocation soundId = buf.readResourceLocation();
        return new StopSoundPacket(soundId);
    }
}