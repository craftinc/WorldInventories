package me.drayshak.WorldInventories;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener
{
    private final WorldInventories plugin;
    
    EntityListener(final WorldInventories plugin)
    {
       this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Player)
        {
            Player player = (Player)event.getEntity();
            String world = player.getWorld().getName();
            
            Group togroup = WorldInventories.findFirstGroupForWorld(world);
            String togroupname = "default";
            if(togroup != null) togroupname = togroup.getName();               
            
            boolean doKeepInventory = false;
            try
            {
                doKeepInventory = togroup.doesKeepInventory();
            }
            catch (NullPointerException e)
            {
                
            }
            
            if(doKeepInventory)
            {
                WorldInventories.logDebug("Player " + player.getName() + " died in world " + world + ", set to keep inventory: " + togroupname);
                event.getDrops().clear();
            }
            else
            {
                WorldInventories.logDebug("Player " + player.getName() + " died in world " + world + ", emptying inventory for group: " + togroupname);
                
                
                // Make the saved inventory blank so players can't duplicate by switching worlds and picking items back up
                plugin.savePlayerInventory(player.getName(), togroup, new PlayerInventoryHelper(new ItemStack[36], new ItemStack[4]));
            }
            
            if(plugin.getConfig().getBoolean("dostats"))
            {
                plugin.savePlayerStats(player.getName(), togroup, new PlayerStats(20, 20, 0, 0, 0, 0F));
            }
        }
    }
}
