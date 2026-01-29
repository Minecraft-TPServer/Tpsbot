package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class InfoCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "info";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "输出具体的服务器信息，TPS/MSPT，占用，服务器人数和具体玩家信息等";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_MEMBER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        // 获取服务器详细信息
        String serverInfo = Tpsbot.INSTANCE.getServerManager().getServerInfo().getDetailedInfo();
        
        // 获取机器人状态信息
        boolean isConnected = Tpsbot.INSTANCE.getBotClient() != null && Tpsbot.INSTANCE.getBotClient().isConnected();
        boolean isSyncEnabled = Tpsbot.INSTANCE.getConfig().isServerGroupSyncEnabled();
        
        // 获取版本号
        String modVersion = Tpsbot.INSTANCE.getModVersion();
        
        // 构建完整的信息
        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("===Tpsbot 信息===");
        infoBuilder.append("\n版本: " + modVersion);
        infoBuilder.append("\n连接状态: " + (isConnected ? "已连接" : "未连接"));
        infoBuilder.append("\n群服互通: " + (isSyncEnabled ? "已启用" : "已禁用"));
        infoBuilder.append("\nOneBot URL: " + Tpsbot.INSTANCE.getConfig().getOneBotUrl());
        infoBuilder.append("\n超级管理员: " + Tpsbot.INSTANCE.getConfig().getSuperAdmin());
        infoBuilder.append("\n命令前缀: " + Tpsbot.INSTANCE.getConfig().getCommandPrefix());
        infoBuilder.append("\n");
        infoBuilder.append(serverInfo);
        
        return new CommandHandler.CommandResult(true, infoBuilder.toString());
    }
}