package de.craftinc.inventories.persistence;

import de.craftinc.inventories.Plugin;
import de.craftinc.inventories.utils.ConfigurationKeys;
import de.craftinc.inventories.utils.Logger;

import java.util.TimerTask;

public class InventorySaveTask extends TimerTask
{
    @Override
    public void run()
    {
        Plugin plugin = Plugin.getSharedInstance();
        boolean logSaveTimerMessages = plugin.getConfig().getBoolean(ConfigurationKeys.logSaveTimerMessagesKey);

        if (logSaveTimerMessages) {
            int saveTimeInterval = plugin.getConfig().getInt(ConfigurationKeys.saveTimerIntervalKey);
            Logger.logStandard("Timer: Saving player information. New save due in " + saveTimeInterval + " seconds.");
	    }

        InventoryPersistenceManager.savePlayers(logSaveTimerMessages);
    }
}
