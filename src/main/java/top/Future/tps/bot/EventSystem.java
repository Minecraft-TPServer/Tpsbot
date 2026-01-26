package top.Future.tps.bot;

import com.google.gson.JsonObject;
import top.Future.tps.Tpsbot;
import top.Future.tps.bot.CommandHandler.CommandResult;
import top.Future.tps.config.BotConfig;
import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    private final CommandHandler commandHandler;
    
    public EventSystem() {
        this.commandHandler = new CommandHandler();
    }
    
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
    
    public void handleEvent(JsonObject event) {
        try {
            String postType = event.get("post_type").getAsString();
            
            switch (postType) {
                case "message":
                    handleMessageEvent(event);
                    break;
                case "notice":
                    handleNoticeEvent(event);
                    break;
                case "request":
                    handleRequestEvent(event);
                    break;
                case "meta_event":
                    handleMetaEvent(event);
                    break;
                default:
                    Tpsbot.LOGGER.debug("Unhandled event type: {}", postType);
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to handle event: {}", e.getMessage());
        }
    }
    
    private void handleMessageEvent(JsonObject event) {
        String messageType = event.get("message_type").getAsString();
        
        if ("group".equals(messageType)) {
            handleGroupMessage(event);
        } else if ("private".equals(messageType)) {
            handlePrivateMessage(event);
        }
    }
    
    private void handleGroupMessage(JsonObject event) {
        long groupId = event.get("group_id").getAsLong();
        long userId = event.get("user_id").getAsLong();
        String message = event.get("message").getAsString();
        String subType = event.get("sub_type").getAsString();
        
        BotConfig config = Tpsbot.INSTANCE.getConfig();
        String prefix = config.getCommandPrefix();
        
        // Check if it's a command
            if (message.startsWith(prefix)) {
                // Remove prefix
                String commandLine = message.substring(prefix.length());
                
                // Parse command with support for quoted arguments
                List<String> parts = parseCommandLine(commandLine);
                
                if (!parts.isEmpty()) {
                    String commandName = parts.get(0).toLowerCase();
                    String[] args = parts.subList(1, parts.size()).toArray(new String[0]);
                    
                    // Execute command
                    CommandResult result = commandHandler.executeCommand(userId, groupId, subType, commandName, args);
                    
                    // Send result back to group
                    if (result != null && result.getMessage() != null) {
                        Tpsbot.INSTANCE.getBotClient().sendMessage(groupId, result.getMessage());
                    }
                }
            }
    }
    
    private void handlePrivateMessage(JsonObject event) {
        // Handle private messages if needed
        long userId = event.get("user_id").getAsLong();
        String message = event.get("message").getAsString();
        
        Tpsbot.LOGGER.debug("Private message from {}: {}", userId, message);
    }
    
    /**
     * Parse command line with support for quoted arguments
     * @param commandLine Command line to parse
     * @return List of parsed arguments
     */
    private List<String> parseCommandLine(String commandLine) {
        List<String> result = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        boolean escaped = false;
        
        for (char c : commandLine.toCharArray()) {
            if (escaped) {
                currentArg.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    result.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else {
                currentArg.append(c);
            }
        }
        
        if (currentArg.length() > 0) {
            result.add(currentArg.toString());
        }
        
        return result;
    }
    
    private void handleNoticeEvent(JsonObject event) {
        // Handle notice events if needed
        String noticeType = event.get("notice_type").getAsString();
        Tpsbot.LOGGER.debug("Notice event: {}", noticeType);
    }
    
    private void handleRequestEvent(JsonObject event) {
        // Handle request events if needed
        String requestType = event.get("request_type").getAsString();
        Tpsbot.LOGGER.debug("Request event: {}", requestType);
    }
    
    private void handleMetaEvent(JsonObject event) {
        String metaType = event.get("meta_event_type").getAsString();
        
        switch (metaType) {
            case "heartbeat":
                handleHeartbeatEvent(event);
                break;
            case "lifecycle":
                handleLifecycleEvent(event);
                break;
            default:
                Tpsbot.LOGGER.debug("Meta event: {}", metaType);
        }
    }
    
    private void handleHeartbeatEvent(JsonObject event) {
        // Get status info from heartbeat
        JsonObject status = event.getAsJsonObject("status");
        if (status != null) {
            boolean online = status.get("online").getAsBoolean();
            Tpsbot.LOGGER.debug("OneBot heartbeat: online={}", online);
        }
    }
    
    private void handleLifecycleEvent(JsonObject event) {
        String subType = event.get("sub_type").getAsString();
        switch (subType) {
            case "enable":
                Tpsbot.LOGGER.info("OneBot lifecycle: enabled");
                break;
            case "disable":
                Tpsbot.LOGGER.info("OneBot lifecycle: disabled");
                break;
            case "connect":
                Tpsbot.LOGGER.info("OneBot lifecycle: connected");
                break;
            default:
                Tpsbot.LOGGER.debug("OneBot lifecycle: {}", subType);
        }
    }
}