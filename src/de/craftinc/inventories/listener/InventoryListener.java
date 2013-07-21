package de.craftinc.inventories.listener;

import java.util.HashMap;

import de.craftinc.inventories.Group;
import de.craftinc.inventories.InventoriesLogger;
import de.craftinc.inventories.InventoryStoredType;
import de.craftinc.inventories.WorldInventories;

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

        if (plugin.isPlayerOnExemptList(playerName)) {
            InventoriesLogger.logDebug("Ignoring exempt player Ender Chest open: " + playerName);
            return;
        }

        Group worldGroup = plugin.findGroup(world);

        InventoriesLogger.logDebug("Ender Chest opened by " + playerName + " in world " + world + ", group " + worldGroup);

        HashMap<Integer, ItemStack[]> playerInventoryMap = plugin.loadPlayerInventory(playerName,
                                                                                      worldGroup,
                                                                                      de.craftinc.inventories.InventoryLoadType.ENDERCHEST);
        inventory.setContents(playerInventoryMap.get(InventoryStoredType.INVENTORY));
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

        if (plugin.isPlayerOnExemptList(playerName)) {
            InventoriesLogger.logDebug("Ignoring exempt player Ender Chest close: " + playerName);
            return;
        }

        Group worldGroup = plugin.findGroup(worldName);

        InventoriesLogger.logDebug("Ender Chest closed by " + playerName + " in world " + worldName + ", group " + worldGroup);

        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, null);
        toSave.put(InventoryStoredType.INVENTORY, inventory.getContents());

        plugin.savePlayerInventory(playerName, worldGroup, de.craftinc.inventories.InventoryLoadType.ENDERCHEST, toSave);
    }
}