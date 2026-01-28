package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

public class AdminTestCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "admin_test";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "显示具体的权限信息";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_MEMBER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        PermissionManager permissionManager = Tpsbot.INSTANCE.getPermissionManager();
        int userLevel = permissionManager.getPermissionLevel(userId, groupId, userRole);
        String userLevelName = permissionManager.getPermissionName(userLevel);

        String sb = "=== 权限等级信息 ===\n" +
                "当前用户权限等级: " + userLevel + " - " + userLevelName + "\n\n" +
                "权限等级说明:\n" +
                "1. 普通成员 - 可以使用基础命令\n" +
                "2. 管理员 - 可以使用管理命令\n" +
                "3. 群主 - 可以使用高级管理命令\n" +
                "4. 超级管理员 - 可以使用所有命令\n\n" +
                "使用 /help 查看各命令所需权限等级\n";
        
        return new CommandHandler.CommandResult(true, sb);
    }
}