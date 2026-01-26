package top.Future.tps.server;

import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class ServerInfo {
    private MinecraftServer server;
    
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
    
    // Get average TPS over the history
    public double getAverageTps() {
        return Math.min(20.0, 1000.0 / getAverageMspt());
    }
    
    // Get average MSPT over the history
    public double getAverageMspt() {
        return server.getAverageTickTime();
    }
    
    // Get server version
    public String getServerVersion() {
        if (server == null) return "Unknown";
        return server.getVersion();
    }
    
    // Get server MOTD
    public String getMotd() {
        if (server == null) return "Unknown";
        return server.getServerMotd();
    }
    
    // Get JVM memory usage
    public long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
    
    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
    
    // Get CPU usage (simplified)
    public double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        // Note: This is a simplified approach, in reality you'd need more sophisticated CPU monitoring
        return osBean.getSystemLoadAverage();
    }
    
    // Format memory usage as human-readable string
    public String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "i";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    // Generate detailed server info message
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== 服务器信息 ===\n");
        sb.append(String.format("版本: %s\n", getServerVersion()));
        sb.append(String.format("MOTD: %s\n", getMotd()));
        sb.append(String.format("TPS: %.1f (平均)\n", getAverageTps()));
        sb.append(String.format("MSPT: %.1f ms (平均)\n", getAverageMspt()));
        
        // Memory usage
        long usedMem = getUsedMemory();
        long maxMem = getMaxMemory();
        sb.append(String.format("内存: %s / %s (%.1f%%)\n", 
                formatMemory(usedMem), formatMemory(maxMem), 
                (double) usedMem / maxMem * 100));
        
        // CPU usage
        sb.append(String.format("CPU负载: %.2f\n", getCpuUsage()));
        
        // Player info
        if (server != null) {
            int playerCount = server.getPlayerManager().getCurrentPlayerCount();
            int maxPlayers = server.getPlayerManager().getMaxPlayerCount();
            sb.append(String.format("在线玩家: %d / %d\n", playerCount, maxPlayers));
            
            if (playerCount > 0) {
                sb.append("玩家列表: ");
                server.getPlayerManager().getPlayerList().forEach(player -> {
                    sb.append(player.getGameProfile().getName()).append(", ");
                });
                // Remove trailing comma
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2);
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    // Get basic server stats
    public String getBasicStats() {
        if (server == null) return "服务器未启动";
        
        int playerCount = server.getPlayerManager().getCurrentPlayerCount();
        int maxPlayers = server.getPlayerManager().getMaxPlayerCount();
        
        return String.format("TPS: %.1f | MSPT: %.1f ms | 在线: %d/%d", 
                getAverageTps(), getAverageMspt(), playerCount, maxPlayers);
    }
}