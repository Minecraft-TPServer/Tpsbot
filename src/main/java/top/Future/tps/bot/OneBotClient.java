package top.Future.tps.bot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import top.Future.tps.Tpsbot;
import top.Future.tps.config.BotConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OneBotClient {
    private static final Gson GSON = new Gson();
    
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final ScheduledExecutorService reconnectExecutor;
    private boolean isConnected;
    private int reconnectAttempts;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int RECONNECT_DELAY = 5;
    
    public OneBotClient() {
        this.client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .build();
        this.reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
        this.isConnected = false;
        this.reconnectAttempts = 0;
    }
    
    public void connect() {
        BotConfig config = Tpsbot.INSTANCE.getConfig();
        String wsUrl = config.getOneBotUrl();
        
        Request request = new Request.Builder().url(wsUrl).build();
        this.webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Tpsbot.LOGGER.info("Connected to OneBot server!");
                isConnected = true;
                reconnectAttempts = 0;
            }
            
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(text);
            }
            
            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // Handle binary messages if needed
            }
            
            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Tpsbot.LOGGER.info("Closing OneBot connection: {}", reason);
                isConnected = false;
            }
            
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Tpsbot.LOGGER.info("OneBot connection closed: {}", reason);
                isConnected = false;
            }
            
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Tpsbot.LOGGER.error("OneBot connection failed: {}", t.getMessage());
                isConnected = false;
                attemptReconnect();
            }
        });
    }
    
    private void attemptReconnect() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            long delay = RECONNECT_DELAY * reconnectAttempts;
            Tpsbot.LOGGER.info("Attempting to reconnect in {} seconds... (Attempt {}/{})");
            reconnectExecutor.schedule(this::connect, delay, TimeUnit.SECONDS);
        } else {
            Tpsbot.LOGGER.error("Max reconnection attempts reached. Please check your OneBot server.");
        }
    }
    
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Mod shutdown");
            webSocket = null;
        }
        reconnectExecutor.shutdownNow();
        isConnected = false;
    }
    
    private void handleMessage(String text) {
        try {
            JsonObject json = GSON.fromJson(text, JsonObject.class);
            Tpsbot.INSTANCE.getEventSystem().handleEvent(json);
        } catch (Exception e) {
            Tpsbot.LOGGER.error("Failed to parse OneBot message: {}", e.getMessage());
        }
    }
    
    public void sendMessage(long groupId, String message) {
        if (!isConnected || webSocket == null) {
            Tpsbot.LOGGER.error("Cannot send message: Not connected to OneBot server");
            return;
        }
        
        JsonObject msg = new JsonObject();
        msg.addProperty("action", "send_group_msg");
        msg.addProperty("echo", "send_group_msg");
        
        JsonObject params = new JsonObject();
        params.addProperty("group_id", groupId);
        params.addProperty("message", message);
        
        msg.add("params", params);
        
        webSocket.send(GSON.toJson(msg));
    }
    
    public boolean isConnected() {
        return isConnected;
    }
}