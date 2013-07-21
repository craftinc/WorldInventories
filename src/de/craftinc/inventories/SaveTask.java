package de.craftinc.inventories;

import java.util.TimerTask;

public class SaveTask extends TimerTask
{
    private final WorldInventories plugin;
    
    public SaveTask(WorldInventories plugin)
    {
	    this.plugin = plugin;
    }
    
    @Override
    public void run()
    {
        if (plugin.getConfig().getBoolean("outputtimertoconsole")) {
            int saveTimeInterval = plugin.getConfig().getInt("saveinterval");
            InventoriesLogger.logStandard("Timer: Saving player information. New save due in " + saveTimeInterval + " seconds.");
	    }
        
        plugin.savePlayers(plugin.getConfig().getBoolean("outputtimertoconsole"));
    }
}
