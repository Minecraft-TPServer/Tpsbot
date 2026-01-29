package top.Future.tps.server;

import com.mojang.authlib.GameProfile;
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
        try {
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(name);
            GameProfile profile = null;
            OperatorList operatorList = playerManager.getOpList();
            if (player != null) {
                // 玩家在线
                profile = player.getGameProfile();
                ServerConfigEntry<GameProfile> existingEntry = operatorList.get(profile);
                if (existingEntry != null) {
                    Tpsbot.LOGGER.info("玩家 {} 已经是OP", name);
                    return true;
                }
                playerManager.addToOperators(player.getGameProfile());
                return true;
            } else {
                // 玩家离线，从缓存查找
                Optional<GameProfile> profileOpt = Objects.requireNonNull(server.getUserCache()).findByName(name);
                if (profileOpt.isPresent()) profile = profileOpt.get();
                ServerConfigEntry<GameProfile> existingEntry = operatorList.get(profile);
                if (existingEntry != null) {
                    Tpsbot.LOGGER.info("玩家 {} 已经是OP", name);
                    return true;
                }
                operatorList.add(new OperatorEntry(profile,4,false));
                return true;
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to op player {}: {}", name, e.getMessage());
        }
        return false;
    }

    public boolean deopPlayer(String name) {
        if (server == null) return false;
        try {
            PlayerManager playerManager = server.getPlayerManager();
            ServerPlayerEntity player = playerManager.getPlayer(name);
            GameProfile profile = null;
            OperatorList operatorList = playerManager.getOpList();
            if (player != null) {
                // 玩家在线
                profile = player.getGameProfile();
                playerManager.removeFromOperators(player.getGameProfile());
                return true;
            } else {
                // 玩家离线，从缓存查找
                Optional<GameProfile> profileOpt = Objects.requireNonNull(server.getUserCache()).findByName(name);
                if (profileOpt.isPresent()) profile = profileOpt.get();
                operatorList.remove(new OperatorEntry(profile,4,false));
                return true;
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to deop player {}: {}", name, e.getMessage());
        }
        return false;
    }

    public List<String> getOpList() {
        if (server == null) return Collections.emptyList();
        try{
            PlayerManager playerManager = server.getPlayerManager();
            return List.of(playerManager.getOpNames());
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to get oplist {}", e.getMessage());
            return new ArrayList<>();
        }
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
            Whitelist whitelist = playerManager.getWhitelist();
            Optional<GameProfile> profileOpt = Objects.requireNonNull(server.getUserCache()).findByName(name);
            profileOpt.ifPresent(profile -> whitelist.add(new WhitelistEntry(profile)));
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
            Whitelist whitelist = playerManager.getWhitelist();
            Optional<GameProfile> profileOpt = Objects.requireNonNull(server.getUserCache()).findByName(name);
            profileOpt.ifPresent(profile -> whitelist.remove(new WhitelistEntry(profile)));
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
    
    // Send message to all players in the server
    public void broadcastMessage(String message) {
        if (server == null) return;
        
        try {
            Text textMessage = Text.literal(message);
            server.getPlayerManager().broadcast(textMessage, false);
            Tpsbot.LOGGER.debug("Broadcasted message: {}", message);
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to broadcast message: {}", e.getMessage());
        }
    }
}