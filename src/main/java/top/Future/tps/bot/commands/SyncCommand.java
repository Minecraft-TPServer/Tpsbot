package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class SyncCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "sync";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "开启或关闭群服互通功能";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /sync <enable/disable>");
        }
        
        String action = args[0].toLowerCase();
        
        if ("enable".equals(action)) {
            // 启用群服互通
            Tpsbot.INSTANCE.getConfig().setServerGroupSyncEnabled(true);
            return new CommandHandler.CommandResult(true, "群服互通功能已启用");
        } else if ("disable".equals(action)) {
            // 禁用群服互通
            Tpsbot.INSTANCE.getConfig().setServerGroupSyncEnabled(false);
            return new CommandHandler.CommandResult(true, "群服互通功能已禁用");
        } else {
            return new CommandHandler.CommandResult(false, "使用格式: /sync <enable/disable>");
        }
    }
}
