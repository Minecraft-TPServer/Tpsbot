package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;
import java.util.Arrays;

public class KickCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "kick";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "踢出玩家";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String subType,  String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, CommandHandler.formatError("用法: /kick <player> [reason]"));
        }
        
        String player = args[0];
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "被管理员踢出服务器";
        
        boolean success = Tpsbot.INSTANCE.getServerManager().kickPlayer(player, reason);
        
        if (success) {
            return new CommandHandler.CommandResult(true, CommandHandler.formatSuccess(String.format("已将玩家 %s 踢出服务器，原因: %s", player, reason)));
        } else {
            return new CommandHandler.CommandResult(false, CommandHandler.formatError(String.format("踢出玩家 %s 失败", player)));
        }
    }
}