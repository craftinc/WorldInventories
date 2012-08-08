package me.drayshak.WorldInventories;

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
        if(plugin.getConfig().getBoolean("outputtimertoconsole"))
	{
	    WorldInventories.logStandard("Timer: Saving player information. New save due in " + plugin.getConfig().getInt("saveinterval") + " seconds.");
	}
        
        plugin.savePlayers();
    }
}
