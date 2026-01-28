package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class WAddCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "wadd";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "添加白名单成员";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        if (args.length != 1) {
            return new CommandHandler.CommandResult(false, "用法: /wadd <player>");
        }
        
        String player = args[0];
        boolean success = Tpsbot.INSTANCE.getServerManager().addWhitelist(player);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已将玩家 %s 添加到白名单", player));
        } else {
            return new CommandHandler.CommandResult(false, String.format("添加玩家 %s 到白名单失败", player));
        }
    }
}