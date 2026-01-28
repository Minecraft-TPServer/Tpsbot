package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

import java.util.List;

public class ListCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "查看服务器人数和具体玩家名称";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_MEMBER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        List<String> players = Tpsbot.INSTANCE.getServerManager().getPlayerNames();
        int count = players.size();
        int max = Tpsbot.INSTANCE.getServerManager().getMaxPlayers();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 服务器在线玩家 ===\n");
        sb.append(String.format("在线人数: %d/%d\n\n", count, max));
        
        if (count == 0) {
            sb.append("当前没有玩家在线\n");
        } else {
            sb.append(CommandHandler.formatPlayerList(players));
        }
        
        return new CommandHandler.CommandResult(true, CommandHandler.formatSuccess(sb.toString()));
    }
}