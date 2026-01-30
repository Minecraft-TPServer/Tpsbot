package top.Future.tps;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.Future.tps.bot.EventSystem;
import top.Future.tps.bot.OneBotClient;
import top.Future.tps.config.BotConfig;
import top.Future.tps.permission.PermissionManager;
import top.Future.tps.server.ServerManager;
import top.Future.tps.bot.commands.InfoCommand;

import java.util.Optional;

import static top.Future.tps.permission.PermissionManager.LEVEL_ADMIN;

public class Tpsbot implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("tpsbot");
    public static Tpsbot INSTANCE;
    public static final String MOD_ID = "tpsbot";
    
    private BotConfig config;
    private OneBotClient botClient;
    private EventSystem eventSystem;
    private PermissionManager permissionManager;
    private ServerManager serverManager;
    String modVersion = "UNKNOWN";
    @Override
    public void onInitialize() {
        INSTANCE = this;


        try {
            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(MOD_ID);
            if (container.isPresent()) {
                modVersion = container.get().getMetadata().getVersion().getFriendlyString();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to get mod version: {}", e.getMessage());
        }
        LOGGER.info("Initializing Tpsbot {}...", modVersion);

        // Initialize components - BotConfig first
        config = new BotConfig();
        LOGGER.info("BotConfig initialized successfully!");

        permissionManager = new PermissionManager();
        serverManager = new ServerManager();
        eventSystem = new EventSystem();
        botClient = new OneBotClient();

        // Register Minecraft commands
        top.Future.tps.command.MinecraftCommand.register();
        LOGGER.info("Minecraft commands registered successfully!");

        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Server started, connecting to OneBot...");
            // 设置服务器实例到ServerManager
            serverManager.setServer(server);
            botClient.connect();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("Server stopping, disconnecting from OneBot...");
            if (botClient != null) {
                botClient.disconnect();
            }
        });

        // Server-group sync functionality will be implemented here
        LOGGER.info("Server-group sync functionality initialized!");

        // Register chat message event listener
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (!(sender instanceof ServerPlayerEntity serverPlayer)) return;
            
            // Check if server-group sync is enabled
            if (!config.isServerGroupSyncEnabled()) return;
            
            try {
                String playerName = serverPlayer.getName().getString();
                String messageContent = message.getContent().getString();
                String formattedMessage = "[游戏] " + playerName + ": " + messageContent;
                
                // Send message to all admin groups
                for (long groupId : config.getAdminGroups()) {
                    botClient.sendMessage(groupId, formattedMessage);
                }
                LOGGER.debug("Game chat message forwarded: {}: {}", playerName, messageContent);
            } catch (Exception e) {
                LOGGER.error("Failed to handle game chat message: {}", e.getMessage());
            }
        });

        for (long groupId : config.getAdminGroups()) {
            InfoCommand infoCommand = new InfoCommand();
            infoCommand.execute(0,groupId,"member",new String[]{});
        }
        LOGGER.info("Tpsbot initialized successfully!");
        LOGGER.info("Game chat message forwarding to QQ enabled!");
    }
    
    // Getters
    public BotConfig getConfig() {
        return config;
    }
    
    public OneBotClient getBotClient() {
        return botClient;
    }
    
    public EventSystem getEventSystem() {
        return eventSystem;
    }
    
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
    
    public ServerManager getServerManager() {
        return serverManager;
    }

    public String getModVersion() {
        return modVersion;
    }
}