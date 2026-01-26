package top.Future.tps.permission;

import top.Future.tps.Tpsbot;
import top.Future.tps.config.BotConfig;

public class PermissionManager {
    // Permission levels
    public static final int LEVEL_MEMBER = 1;    // 普通群成员
    public static final int LEVEL_ADMIN = 2;     // 管理员
    public static final int LEVEL_OWNER = 3;     // 群主
    public static final int LEVEL_SUPER_ADMIN = 4; // 超级管理员
    
    /**
     * Get permission level for a user
     * @param userId QQ user ID
     * @param subType Message subtype (normal, admin, owner)
     * @return Permission level
     */
    public int getPermissionLevel(long userId, String subType) {
        BotConfig config = Tpsbot.INSTANCE.getConfig();
        
        // Check if user is super admin
        if (userId == config.getSuperAdmin()) {
            return LEVEL_SUPER_ADMIN;
        }
        
        // Check group role
        switch (subType) {
            case "owner":
                return LEVEL_OWNER;
            case "admin":
                return LEVEL_ADMIN;
            default: // normal member
                return LEVEL_MEMBER;
        }
    }
    
    /**
     * Check if user has permission to use a command
     * @param userId QQ user ID
     * @param subType Message subtype
     * @param requiredLevel Required permission level for the command
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(long userId, String subType, int requiredLevel) {
        int userLevel = getPermissionLevel(userId, subType);
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