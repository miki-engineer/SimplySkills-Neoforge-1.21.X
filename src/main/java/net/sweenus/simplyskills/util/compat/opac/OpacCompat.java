package net.sweenus.simplyskills.util.compat.opac;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.util.List;
import java.util.UUID;

public class OpacCompat {

    public static boolean checkOpacFriendlyFire(LivingEntity livingEntity, Player playerEntity) {
        if (!playerEntity.level().isClientSide() && livingEntity instanceof Player) {

            MinecraftServer server = playerEntity.getServer();
            if (server == null) {
                return true;
            }

            UUID playerUUID = playerEntity.getUUID();
            UUID targetUUID = livingEntity.getUUID();
            UUID playerParty = null;
            UUID targetParty = null;
            List<ServerPlayer> memberList = null;
            try {
                memberList = OpenPACServerAPI.get(server).getPartyManager().getPartyByMember(playerUUID).getOnlineMemberStream().toList();
            } catch (Exception e) {
                //System.out.println("Failed to fetch party members");
            }

            // Check if parties are allied
            if (OpenPACServerAPI.get(server).getPartyManager().getPartyByMember(targetUUID) != null && OpenPACServerAPI.get(server).getPartyManager().getPartyByMember(playerUUID) != null) {
                try {
                    targetParty = OpenPACServerAPI.get(server).getPartyManager().getPartyByMember(targetUUID).getId();
                    playerParty = OpenPACServerAPI.get(server).getPartyManager().getPartyByMember(playerUUID).getId();
                }
                catch (Exception e){
                    //System.out.println("Failed to get party ID from target: " + targetUUID);
                }
                if (targetParty !=null && playerParty != null && !targetParty.equals(playerParty)) {
                    return !OpenPACServerAPI.get(server).getPartyManager().getPartyByMember(playerUUID).isAlly(targetParty);
                }
            }

            // Check if in the same party
            if (memberList != null && !memberList.isEmpty() && livingEntity instanceof ServerPlayer serverTarget) {
                //System.out.println("Target is member of same party: " + memberList.contains(serverTarget));
                return !memberList.contains(serverTarget);
            }


            return true;
        }
        return true;
    }
}