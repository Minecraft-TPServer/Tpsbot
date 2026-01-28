package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class RebootCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "reboot";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "重启服务器";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_OWNER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        // Send confirmation message first
        String message = "服务器将在1秒后重启...";
        Tpsbot.INSTANCE.getBotClient().sendMessage(groupId, message);
        
        // Reboot the server
        Tpsbot.INSTANCE.getServerManager().rebootServer();
        
        return new CommandHandler.CommandResult(true, message);
    }
}