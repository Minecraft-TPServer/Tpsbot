package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class BanCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "ban";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "封禁玩家";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /ban <player> [reason]");
        }
        
        String player = args[0];
        String reason = args.length > 1 ? String.join(" ", args).substring(player.length() + 1) : null;
        
        boolean success = Tpsbot.INSTANCE.getServerManager().banPlayer(player, reason);
        
        if (success) {
            return new CommandHandler.CommandResult(true, String.format("已成功封禁玩家 %s", player));
        } else {
            return new CommandHandler.CommandResult(false, String.format("封禁玩家 %s 失败", player));
        }
    }
}