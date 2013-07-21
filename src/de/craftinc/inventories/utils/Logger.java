package de.craftinc.inventories.utils;

import de.craftinc.inventories.Plugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class Logger
{
    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Minecraft");


    public static void logStandard(String line)
    {
        logger.log(Level.INFO, "[Plugin] {0}", line);
    }

    public static void logError(String line)
    {
        logger.log(Level.SEVERE, "[Plugin] {0}", line);
    }

    public static void logDebug(String line)
    {
        logger.log(Level.FINEST, "[Plugin] {0}", line);
    }

    /**
     * Checks if the given message key is hidden or not, send if appropriate
     */
    public static void sendMessage(String key, Player player, String message)
    {
        FileConfiguration config = Plugin.getSharedInstance().getConfig();
        String fullKey = ConfigurationKeys.hideMessagesGroupKey + key;

        if (!config.getBoolean(fullKey, false)) {
            player.sendMessage(message);
        }
    }
}