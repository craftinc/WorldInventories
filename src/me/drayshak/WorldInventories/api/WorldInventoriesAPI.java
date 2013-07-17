package me.drayshak.WorldInventories.api;

import me.drayshak.WorldInventories.InventoryLoadType;
import java.util.HashMap;
import java.util.List;
import me.drayshak.WorldInventories.Group;
import me.drayshak.WorldInventories.PlayerStats;
import me.drayshak.WorldInventories.WorldInventories;
import static me.drayshak.WorldInventories.WorldInventories.groups;
import org.bukkit.inventory.ItemStack;

public class WorldInventoriesAPI
{
    private final WorldInventories plugin;
    
    /*
     * Returns the first group associated with a world, otherwise the default
     */
    public static Group findGroup(String world)
    {
        return WorldInventories.findGroup(world);
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
        return groups;
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
