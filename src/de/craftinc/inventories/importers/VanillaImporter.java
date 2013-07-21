package de.craftinc.inventories.importers;

import de.craftinc.inventories.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VanillaImporter
{
    public static boolean performImport()
    {
        int imported = 0;
        int failed = 0;

        WorldInventories plugin = WorldInventories.getSharedInstance();
        FileConfiguration config = plugin.getConfig();

        InventoriesLogger.logStandard("Starting vanilla players importers...");

        Group group = plugin.findGroup(config.getString("vanillatogroup"));

        if (group == null) {
            InventoriesLogger.logStandard("Warning: importing from vanilla in to the default group (does the group specified exist?)");
        }

        OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();

        if (offlinePlayers.length <= 0) {
            InventoriesLogger.logStandard("Found no offline players to importers!");
            return false;
        }

        for (OfflinePlayer offlineplayer : offlinePlayers) {
            Player player = null;

            try {
                player = (Player) offlineplayer;
            }
            catch(Exception e) {
                InventoriesLogger.logError("  (Warning) Couldn't convert a player: " + e.getMessage());
            }

            if (player == null) {
                String playerName = "unknown";

                if (offlineplayer != null) {
                    playerName = offlineplayer.getName();
                }

                InventoriesLogger.logStandard("Failed to import '" + playerName + "', couldn't create EntityPlayer.");
                failed++;

                continue;
            }

            plugin.savePlayerStats(player, group);

            HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
            toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
            toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());

            plugin.savePlayerInventory(player.getName(), group, InventoryLoadType.INVENTORY, toSave);

            toSave.put(InventoryStoredType.ARMOUR, null);
            toSave.put(InventoryStoredType.INVENTORY, player.getEnderChest().getContents());

            plugin.savePlayerInventory(player.getName(), group, InventoryLoadType.ENDERCHEST, toSave);

            imported++;
        }

        InventoriesLogger.logStandard("Imported " + Integer.toString(imported) + "/" + Integer.toString(offlinePlayers.length) + " (" + Integer.toString(failed) + " failures).");

        return (failed < offlinePlayers.length);
    }

}
