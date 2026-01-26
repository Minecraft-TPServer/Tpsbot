package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class WDelCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "wdel";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "删除白名单成员";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        if (args.length != 1) {
            return new CommandHandler.CommandResult(false, CommandHandler.formatError("用法: /wdel <player>"));
        }
        
        String player = args[0];
        boolean success = Tpsbot.INSTANCE.getServerManager().removeWhitelist(player);
        
        if (success) {
            return new CommandHandler.CommandResult(true, CommandHandler.formatSuccess(String.format("已将玩家 %s 从白名单中删除", player)));
        } else {
            return new CommandHandler.CommandResult(false, CommandHandler.formatError(String.format("从白名单中删除玩家 %s 失败", player)));
        }
    }
}