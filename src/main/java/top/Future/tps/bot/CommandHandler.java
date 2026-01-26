package top.Future.tps.bot;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.commands.*;
import top.Future.tps.permission.PermissionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands;
    private final PermissionManager permissionManager;
    
    public CommandHandler() {
        this.commands = new HashMap<>();
        this.permissionManager = Tpsbot.INSTANCE.getPermissionManager();
        registerCommands();
    }
    
    private void registerCommands() {
        // Register all commands
        registerCommand(new HelpCommand());
        registerCommand(new ListCommand());
        registerCommand(new InfoCommand());
        registerCommand(new AdminTestCommand());
        registerCommand(new WListCommand());
        registerCommand(new WAddCommand());
        registerCommand(new WDelCommand());
        registerCommand(new KickCommand());
        registerCommand(new BanCommand());
        registerCommand(new BanIpCommand());
        registerCommand(new UnbanCommand());
        registerCommand(new UnbanIpCommand());
        registerCommand(new OpCommand());
        registerCommand(new DeopCommand());
        registerCommand(new StopCommand());
        registerCommand(new RebootCommand());
        
        Tpsbot.LOGGER.info("Registered {} commands", commands.size());
    }
    
    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
        // Register aliases if any
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }
    
    public CommandResult executeCommand(long userId, long groupId, String subType, String commandName, String[] args) {
        Command command = commands.get(commandName.toLowerCase());
        
        if (command == null) {
            return new CommandResult(false, "未知命令，请使用/help查看所有命令");
        }
        
        // Check permission
        int requiredLevel = command.getRequiredLevel();
        if (!permissionManager.hasPermission(userId, subType, requiredLevel)) {
            return new CommandResult(false, "权限不足，无法执行该命令");
        }
        
        // Execute command
        try {
            return command.execute(userId, groupId, args);
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Error executing command {}: {}", commandName, e.getMessage());
            return new CommandResult(false, "执行命令时发生错误: " + e.getMessage());
        }
    }
    
    public Map<String, Command> getCommands() {
        return commands;
    }
    
    // Command interface
    public interface Command {
        String getName();
        String[] getAliases();
        String getDescription();
        int getRequiredLevel();
        CommandResult execute(long userId, long groupId, String[] args);
    }
    
    // Command result class
    public static class CommandResult {
        private final boolean success;
        private final String message;
        
        public CommandResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    // Formatting utility methods
    public static String formatSuccess(String message) {
        return "✅ " + message;
    }
    
    public static String formatError(String message) {
        return "❌ " + message;
    }
    
    public static String formatList(String title, List<String> items) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        if (items.isEmpty()) {
            sb.append("   (空)");
        } else {
            for (String item : items) {
                sb.append("   • ").append(item).append("\n");
            }
        }
        return sb.toString().trim();
    }
    
    public static String formatPlayerList(List<String> players) {
        StringBuilder sb = new StringBuilder();
        sb.append("在线玩家 (").append(players.size()).append("):\n");
        if (players.isEmpty()) {
            sb.append("   无在线玩家");
        } else {
            for (String player : players) {
                sb.append("   • ").append(player).append("\n");
            }
        }
        return sb.toString().trim();
    }
}