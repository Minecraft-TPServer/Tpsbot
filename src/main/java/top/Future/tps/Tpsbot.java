package top.Future.tps;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.Future.tps.bot.EventSystem;
import top.Future.tps.bot.OneBotClient;
import top.Future.tps.config.BotConfig;
import top.Future.tps.permission.PermissionManager;
import top.Future.tps.server.ServerManager;

public class Tpsbot implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("tpsbot");
    public static Tpsbot INSTANCE;
    
    private BotConfig config;
    private OneBotClient botClient;
    private EventSystem eventSystem;
    private PermissionManager permissionManager;
    private ServerManager serverManager;
    
    @Override
    public void onInitialize() {
        INSTANCE = this;
        
        LOGGER.info("Initializing Tpsbot...");
        
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
        
        LOGGER.info("Tpsbot initialized successfully!");
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
}