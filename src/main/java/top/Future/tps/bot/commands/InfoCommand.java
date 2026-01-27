package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class InfoCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "info";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "输出具体的服务器信息，TPS/MSPT，占用，服务器人数和具体玩家信息等";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_MEMBER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String subType, String[] args) {
        String info = Tpsbot.INSTANCE.getServerManager().getServerInfo().getDetailedInfo();
        return new CommandHandler.CommandResult(true, info);
    }
}