package de.craftinc.inventories.api;

import de.craftinc.inventories.persistence.InventoryLoadType;
import de.craftinc.inventories.Group;
import de.craftinc.inventories.PlayerStats;
import de.craftinc.inventories.WorldInventories;

import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.List;


public class WorldInventoriesAPI
{
    /**
     * Returns the first group associated with a world, otherwise the default
     */
    public static Group findGroup(String world)
    {
        return WorldInventories.getSharedInstance().findGroup(world);
    }

    /**
     * Returns a list of groups currently loaded
     */
    public static List<Group> getGroups()
    {
        return WorldInventories.getSharedInstance().getAllGroups();
    }
    
    /**
     * Loads and returns inventories based on the InventoryLoadType given.
     * Type and locations are defined in InventoryStoredType.
     * Loading an Enderchest stores it in type INVENTORY
     */
    public static HashMap<Integer, ItemStack[]> getPlayerInventory(String player, Group group, InventoryLoadType type)
    {
        return WorldInventories.getSharedInstance().loadPlayerInventory(player, group, type);
    }
    
    /**
     * Loads and returns all player stats.
     */
    public static PlayerStats getPlayerStats(String player, Group group)
    {
        return WorldInventories.getSharedInstance().loadPlayerStats(player, group);
    }
}
