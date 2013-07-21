package de.craftinc.inventories.persistence;

import de.craftinc.inventories.WorldInventories;
import de.craftinc.inventories.utils.ConfigurationKeys;
import de.craftinc.inventories.utils.Logger;

import java.util.TimerTask;

public class SaveTask extends TimerTask
{
    @Override
    public void run()
    {
        WorldInventories plugin = WorldInventories.getSharedInstance();
        boolean logSaveTimerMessages = plugin.getConfig().getBoolean(ConfigurationKeys.logSaveTimerMessagesKey);

        if (logSaveTimerMessages) {
            int saveTimeInterval = plugin.getConfig().getInt(ConfigurationKeys.saveTimerIntervalKey);
            Logger.logStandard("Timer: Saving player information. New save due in " + saveTimeInterval + " seconds.");
	    }

        InventoryPersistenceManager.savePlayers(logSaveTimerMessages);
    }
}
