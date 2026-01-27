package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

import java.util.List;

public class WListCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "wlist";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "输出白名单列表";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String subType, String[] args) {
        List<String> whitelist = Tpsbot.INSTANCE.getServerManager().getWhitelist();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 白名单列表 ===\n");
        sb.append(String.format("白名单人数: %d\n\n", whitelist.size()));
        
        if (whitelist.isEmpty()) {
            sb.append("白名单为空\n");
        } else {
            sb.append(CommandHandler.formatList("白名单成员:", whitelist));
        }
        
        return new CommandHandler.CommandResult(true, CommandHandler.formatSuccess(sb.toString()));
    }
}