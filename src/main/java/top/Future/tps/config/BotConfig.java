package top.Future.tps.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "tpsbot.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser JSON_PARSER = new JsonParser();
    
    private String oneBotUrl = "ws://localhost:6700";
    private long superAdmin = 0L;
    private List<Long> adminGroups = new ArrayList<>();
    private String commandPrefix = "/";
    
    public BotConfig() {
        load();
    }
    
    public void load() {
        // 确保配置目录存在
        File configDir = CONFIG_FILE.getParentFile();
        if (configDir != null && !configDir.exists()) {
            configDir.mkdirs();
        }
        
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                // 使用JsonObject手动解析，避免Gson递归创建BotConfig实例
                JsonObject jsonObject = JSON_PARSER.parse(reader).getAsJsonObject();
                
                // 手动提取字段值
                if (jsonObject.has("oneBotUrl")) {
                    this.oneBotUrl = jsonObject.get("oneBotUrl").getAsString();
                }
                if (jsonObject.has("superAdmin")) {
                    this.superAdmin = jsonObject.get("superAdmin").getAsLong();
                }
                if (jsonObject.has("adminGroups")) {
                    // 手动解析adminGroups数组
                    this.adminGroups = new ArrayList<>();
                    for (var element : jsonObject.getAsJsonArray("adminGroups")) {
                        this.adminGroups.add(element.getAsLong());
                    }
                }
                if (jsonObject.has("commandPrefix")) {
                    this.commandPrefix = jsonObject.get("commandPrefix").getAsString();
                }
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Failed to parse config: " + e.getMessage());
            }
        } else {
            save();
        }
    }
    
    public void save() {
        // 确保配置目录存在
        File configDir = CONFIG_FILE.getParentFile();
        if (configDir != null && !configDir.exists()) {
            configDir.mkdirs();
        }
        
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
    
    // Getters and setters
    public String getOneBotUrl() {
        return oneBotUrl;
    }
    
    public void setOneBotUrl(String oneBotUrl) {
        this.oneBotUrl = oneBotUrl;
        save();
    }
    
    public long getSuperAdmin() {
        return superAdmin;
    }
    
    public void setSuperAdmin(long superAdmin) {
        this.superAdmin = superAdmin;
        save();
    }
    
    public List<Long> getAdminGroups() {
        return adminGroups;
    }
    
    public String getCommandPrefix() {
        return commandPrefix;
    }
    
    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
        save();
    }
}