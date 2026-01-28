package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class OpCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "op";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "使玩家成为服务器管理员";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_OWNER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole,  String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /op <player>");
        }
        
        String player = args[0];
        boolean success = Tpsbot.INSTANCE.getServerManager().opPlayer(player);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已成功将玩家 %s 设置为管理员", player));
        } else {
            return new CommandHandler.CommandResult(false, String.format("将玩家 %s 设置为管理员失败", player));
        }
    }
}