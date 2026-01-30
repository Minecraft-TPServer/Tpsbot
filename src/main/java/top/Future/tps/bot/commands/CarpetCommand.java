package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

import top.Future.tps.server.ServerManager;

public class CarpetCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "carpet";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }

    @Override
    public String getDescription() {
        return "carpet设置";
    }

    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }

    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole,  String[] args) {
        if (args.length == 0) {
            return new CommandHandler.CommandResult(false, "使用格式: /carpet <command> <true/false/0/1/2/3/ops> 或 /carpet list");
        }

        if ("list".equals(args[0])) {
            // 执行 /carpet 命令，查看所有规则
            String fullCommand = "/carpet";
            String result = Tpsbot.INSTANCE.getServerManager().minecraftCommand(fullCommand);
            
            if (result != null && !result.isEmpty()) {
                return new CommandHandler.CommandResult(true, "执行命令: " + fullCommand + "\n结果: " + result);
            } else {
                return new CommandHandler.CommandResult(true, "执行命令: " + fullCommand + "\n命令返回不明确");
            }
        } else if (args.length < 2) {
            return new CommandHandler.CommandResult(false, "使用格式: /carpet <command> <true/false/0/1/2/3/ops> 或 /carpet list");
        } else {
            // 执行 /carpet setDefault 命令
            String command = args[0];
            String value = args[1];
            String fullCommand = "/carpet setDefault " + command + " " + value;

            String result = Tpsbot.INSTANCE.getServerManager().minecraftCommand(fullCommand);
            
            if (result != null && !result.isEmpty()) {
                return new CommandHandler.CommandResult(true, "执行命令: " + fullCommand + "\n结果: " + result);
            } else {
                return new CommandHandler.CommandResult(true, "执行命令: " + fullCommand + "\n命令返回不明确");
            }
        }
    }
}