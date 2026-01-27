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
            
            StringBuilder statusMsg = new StringBuilder("§6Tpsbot 状态:");
            statusMsg.append("\n§7连接状态: ").append(isConnected ? "§a已连接" : "§c未连接");
            statusMsg.append("\n§7OneBot URL: §r").append(Tpsbot.INSTANCE.getConfig().getOneBotUrl());
            statusMsg.append("\n§7超级管理员: §r").append(Tpsbot.INSTANCE.getConfig().getSuperAdmin());
            statusMsg.append("\n§7命令前缀: §r").append(Tpsbot.INSTANCE.getConfig().getCommandPrefix());
            
            // 获取服务器基本信息
            String serverStats = Tpsbot.INSTANCE.getServerManager().getServerInfo().getBasicStats();
            statusMsg.append("\n§7服务器状态: §r").append(serverStats);
            
            source.sendFeedback(() -> Text.literal(statusMsg.toString()), false);
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§c获取状态失败: " + e.getMessage()), false);
            Tpsbot.LOGGER.error("Failed to get status: {}", e.getMessage());
        }
        
        return 1;
    }
}
