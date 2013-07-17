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

        Group worldGroup = WorldInventoriesAPI.findGroup(world);

        WorldInventories.logDebug("Ender Chest opened by " + playerName + " in world " + world + ", group " + worldGroup);

        HashMap<Integer, ItemStack[]> playerInventoryMap = plugin.loadPlayerInventory(playerName,
                                                                                     worldGroup,
                                                                                     me.drayshak.WorldInventories.InventoryLoadType.ENDERCHEST);
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

        if (WorldInventories.exempts.contains(playerName.toLowerCase())) {
            WorldInventories.logDebug("Ignoring exempt player Ender Chest close: " + playerName);
            return;
        }

        Group worldGroup = WorldInventoriesAPI.findGroup(worldName);

        WorldInventories.logDebug("Ender Chest closed by " + playerName + " in world " + worldName + ", group " + worldGroup);

        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, null);
        toSave.put(InventoryStoredType.INVENTORY, inventory.getContents());

        plugin.savePlayerInventory(playerName, worldGroup, me.drayshak.WorldInventories.InventoryLoadType.ENDERCHEST, toSave);
    }
}