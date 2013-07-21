package de.craftinc.inventories;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoriesLogger
{
    protected static final Logger logger = Logger.getLogger("Minecraft");


    public static void logStandard(String line)
    {
        logger.log(Level.INFO, "[WorldInventories] {0}", line);
    }

    public static void logError(String line)
    {
        logger.log(Level.SEVERE, "[WorldInventories] {0}", line);
    }

    public static void logDebug(String line)
    {
        logger.log(Level.FINE, "[WorldInventories] {0}", line);
    }

    /**
     * Checks if the given message key is hidden or not, send if appropriate
     */
    public static void sendMessage(String key, Player player, String message)
    {
        FileConfiguration config = WorldInventories.getSharedInstance().getConfig();

        if (!config.getBoolean("message-hidden." + key, false) &&
             config.getBoolean("donotifications" + key, true)) {

            player.sendMessage(message);
        }
    }
}