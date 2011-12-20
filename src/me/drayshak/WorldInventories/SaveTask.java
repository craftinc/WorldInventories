package me.drayshak.WorldInventories;

import java.util.TimerTask;
import org.bukkit.entity.Player;

public class SaveTask extends TimerTask
{
    private final WorldInventories plugin;
    
    public SaveTask(WorldInventories plugin)
    {
	this.plugin = plugin;
    }
    
    public void run()
    {
        WorldInventories.logStandard("Timer: saving player inventories.");
        
        for(Player player : WorldInventories.bukkitServer.getOnlinePlayers())
        {
            String world = player.getLocation().getWorld().getName();

            Group tGroup = WorldInventories.findFirstGroupForWorld(world);

            // Don't save if we don't care where we are (default group)
            if(tGroup != null)
            {    
                plugin.savePlayerInventory(player.getName(), WorldInventories.findFirstGroupForWorld(world), plugin.getPlayerInventory(player));
            }
        }
        
        WorldInventories.logStandard("Done - another save due in " + WorldInventories.saveInterval*1000 + " seconds.");

    }
}
