package me.drayshak.WorldInventories.listener;

import java.util.HashMap;
import me.drayshak.WorldInventories.Group;
import me.drayshak.WorldInventories.InventoryStoredType;
import me.drayshak.WorldInventories.InventoryLoadType;
import me.drayshak.WorldInventories.PlayerStats;
import me.drayshak.WorldInventories.WorldInventories;
import me.drayshak.WorldInventories.api.WorldInventoriesAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener
{
    private final WorldInventories plugin;
    
    public EntityListener(final WorldInventories plugin)
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
            
            if(WorldInventories.exempts.contains(player.getName().toLowerCase()))
            {
                WorldInventories.logDebug("Ignoring exempt player death: " + player.getName());
                return;
            }
            
            Group togroup = WorldInventoriesAPI.findGroup(world);       

            WorldInventories.logDebug("Player " + player.getName() + " died in world " + world + ", emptying inventory for group: " + togroup.getName());

            // Make the saved inventory blank so players can't duplicate by switching worlds and picking items back up
            HashMap<Integer, ItemStack[]> tosave = new HashMap();
            tosave.put(InventoryStoredType.ARMOUR, new ItemStack[4]);
            tosave.put(InventoryStoredType.INVENTORY, new ItemStack[36]);
            
            plugin.savePlayerInventory(player.getName(), togroup, InventoryLoadType.INVENTORY, tosave);
            
            if(plugin.getConfig().getBoolean("dostats"))
            {
                plugin.savePlayerStats(player.getName(), togroup, new PlayerStats(20, 20, 0, 0, 0, 0F, null));
            }
        }
    }
}
