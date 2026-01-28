package top.Future.tps.server;

import net.minecraft.server.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.Future.tps.Tpsbot;

import java.util.*;
import java.util.stream.Collectors;

public class ServerManager {
    private MinecraftServer server;
    private final ServerInfo serverInfo;
    
    public ServerManager() {
        this.serverInfo = new ServerInfo();
    }
    
    // Setter for server (will be called when server starts)
    public void setServer(MinecraftServer server) {
        this.server = server;
        this.serverInfo.setServer(server);
    }
    
    // Getter for server
    public MinecraftServer getServer() {
        return server;
    }
    
    // Getter for server info
    public ServerInfo getServerInfo() {
        return serverInfo;
    }
    
    // Player management
    public List<String> getPlayerNames() {
        if (server == null) return Collections.emptyList();
        
        return server.getPlayerManager().getPlayerList().stream()
                .map(ServerPlayerEntity::getGameProfile)
                .map(Objects::requireNonNull)
                .map(profile -> profile.getName() != null ? profile.getName() : "Unknown")
                .collect(Collectors.toList());
    }
    
    public int getPlayerCount() {
        if (server == null) return 0;
        return server.getPlayerManager().getCurrentPlayerCount();
    }
    
    public int getMaxPlayers() {
        if (server == null) return 0;
        return server.getPlayerManager().getMaxPlayerCount();
    }
    
    public ServerPlayerEntity getPlayer(String name) {
        if (server == null) return null;
        return server.getPlayerManager().getPlayer(name);
    }
    
    public boolean kickPlayer(String name, String reason) {
        if (server == null) return false;
        
        ServerPlayerEntity player = getPlayer(name);
        if (player != null) {
            Text kickReason = reason != null ? Text.literal(reason) : Text.literal("You have been kicked from the server");
            player.networkHandler.disconnect(kickReason);
            return true;
        }
        return false;
    }
    
    public boolean banPlayer(String name, String reason) {
        if (server == null) {
            Tpsbot.LOGGER.error("Failed to ban player {}: Server is not initialized", name);
            return false;
        }
        try{
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(name);
            if (player != null) {
                playerManager.getUserBanList().add(new BannedPlayerEntry(player.getGameProfile()));
            }
            return true;
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to ban player {}: {}", name, e.getMessage());
        }
        return false;
    }
    
    public boolean unbanPlayer(String name) {
        if (server == null) return false;
        try{
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(name);
            if (player != null) {
                playerManager.getUserBanList().remove(new BannedPlayerEntry(player.getGameProfile()));
            }
            return true;
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to unban player {}: {}", name, e.getMessage());
        }
        return false;
    }
    
    public boolean banIp(String ip, String reason) {
        if (server == null) return false;
        try{
            PlayerManager playerManager = server.getPlayerManager();
            playerManager.getIpBanList().add(new BannedIpEntry(ip));
            return true;
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to ban IP {}: {}", ip, e.getMessage());
        }
        return false;
    }
    
    public boolean unbanIp(String ip) {
        if (server == null) return false;
        try{
            PlayerManager playerManager = server.getPlayerManager();
            playerManager.getIpBanList().remove(new BannedIpEntry(ip));
            return true;
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to unban IP {}: {}", ip, e.getMessage());
        }
        return false;
    }
    
    // OP management
    public boolean opPlayer(String name) {
        if (server == null) return false;
        
        PlayerManager playerManager = server.getPlayerManager();
        try {
            ServerPlayerEntity player = playerManager.getPlayer(name);
            if (player != null) {
                playerManager.addToOperators(player.getGameProfile());
                return true;
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to op player {}: {}", name, e.getMessage());
        }
        return false;
    }

    public boolean deopPlayer(String name) {
        if (server == null) return false;

        PlayerManager playerManager = server.getPlayerManager();
        try {
            ServerPlayerEntity player = playerManager.getPlayer(name);
            if (player != null) {
                playerManager.removeFromOperators(player.getGameProfile());
                return true;
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to deop player {}: {}", name, e.getMessage());
        }
        return false;
    }
    
    // Whitelist management
    public List<String> getWhitelist() {
        if (server == null) return Collections.emptyList();
        try{
            PlayerManager playerManager = server.getPlayerManager();
            return List.of(playerManager.getWhitelistedNames());
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to get whitelist {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public boolean addWhitelist(String name) {
        if (server == null) return false;
        try {
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(name);
            if (player != null) {
                playerManager.getWhitelist().add(new WhitelistEntry(player.getGameProfile()));
            }
            return true;
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to add player {} to whitelist: {}", name, e.getMessage());
            return false;
        }
    }
    
    public boolean removeWhitelist(String name) {
        if (server == null) return false;
        try {
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(name);
            if (player != null) {
                playerManager.getWhitelist().remove(new WhitelistEntry(player.getGameProfile()));
            }
            return true;
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to remove player {} to whitelist: {}", name, e.getMessage());
            return false;
        }
    }
    
    // Server control
    public void stopServer() {
        if (server == null) return;
        try{
            server.stop(true);
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to stop server {}", e.getMessage());
        }
    }
    
    public void rebootServer() {
        if (server == null) return;
        stopServer();
    }
}