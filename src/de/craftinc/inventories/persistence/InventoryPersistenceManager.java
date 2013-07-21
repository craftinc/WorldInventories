package de.craftinc.inventories.persistence;


import de.craftinc.inventories.Group;
import de.craftinc.inventories.WorldInventories;
import de.craftinc.inventories.utils.ConfigurationKeys;
import de.craftinc.inventories.utils.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class InventoryPersistenceManager
{
    protected static final String inventoryFileVersion = "v5";

    public static void savePlayers(boolean printOnConsole)
    {
        WorldInventories plugin = WorldInventories.getSharedInstance();

        if(printOnConsole) {
            Logger.logStandard("Saving player information...");
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            String world = player.getLocation().getWorld().getName();
            Group tGroup = plugin.findGroup(world);

            HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();

            // Don't save if we don't care where we are (default group)
            if (!"default".equals(tGroup.getName())) {
                toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
                toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());

                savePlayerInventory(player.getName(), plugin.findGroup(world), InventoryLoadType.INVENTORY, toSave);

                if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
                    plugin.savePlayerStats(player, plugin.findGroup(world));
                }
            }
        }

        if (printOnConsole) {
            Logger.logStandard("Done.");
        }
    }


    public static void savePlayerInventory(String player, Group group, InventoryLoadType type, HashMap<Integer, ItemStack[]> inventory)
    {
        WorldInventories plugin = WorldInventories.getSharedInstance();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        String path = File.separator + group.getName();

        path = plugin.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        String sType = "unknown";
        if(type == InventoryLoadType.INVENTORY)
        {
            sType = "inventory";
        }
        else if(type == InventoryLoadType.ENDERCHEST)
        {
            sType = "enderchest";
        }

        path += File.separator + player + "." + sType + "." + inventoryFileVersion + ".yml";

        file = new File(path);

        try
        {
            file.createNewFile();
            FileConfiguration pc = YamlConfiguration.loadConfiguration(new File(path));

            if(type == InventoryLoadType.INVENTORY)
            {
                pc.set("armour", inventory.get(InventoryStoredType.ARMOUR));
                pc.set("inventory", inventory.get(InventoryStoredType.INVENTORY));
            }
            else if(type == InventoryLoadType.ENDERCHEST)
            {
                pc.set("enderchest", inventory.get(InventoryStoredType.INVENTORY));
            }

            pc.save(file);
        }
        catch (Exception e)
        {
            Logger.logError("Failed to save " + sType + " for player: " + player + ": " + e.getMessage());
        }

        Logger.logDebug("Saved " + sType + " for player: " + player + " " + path);
    }


    public static HashMap<Integer, ItemStack[]> loadPlayerInventory(String player, Group group, InventoryLoadType type)
    {
        WorldInventories plugin = WorldInventories.getSharedInstance();

        String path = File.separator + group.getName();
        path = plugin.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);

        if (!file.exists())
        {
            file.mkdir();
        }

        String sType = "unknown";
        if(type == InventoryLoadType.INVENTORY)
        {
            sType = "inventory";
        }
        else if(type == InventoryLoadType.ENDERCHEST)
        {
            sType = "enderchest";
        }

        path += File.separator + player + "." + sType + "." + inventoryFileVersion + ".yml";

        file = new File(path);
        FileConfiguration pc = null;

        try {
            file.createNewFile();
            pc = YamlConfiguration.loadConfiguration(new File(path));
        }
        catch (Exception e) {
            Logger.logError("Failed to load " + sType + " for player: " + player + ": " + e.getMessage());
        }

        List armour = null;
        List inventory = null;

        ItemStack[] iArmour = new ItemStack[4];
        ItemStack[] iInventory = null;

        if (type == InventoryLoadType.INVENTORY) {

            if (pc != null) {
                armour = pc.getList("armour", null);
                inventory = pc.getList("inventory", null);
            }


            if (armour == null) {
                Logger.logDebug("Player " + player + " will get new armour on next save (clearing now).");

                for (int i = 0; i < 4; i++) {
                    iArmour[i] = new ItemStack(Material.AIR);
                }
            }
            else {

                for(int i = 0; i < 4; i++) {
                    iArmour[i] = (ItemStack)armour.get(i);
                }
            }

            iInventory = new ItemStack[36];

            if (inventory == null) {
                Logger.logDebug("Player " + player + " will get new items on next save (clearing now).");

                for (int i = 0; i < 36; i++) {
                    iInventory[i] = new ItemStack(Material.AIR);
                }
            }
            else {

                for (int i = 0; i < 36; i++) {
                    iInventory[i] = (ItemStack)inventory.get(i);
                }
            }
        }
        else if (type == InventoryLoadType.ENDERCHEST) {

            if (pc != null) {
                inventory = pc.getList("enderchest", null);
            }

            iInventory = new ItemStack[27];

            if (inventory == null) {

                for (int i = 0; i < 27; i++) {
                    iInventory[i] = new ItemStack(Material.AIR);
                }
            }
            else {

                for(int i = 0; i < 27; i++) {
                    iInventory[i] = (ItemStack)inventory.get(i);
                }
            }
        }

        HashMap<Integer, ItemStack[]> ret = new HashMap<Integer, ItemStack[]>();
        ret.put(InventoryStoredType.ARMOUR, iArmour);
        ret.put(InventoryStoredType.INVENTORY, iInventory);

        Logger.logDebug("Loaded " + sType + " for player: " + player + " " + path);

        return ret;
    }


    public static void setPlayerInventory(Player player, HashMap<Integer, ItemStack[]> playerInventory)
    {
        if (playerInventory != null) {
            player.getInventory().setContents(playerInventory.get(InventoryStoredType.INVENTORY));
            player.getInventory().setArmorContents(playerInventory.get(InventoryStoredType.ARMOUR));
        }
    }
}
