package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class UnbanCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "unban";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "取消封禁玩家";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /unban <player>");
        }
        
        String player = args[0];
        boolean success = Tpsbot.INSTANCE.getServerManager().unbanPlayer(player);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已成功取消玩家 %s 的封禁", player));
        } else {
            return new CommandHandler.CommandResult(false, String.format("取消玩家 %s 封禁失败", player));
        }
    }
}