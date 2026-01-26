package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class StopCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "stop";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "停止服务器";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_OWNER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        // Send confirmation message first
        String message = "服务器将在1秒后停止...";
        Tpsbot.INSTANCE.getBotClient().sendMessage(groupId, message);
        
        // Stop the server
        Tpsbot.INSTANCE.getServerManager().stopServer();
        
        return new CommandHandler.CommandResult(true, message);
    }
}