package me.drayshak.WorldInventories.listener;

import java.util.HashMap;
import me.drayshak.WorldInventories.Group;
import me.drayshak.WorldInventories.InventoryStoredType;
import me.drayshak.WorldInventories.WorldInventories;
import me.drayshak.WorldInventories.api.WorldInventoriesAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener
{
    private final WorldInventories plugin;
    
    public InventoryListener(final WorldInventories plugin)
    {
       this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        Inventory inventory = event.getInventory();

        if (inventory.getType() != InventoryType.ENDER_CHEST) {
            return;
        }

        String playerName = event.getPlayer().getName();
        String world = event.getPlayer().getWorld().getName();

        if (WorldInventories.exempts.contains(playerName.toLowerCase())) {
            WorldInventories.logDebug("Ignoring exempt player Ender Chest open: " + playerName);
            return;
        }

        Group worldgroup = WorldInventoriesAPI.findGroup(world);

        WorldInventories.logDebug("Ender Chest opened by " + playerName + " in world " + world + ", group " + worldgroup);

        HashMap<Integer, ItemStack[]> playerIventoryMap = plugin.loadPlayerInventory(playerName,
                                                                                     worldgroup,
                                                                                     me.drayshak.WorldInventories.InventoryLoadType.ENDERCHEST);
        inventory.setContents(playerIventoryMap.get(InventoryStoredType.INVENTORY));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClosed(InventoryCloseEvent event)
    {
        Inventory inventory = event.getInventory();
        
        if (inventory.getType() != InventoryType.ENDER_CHEST) {
            return;
        }

        String playerName = event.getPlayer().getName();
        String worldName = event.getPlayer().getWorld().getName();

        if (WorldInventories.exempts.contains(playerName.toLowerCase())) {
            WorldInventories.logDebug("Ignoring exempt player Ender Chest close: " + playerName);
            return;
        }

        Group worldgroup = WorldInventoriesAPI.findGroup(worldName);

        WorldInventories.logDebug("Ender Chest closed by " + playerName + " in world " + worldName + ", group " + worldgroup);

        HashMap<Integer, ItemStack[]> tosave = new HashMap<Integer, ItemStack[]>();
        tosave.put(InventoryStoredType.ARMOUR, null);
        tosave.put(InventoryStoredType.INVENTORY, inventory.getContents());

        plugin.savePlayerInventory(playerName, worldgroup, me.drayshak.WorldInventories.InventoryLoadType.ENDERCHEST, tosave);
    }
}