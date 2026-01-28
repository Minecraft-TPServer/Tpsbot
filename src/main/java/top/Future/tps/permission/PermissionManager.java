package top.Future.tps.permission;

import top.Future.tps.Tpsbot;
import top.Future.tps.config.BotConfig;

import java.util.concurrent.ConcurrentHashMap;

public class PermissionManager {
    // Permission levels
    public static final int LEVEL_MEMBER = 1;    // 普通群成员
    public static final int LEVEL_ADMIN = 2;     // 管理员
    public static final int LEVEL_OWNER = 3;     // 群主
    public static final int LEVEL_SUPER_ADMIN = 4; // 超级管理员
    
    // Permission cache to improve performance
    private final ConcurrentHashMap<Long, Integer> permissionCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes cache expiry
    private final ConcurrentHashMap<Long, Long> cacheTimestamps = new ConcurrentHashMap<>();
    
    /**
     * Get permission level for a user
     * @param userId QQ user ID
     * @param groupId Group ID
     * @param userRole User role (owner, admin, member)
     * @return Permission level
     */
    public int getPermissionLevel(long userId, long groupId, String userRole) {
        BotConfig config = Tpsbot.INSTANCE.getConfig();
        
        // Check if user is super admin
        if (userId == config.getSuperAdmin()) {
            return LEVEL_SUPER_ADMIN;
        }
        
        // Generate cache key (userId + groupId)
        long cacheKey = userId * 10000000000L + groupId;
        
        // Check cache with expiry
        if (permissionCache.containsKey(cacheKey)) {
            long timestamp = cacheTimestamps.getOrDefault(cacheKey, 0L);
            if (System.currentTimeMillis() - timestamp < CACHE_EXPIRY_TIME) {
                return permissionCache.get(cacheKey);
            } else {
                // Cache expired, remove it
                permissionCache.remove(cacheKey);
                cacheTimestamps.remove(cacheKey);
            }
        }
        
        // Calculate permission level
        int level;
        switch (userRole) {
            case "owner":
                level = LEVEL_OWNER;
                break;
            case "admin":
                level = LEVEL_ADMIN;
                break;
            default: // normal member
                level = LEVEL_MEMBER;
                break;
        }
        
        // Update cache
        permissionCache.put(cacheKey, level);
        cacheTimestamps.put(cacheKey, System.currentTimeMillis());
        
        return level;
    }
    
    /**
     * Check if user has permission to use a command
     * @param userId QQ user ID
     * @param groupId Group ID
     * @param userRole User role
     * @param requiredLevel Required permission level for the command
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(long userId, long groupId, String userRole, int requiredLevel) {
        int userLevel = getPermissionLevel(userId, groupId, userRole);
        return userLevel >= requiredLevel;
    }
    
    /**
     * Get permission name for a level
     * @param level Permission level
     * @return Human-readable permission name
     */
    public String getPermissionName(int level) {
        switch (level) {
            case LEVEL_SUPER_ADMIN:
                return "超级管理员";
            case LEVEL_OWNER:
                return "群主";
            case LEVEL_ADMIN:
                return "管理员";
            case LEVEL_MEMBER:
                return "普通成员";
            default:
                return "未知权限";
        }
    }
}