package me.drayshak.WorldInventories.listener;

import java.util.HashMap;

import me.drayshak.WorldInventories.*;
import me.drayshak.WorldInventories.api.WorldInventoriesAPI;

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
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getEntity();

        String worldName = player.getWorld().getName();
        String playerName = player.getName();


        if (WorldInventories.exempts.contains(playerName.toLowerCase())) {
            WorldInventories.logDebug("Ignoring exempt player death: " + playerName);
            return;
        }

        Group toGroup = WorldInventoriesAPI.findGroup(worldName);

        WorldInventories.logDebug("Player " + playerName + " died in world " + worldName + ", emptying inventory for group: " + toGroup.getName());

        // Make the saved inventory blank so players can't duplicate by switching worlds and picking items back up
        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, new ItemStack[4]);
        toSave.put(InventoryStoredType.INVENTORY, new ItemStack[36]);

        plugin.savePlayerInventory(playerName, toGroup, InventoryLoadType.INVENTORY, toSave);

        if (plugin.getConfig().getBoolean("dostats")) {
            plugin.savePlayerStats(playerName, toGroup, new PlayerStats(20, 20, 0, 0, 0, 0F, null));
        }
    }
}
