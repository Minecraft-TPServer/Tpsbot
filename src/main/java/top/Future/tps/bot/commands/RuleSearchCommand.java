package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class RuleSearchCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "rulesearch";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "查找carpet命令信息";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_MEMBER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        if (args.length < 1) {
            return new CommandHandler.CommandResult(false, "使用格式: /rulesearch <rule>");
        }
        
        String rule = args[0];
        String fullCommand = "/ruleSearch " + rule;
        
        String result = Tpsbot.INSTANCE.getServerManager().minecraftCommand(fullCommand);
        
        if (result != null && !result.isEmpty()) {
            return new CommandHandler.CommandResult(true, "执行命令: " + fullCommand + "\n结果: " + result);
        } else {
            return new CommandHandler.CommandResult(true, "执行命令: " + fullCommand + "\n命令执行成功");
        }
    }
}
