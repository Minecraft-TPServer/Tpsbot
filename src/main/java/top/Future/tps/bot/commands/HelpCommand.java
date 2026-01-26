package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

import java.util.Map;

public class HelpCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{};
    }
    
    @Override
    public String getDescription() {
        return "显示帮助信息";
    }
    
    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_MEMBER;
    }
    
    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Tpsbot 命令帮助 ===\n");
        sb.append("使用格式: /命令 [参数]\n\n");
        
        // Get all commands
        Map<String, CommandHandler.Command> commands = Tpsbot.INSTANCE.getEventSystem().getCommandHandler().getCommands();
        
        // Group commands by permission level
        for (int level = 1; level <= 4; level++) {
            boolean hasCommands = false;
            for (CommandHandler.Command command : commands.values()) {
                if (command.getRequiredLevel() == level) {
                    if (!hasCommands) {
                        hasCommands = true;
                        sb.append(String.format("--- 权限等级 %d (%s) ---\n", 
                                level, Tpsbot.INSTANCE.getPermissionManager().getPermissionName(level)));
                    }
                    sb.append(String.format("/%s - %s\n", 
                            command.getName(), command.getDescription()));
                }
            }
            if (hasCommands) {
                sb.append("\n");
            }
        }
        
        return new CommandHandler.CommandResult(true, sb.toString());
    }
}