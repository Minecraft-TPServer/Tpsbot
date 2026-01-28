package top.Future.tps.bot.commands;

import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler;
import top.Future.tps.permission.PermissionManager;

import java.util.List;

public class OpListCommand implements CommandHandler.Command {
    @Override
    public String getName() {
        return "oplist";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }

    @Override
    public String getDescription() {
        return "输出op列表";
    }

    @Override
    public int getRequiredLevel() {
        return PermissionManager.LEVEL_ADMIN;
    }

    @Override
    public CommandHandler.CommandResult execute(long userId, long groupId, String userRole, String[] args) {
        List<String> oplist = Tpsbot.INSTANCE.getServerManager().getOpList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== op列表 ===\n");
        sb.append(String.format("op列表人数: %d\n\n", oplist.size()));

        if (oplist.isEmpty()) {
            sb.append("op列表为空\n");
        } else {
            sb.append(CommandHandler.formatList("op成员:", oplist));
        }

        return new CommandHandler.CommandResult(true, CommandHandler.formatSuccess(sb.toString()));
    }
}