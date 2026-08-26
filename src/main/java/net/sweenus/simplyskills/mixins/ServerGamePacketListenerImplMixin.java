package net.sweenus.simplyskills.mixins;

import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1100)
public abstract class ServerGamePacketListenerImplMixin {

    @Inject(at = @At("HEAD"), method = "handleAnimate")
    private void simplyskills$handleAnimateOnServerThread(ServerboundSwingPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        PacketUtils.ensureRunningOnSameThread(packet, listener, listener.player.serverLevel());
    }

    @Inject(at = @At("HEAD"), method = "handlePlayerAction")
    private void simplyskills$handlePlayerActionOnServerThread(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        PacketUtils.ensureRunningOnSameThread(packet, listener, listener.player.serverLevel());
    }

    @Inject(at = @At("HEAD"), method = "handleInteract")
    private void simplyskills$handleInteractOnServerThread(ServerboundInteractPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        PacketUtils.ensureRunningOnSameThread(packet, listener, listener.player.serverLevel());
    }
}
