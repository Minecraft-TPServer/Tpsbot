package top.Future.tps.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import top.Future.tps.Tpsbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "tpsbot.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private String oneBotUrl = "ws://localhost:6700";
    private long superAdmin = 0L;
    private List<Long> adminGroups = new ArrayList<>();
    private String commandPrefix = "/";
    
    public BotConfig() {
        load();
    }
    
    public void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                BotConfig loadedConfig = GSON.fromJson(reader, BotConfig.class);
                if (loadedConfig != null) {
                    this.oneBotUrl = loadedConfig.oneBotUrl;
                    this.superAdmin = loadedConfig.superAdmin;
                    this.adminGroups = loadedConfig.adminGroups;
                    this.commandPrefix = loadedConfig.commandPrefix;
                }
            } catch (IOException e) {
                Tpsbot.LOGGER.error("Failed to load config: {}", e.getMessage());
            }
        } else {
            save();
        }
    }
    
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            Tpsbot.LOGGER.error("Failed to save config: {}", e.getMessage());
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