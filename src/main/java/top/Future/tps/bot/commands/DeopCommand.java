package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class DeopCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "deop";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "取消玩家的服务器管理员身份";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_OWNER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole,  String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /deop <player>");
        }
        
        String player = args[0];
        boolean success = Tpsbot.INSTANCE.getServerManager().deopPlayer(player);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已成功取消玩家 %s 的管理员身份", player));
        } else {
            return new CommandHandler.CommandResult(false, String.format("取消玩家 %s 管理员身份失败", player));
        }
    }
}