package top.Future.tps.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import top.Future.tps.Tpsbot;

public class MinecraftCommand {
    
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher, registryAccess));
    }
    
    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        // 注册 /tpsbot 命令
        dispatcher.register(CommandManager.literal("tpsbot")
            .requires(source -> source.hasPermissionLevel(2)) // 需要OP权限
            .then(CommandManager.literal("reconnect")
                .executes(MinecraftCommand::reconnect)
            )
            .then(CommandManager.literal("status")
                .executes(MinecraftCommand::status)
            )
            .then(CommandManager.literal("sync")
                .then(CommandManager.literal("enable")
                    .executes(MinecraftCommand::enableSync)
                )
                .then(CommandManager.literal("disable")
                    .executes(MinecraftCommand::disableSync)
                )
            )
            .then(CommandManager.literal("reload")
                .executes(MinecraftCommand::reload)
            )
        );
    }
    
    private static int reconnect(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            // 断开现有连接
            if (Tpsbot.INSTANCE.getBotClient() != null) {
                Tpsbot.INSTANCE.getBotClient().disconnect();
            }
            
            // 重新连接
            Tpsbot.INSTANCE.getBotClient().connect();
            
            source.sendFeedback(() -> Text.literal("§a正在重新连接到 OneBot 服务器..."), true);
            Tpsbot.LOGGER.info("Reconnecting to OneBot server...");
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§c重新连接失败: " + e.getMessage()), true);
            Tpsbot.LOGGER.error("Failed to reconnect: {}", e.getMessage());
        }
        
        return 1;
    }
    
    private static int status(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            // 获取机器人状态
            boolean isConnected = Tpsbot.INSTANCE.getBotClient() != null && Tpsbot.INSTANCE.getBotClient().isConnected();
            boolean isSyncEnabled = Tpsbot.INSTANCE.getConfig().isServerGroupSyncEnabled();
            
            StringBuilder statusMsg = new StringBuilder("§6Tpsbot 状态:");
            statusMsg.append("\n§7版本: " + Tpsbot.INSTANCE.getModVersion());
            statusMsg.append("\n§7连接状态: " + (isConnected ? "§a已连接" : "§c未连接"));
            statusMsg.append("\n§7群服互通: " + (isSyncEnabled ? "§a已启用" : "§c已禁用"));
            statusMsg.append("\n§7OneBot URL: §r" + Tpsbot.INSTANCE.getConfig().getOneBotUrl());
            statusMsg.append("\n§7超级管理员: §r" + Tpsbot.INSTANCE.getConfig().getSuperAdmin());
            statusMsg.append("\n§7命令前缀: §r" + Tpsbot.INSTANCE.getConfig().getCommandPrefix());
            
            // 获取服务器基本信息
            String serverStats = Tpsbot.INSTANCE.getServerManager().getServerInfo().getBasicStats();
            statusMsg.append("\n§7服务器状态: §r" + serverStats);
            
            source.sendFeedback(() -> Text.literal(statusMsg.toString()), false);
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§c获取状态失败: " + e.getMessage()), false);
            Tpsbot.LOGGER.error("Failed to get status: {}", e.getMessage());
        }
        
        return 1;
    }
    
    private static int enableSync(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            Tpsbot.INSTANCE.getConfig().setServerGroupSyncEnabled(true);
            source.sendFeedback(() -> Text.literal("§a群服互通功能已启用"), true);
            Tpsbot.LOGGER.info("Server-group sync enabled");
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§c启用群服互通失败: " + e.getMessage()), true);
            Tpsbot.LOGGER.error("Failed to enable sync: {}", e.getMessage());
        }
        
        return 1;
    }
    
    private static int disableSync(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            Tpsbot.INSTANCE.getConfig().setServerGroupSyncEnabled(false);
            source.sendFeedback(() -> Text.literal("§c群服互通功能已禁用"), true);
            Tpsbot.LOGGER.info("Server-group sync disabled");
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§c禁用群服互通失败: " + e.getMessage()), true);
            Tpsbot.LOGGER.error("Failed to disable sync: {}", e.getMessage());
        }
        
        return 1;
    }
    
    private static int reload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            // 重新加载配置文件
            Tpsbot.INSTANCE.getConfig().load();
            
            // 重启机器人连接
            if (Tpsbot.INSTANCE.getBotClient() != null) {
                Tpsbot.INSTANCE.getBotClient().disconnect();
                Tpsbot.INSTANCE.getBotClient().connect();
            }
            
            source.sendFeedback(() -> Text.literal("§aTpsbot 已重新加载配置并重启连接"), true);
            Tpsbot.LOGGER.info("Tpsbot reloaded and reconnecting...");
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§c重新加载失败: " + e.getMessage()), true);
            Tpsbot.LOGGER.error("Failed to reload: {}", e.getMessage());
        }
        
        return 1;
    }
}
