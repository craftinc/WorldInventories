package de.craftinc.inventories.listener;

import java.util.HashMap;

import de.craftinc.inventories.*;

import de.craftinc.inventories.persistence.*;
import de.craftinc.inventories.utils.ConfigurationKeys;
import de.craftinc.inventories.utils.Language;
import de.craftinc.inventories.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener
{
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getEntity();

        String worldName = player.getWorld().getName();
        String playerName = player.getName();

        Plugin plugin = Plugin.getSharedInstance();

        if (plugin.isPlayerOnExemptList(playerName)) {
            Logger.logDebug("Ignoring exempt player death: " + playerName);
            return;
        }

        Group toGroup = plugin.findGroup(worldName);

        Logger.logDebug("Player " + playerName + " died in world " + worldName + ", emptying inventory for group: " + toGroup.getName());

        // Make the saved inventory blank so players can't duplicate by switching worlds and picking items back up
        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, new ItemStack[4]);
        toSave.put(InventoryStoredType.INVENTORY, new ItemStack[36]);

        InventoryPersistenceManager.savePlayerInventory(playerName, toGroup, InventoryLoadType.INVENTORY, toSave);

        if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
            StatsPersistenceManager.savePlayerStats(playerName, toGroup, new PlayerStats(20, 20, 0, 0, 0, 0F, null));
        }

        Logger.sendMessage(ConfigurationKeys.hideDiedMessageKey,
                           player,
                           ChatColor.GREEN + plugin.getLocale().get(Language.diedMessageKey) + toGroup.getName());
    }
}
