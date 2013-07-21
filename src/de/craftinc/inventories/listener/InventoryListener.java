package de.craftinc.inventories.listener;

import java.util.HashMap;

import de.craftinc.inventories.Group;
import de.craftinc.inventories.persistence.InventoryLoadType;
import de.craftinc.inventories.persistence.InventoryPersistenceManager;
import de.craftinc.inventories.utils.Logger;
import de.craftinc.inventories.persistence.InventoryStoredType;
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
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        Inventory inventory = event.getInventory();
        WorldInventories plugin = WorldInventories.getSharedInstance();

        if (inventory.getType() != InventoryType.ENDER_CHEST) {
            return;
        }

        String playerName = event.getPlayer().getName();
        String world = event.getPlayer().getWorld().getName();

        if (plugin.isPlayerOnExemptList(playerName)) {
            Logger.logDebug("Ignoring exempt player Ender Chest open: " + playerName);
            return;
        }

        Group worldGroup = plugin.findGroup(world);

        Logger.logDebug("Ender Chest opened by " + playerName + " in world " + world + ", group " + worldGroup);

        HashMap<Integer, ItemStack[]> playerInventoryMap = InventoryPersistenceManager.loadPlayerInventory(playerName,
                worldGroup,
                InventoryLoadType.ENDERCHEST);
        inventory.setContents(playerInventoryMap.get(InventoryStoredType.INVENTORY));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClosed(InventoryCloseEvent event)
    {
        Inventory inventory = event.getInventory();
        WorldInventories plugin = WorldInventories.getSharedInstance();
        
        if (inventory.getType() != InventoryType.ENDER_CHEST) {
            return;
        }

        String playerName = event.getPlayer().getName();
        String worldName = event.getPlayer().getWorld().getName();

        if (plugin.isPlayerOnExemptList(playerName)) {
            Logger.logDebug("Ignoring exempt player Ender Chest close: " + playerName);
            return;
        }

        Group worldGroup = plugin.findGroup(worldName);

        Logger.logDebug("Ender Chest closed by " + playerName + " in world " + worldName + ", group " + worldGroup);

        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, null);
        toSave.put(InventoryStoredType.INVENTORY, inventory.getContents());

        InventoryPersistenceManager.savePlayerInventory(playerName, worldGroup, InventoryLoadType.ENDERCHEST, toSave);
    }
}