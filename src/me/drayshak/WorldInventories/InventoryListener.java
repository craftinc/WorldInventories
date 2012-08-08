package me.drayshak.WorldInventories;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener
{
    private final WorldInventories plugin;
    
    InventoryListener(final WorldInventories plugin)
    {
       this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpened(InventoryOpenEvent event)
    {
        Inventory inventory = event.getInventory();
        
        if(inventory.getType().equals(InventoryType.ENDER_CHEST))
        {
            
        }
    }
}