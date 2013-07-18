package de.craftinc.inventories.api;

import de.craftinc.inventories.InventoryLoadType;
import de.craftinc.inventories.Group;
import de.craftinc.inventories.PlayerStats;
import de.craftinc.inventories.WorldInventories;

import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.List;


public class WorldInventoriesAPI
{
    private final WorldInventories plugin;
    
    /*
     * Returns the first group associated with a world, otherwise the default
     */
    public Group findGroup(String world)
    {
        return plugin.findGroup(world);
    }    
    
    public WorldInventoriesAPI(final WorldInventories plugin)
    {
       this.plugin = plugin;
    }
    /*
     * Returns a list of groups currently loaded
     */
    public List<Group> getGroups()
    {
        return plugin.getAllGroups();
    }
    
    /*
     * Loads and returns inventories based on the InventoryLoadType given.
     * Type and locations are defined in InventoryStoredType.
     * Loading an Enderchest stores it in type INVENTORY
     */
    public HashMap<Integer, ItemStack[]> getPlayerInventory(String player, Group group, InventoryLoadType type)
    {
        return plugin.loadPlayerInventory(player, group, type);
    }
    
    /*
     * Loads and returns all player stats.
     */
    public PlayerStats getPlayerStats(String player, Group group)
    {
        return plugin.loadPlayerStats(player, group);
    }
}