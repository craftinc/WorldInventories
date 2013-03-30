package me.drayshak.WorldInventories.api;

import java.util.List;
import me.drayshak.WorldInventories.Group;
import me.drayshak.WorldInventories.PlayerStats;
import me.drayshak.WorldInventories.WorldInventories;
import static me.drayshak.WorldInventories.WorldInventories.groups;
import me.drayshak.WorldInventories.helper.InventoryHelper;
import me.drayshak.WorldInventories.helper.InventoryTypeHelper;

public class API {
    private final WorldInventories plugin;
    
    public API(final WorldInventories plugin)
    {
       this.plugin = plugin;
    }    
    
    public List<Group> getGroups()
    {
        return groups;
    }
    
    public InventoryHelper getPlayerInventory(String player, Group group, InventoryTypeHelper type)
    {
        return plugin.loadPlayerInventory(player, group, type);
    }
    
    public PlayerStats getPlayerStats(String player, Group group)
    {
        return plugin.loadPlayerStats(player, group);
    }
}
