package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class BanIpCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "banip";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "封禁IP";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /banip <IP> [reason]");
        }
        
        String ip = args[0];
        String reason = args.length > 1 ? String.join(" ", args).substring(ip.length() + 1) : null;
        
        boolean success = Tpsbot.INSTANCE.getServerManager().banIp(ip, reason);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已成功封禁IP %s", ip));
        } else {
            return new CommandHandler.CommandResult(false, String.format("封禁IP %s 失败", ip));
        }
    }
}