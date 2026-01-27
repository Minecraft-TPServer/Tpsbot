package top.Future.tps.bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
            if (event == null) {
                Tpsbot.LOGGER.error("Failed to handle event: event is null");
                return;
            }
            
            if (!event.has("post_type")) {
                Tpsbot.LOGGER.error("Failed to handle event: missing post_type field");
                Tpsbot.LOGGER.debug("Event: {}", event.toString());
                return;
            }
            
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
                    Tpsbot.LOGGER.debug("Event: {}", event.toString());
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to handle event: {}", e.getMessage());
            Tpsbot.LOGGER.debug("Event: {}", event != null ? event.toString() : "null");
            e.printStackTrace();
        }
    }
    
    private void handleMessageEvent(JsonObject event) {
        if (!event.has("message_type")) {
            Tpsbot.LOGGER.error("Failed to handle message event: missing message_type field");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
        String messageType = event.get("message_type").getAsString();
        
        if ("group".equals(messageType)) {
            handleGroupMessage(event);
        } else if ("private".equals(messageType)) {
            handlePrivateMessage(event);
        } else {
            Tpsbot.LOGGER.debug("Unhandled message type: {}", messageType);
        }
    }
    
    private void handleGroupMessage(JsonObject event) {
        if (!event.has("group_id") || !event.has("user_id") || !event.has("message") || !event.has("sub_type")) {
            Tpsbot.LOGGER.error("Failed to handle group message: missing required fields");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
        long groupId = event.get("group_id").getAsLong();
        long userId = event.get("user_id").getAsLong();
        
        // 处理 message 字段，可能是字符串、JsonObject 或 JsonArray
        String message = "";
        try {
            if (event.get("message").isJsonPrimitive()) {
                message = event.get("message").getAsString();
            } else if (event.get("message").isJsonObject()) {
                // 处理JsonObject类型的message
                JsonObject messageObj = event.getAsJsonObject("message");
                if (messageObj.has("text")) {
                    message = messageObj.get("text").getAsString();
                } else {
                    message = messageObj.toString();
                }
            } else if (event.get("message").isJsonArray()) {
                // 处理JsonArray类型的message（消息段数组）
                StringBuilder messageBuilder = new StringBuilder();
                JsonArray messageArray = event.getAsJsonArray("message");
                for (int i = 0; i < messageArray.size(); i++) {
                    JsonElement element = messageArray.get(i);
                    if (element.isJsonObject()) {
                        JsonObject segment = element.getAsJsonObject();
                        if (segment.has("type") && segment.has("data")) {
                            String type = segment.get("type").getAsString();
                            JsonObject data = segment.getAsJsonObject("data");
                            if ("text".equals(type) && data.has("text")) {
                                messageBuilder.append(data.get("text").getAsString());
                            }
                        }
                    } else if (element.isJsonPrimitive()) {
                        messageBuilder.append(element.getAsString());
                    }
                }
                message = messageBuilder.toString();
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to parse message: {}", e.getMessage());
            Tpsbot.LOGGER.debug("Message element: {}", event.get("message").toString());
        }
        
        // 处理 sub_type 字段
        String subType = "";
        try {
            if (event.get("sub_type").isJsonPrimitive()) {
                subType = event.get("sub_type").getAsString();
            }
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to parse sub_type: {}", e.getMessage());
        }
        
        BotConfig config = Tpsbot.INSTANCE.getConfig();
        String prefix = config.getCommandPrefix();
        
        // Check if it's a command
        if (!message.isEmpty() && message.startsWith(prefix)) {
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
        if (!event.has("user_id") || !event.has("message")) {
            Tpsbot.LOGGER.error("Failed to handle private message: missing required fields");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
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
        if (!event.has("notice_type")) {
            Tpsbot.LOGGER.error("Failed to handle notice event: missing notice_type field");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
        // Handle notice events if needed
        String noticeType = event.get("notice_type").getAsString();
        Tpsbot.LOGGER.debug("Notice event: {}", noticeType);
    }
    
    private void handleRequestEvent(JsonObject event) {
        if (!event.has("request_type")) {
            Tpsbot.LOGGER.error("Failed to handle request event: missing request_type field");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
        // Handle request events if needed
        String requestType = event.get("request_type").getAsString();
        Tpsbot.LOGGER.debug("Request event: {}", requestType);
    }
    
    private void handleMetaEvent(JsonObject event) {
        if (!event.has("meta_event_type")) {
            Tpsbot.LOGGER.error("Failed to handle meta event: missing meta_event_type field");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
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
                Tpsbot.LOGGER.debug("Event: {}", event.toString());
        }
    }
    
    private void handleHeartbeatEvent(JsonObject event) {
        // Get status info from heartbeat
        JsonObject status = event.getAsJsonObject("status");
        if (status != null) {
            if (status.has("online")) {
                boolean online = status.get("online").getAsBoolean();
                Tpsbot.LOGGER.debug("OneBot heartbeat: online={}", online);
            } else {
                Tpsbot.LOGGER.debug("OneBot heartbeat: no online status available");
            }
        } else {
            Tpsbot.LOGGER.debug("OneBot heartbeat: no status available");
        }
    }
    
    private void handleLifecycleEvent(JsonObject event) {
        if (!event.has("sub_type")) {
            Tpsbot.LOGGER.error("Failed to handle lifecycle event: missing sub_type field");
            Tpsbot.LOGGER.debug("Event: {}", event.toString());
            return;
        }
        
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