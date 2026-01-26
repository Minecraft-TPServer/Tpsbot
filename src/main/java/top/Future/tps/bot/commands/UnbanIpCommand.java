package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class UnbanIpCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "unbanip";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "取消封禁IP";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /unbanip <IP>");
        }
        
        String ip = args[0];
        boolean success = Tpsbot.INSTANCE.getServerManager().unbanIp(ip);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已成功取消IP %s 的封禁", ip));
        } else {
            return new CommandHandler.CommandResult(false, String.format("取消IP %s 封禁失败", ip));
        }
    }
}