package me.drayshak.WorldInventories;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldInventories extends JavaPlugin
{
    public static final Logger log = Logger.getLogger("Minecraft");
    public static PluginManager pluginManager = null;
    public static Server bukkitServer = null;
    public static ArrayList<Group> groups = null;
    public static Timer saveTimer = new Timer();
    public static String fileVersion = "v3";

    public PlayerInventoryHelper getPlayerInventory(Player player)
    {
        return new PlayerInventoryHelper(player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public void setPlayerInventory(Player player, PlayerInventoryHelper playerInventory)
    {
        if (playerInventory != null)
        {
            player.getInventory().setContents(playerInventory.getItems());
            player.getInventory().setArmorContents(playerInventory.getArmour());
        }
    }

    public void setPlayerStats(Player player, PlayerStats playerstats)
    {
        // Never kill a player - must be a bug if it was 0
        player.setHealth(Math.max(playerstats.getHealth(), 1));
        player.setFoodLevel(playerstats.getFoodLevel());
        player.setExhaustion(playerstats.getExhaustion());
        player.setSaturation(playerstats.getSaturation());
        player.setLevel(playerstats.getLevel());
        player.setExp(playerstats.getExp());
    }

    public void savePlayers()
    {
        WorldInventories.logStandard("Saving player information...");

        for (Player player : WorldInventories.bukkitServer.getOnlinePlayers())
        {
            String world = player.getLocation().getWorld().getName();

            Group tGroup = WorldInventories.findFirstGroupForWorld(world);

            // Don't save if we don't care where we are (default group)
            if (tGroup != null)
            {
                savePlayerInventory(player.getName(), WorldInventories.findFirstGroupForWorld(world), getPlayerInventory(player));
                if (getConfig().getBoolean("dostats"))
                {
                    savePlayerStats(player, WorldInventories.findFirstGroupForWorld(world));
                }
            }
        }

        WorldInventories.logStandard("Done.");
    }

    public void savePlayerInventory(String player, Group group, PlayerInventoryHelper toStore)
    {
        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;

        if (!this.getDataFolder().exists())
        {
            this.getDataFolder().mkdir();
        }

        String path = File.separator;

        // Use default group
        if (group == null)
        {
            path += "default";
        }
        else
        {
            path += group.getName();
        }

        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }

        path += File.separator + player + ".inventory." + fileVersion;

        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(toStore.getSerializable());
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save inventory for player: " + player + ": " + e.getMessage());
        }
    }

    public PlayerInventoryHelper loadPlayerInventory(Player player, Group group)
    {
        InventoriesSaveable playerInventory = null;

        FileInputStream fIS = null;
        ObjectInputStream obIn = null;

        String path = File.separator;

        // Use default group
        if (group == null)
        {
            path += "default";
        }
        else
        {
            path += group.getName();
        }

        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }

        path += File.separator + player.getName() + ".inventory." + fileVersion;

        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerInventory = (InventoriesSaveable) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (FileNotFoundException e)
        {
            WorldInventories.logDebug("Player " + player.getName() + " will get a new item file on next save (clearing now).");
            player.getInventory().clear();
            ItemStack[] armour = new ItemStack[4];
            for (int i = 0; i < 4; i++)
            {
                armour[i] = new ItemStack(Material.AIR);
            }

            player.getInventory().setArmorContents(armour);
            
            return new PlayerInventoryHelper(player.getInventory().getContents(), player.getInventory().getArmorContents());
        }
        catch (Exception e)
        {
            WorldInventories.logDebug("Failed to load inventory for player: " + player.getName() + ", giving empty inventory: " + e.getMessage());
        }

        return new PlayerInventoryHelper(playerInventory);
    }

    public PlayerStats loadPlayerStats(Player player, Group group)
    {
        PlayerStats playerstats = null;

        FileInputStream fIS = null;
        ObjectInputStream obIn = null;

        String path = File.separator;

        // Use default group
        if (group == null)
        {
            path += "default";
        }
        else
        {
            path += group.getName();
        }

        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }

        path += File.separator + player.getName() + ".stats";

        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerstats = (PlayerStats) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (FileNotFoundException e)
        {
            WorldInventories.logDebug("Player " + player.getName() + " will get a new stats file on next save (clearing now).");
            playerstats = new PlayerStats(20, 20, 0, 0, 0, 0F);
            this.setPlayerStats(player, playerstats);
        }
        catch (Exception e)
        {
            WorldInventories.logDebug("Failed to load stats for player: " + player.getName() + ", giving defaults: " + e.getMessage());
        }

        return playerstats;
    }

    public void savePlayerStats(Player player, Group group, PlayerStats playerstats)
    {
        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;

        if (!this.getDataFolder().exists())
        {
            this.getDataFolder().mkdir();
        }

        String path = File.separator;

        // Use default group
        if (group == null)
        {
            path += "default";
        }
        else
        {
            path += group.getName();
        }

        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }

        path += File.separator + player.getName() + ".stats";

        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(playerstats);
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save stats for player: " + player + ": " + e.getMessage());
        }
    }

    public void savePlayerStats(Player player, Group group)
    {
        PlayerStats playerstats = new PlayerStats(player.getHealth(), player.getFoodLevel(), player.getExhaustion(), player.getSaturation(), player.getLevel(), player.getExp());

        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;

        if (!this.getDataFolder().exists())
        {
            this.getDataFolder().mkdir();
        }

        String path = File.separator;

        // Use default group
        if (group == null)
        {
            path += "default";
        }
        else
        {
            path += group.getName();
        }

        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }

        path += File.separator + player.getName() + ".stats";

        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(playerstats);
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save stats for player: " + player + ": " + e.getMessage());
        }
    }

    public boolean importMultiInvData()
    {
        Plugin pMultiInv = WorldInventories.pluginManager.getPlugin("MultiInv");
        if (pMultiInv == null)
        {
            WorldInventories.logError("Failed to import MultiInv shares - Bukkit couldn't find MultiInv. Make sure it is installed and enabled whilst doing the import, then when successful remove it.");
        }

        File MISharesLocation = new File(pMultiInv.getDataFolder(), "Worlds" + File.separator);
        if (!MISharesLocation.exists())
        {
            WorldInventories.logError("Failed to import MultiInv shares - " + MISharesLocation.toString() + " doesn't seem to exist.");
            return false;
        }

        File fMIConfig = new File(WorldInventories.pluginManager.getPlugin("MultiInv").getDataFolder(), "shares.yml");
        if (!fMIConfig.exists())
        {
            WorldInventories.logError("Failed to import MultiInv shares - shares file doesn't seem to exist.");
            return false;
        }

        FileConfiguration MIConfig = YamlConfiguration.loadConfiguration(fMIConfig);

        for (String sGroup : MIConfig.getConfigurationSection("").getKeys(false))
        {
            List<String> sWorlds = MIConfig.getStringList(sGroup);
            if (sWorlds != null)
            {
                Group group = new Group(sGroup, sWorlds, false);
                WorldInventories.groups.add(group);
                getConfig().set("groups." + sGroup, sWorlds);
            }
            else
            {
                WorldInventories.logDebug("Skipping import of group because it is empty: " + sGroup);
            }
        }

        this.saveConfig();

        ArrayList<String> sMIShares = new ArrayList(Arrays.asList(MISharesLocation.list()));

        if (sMIShares.size() <= 0)
        {
            WorldInventories.logError("Failed to import MultiInv shares - there weren't any shares found!");
            return false;
        }
        else
        {
            for (int i = 0; i < sMIShares.size(); i++)
            {
                String sWorld = sMIShares.get(i);

                File fWorld = new File(MISharesLocation, sWorld);
                if (fWorld.isDirectory() && fWorld.exists())
                {
                    Group group = findFirstGroupForWorld(sWorld);
                    if (group == null)
                    {
                        group = new Group(sWorld, Arrays.asList(sWorld), false);
                        WorldInventories.groups.add(group);
                        getConfig().set("groups." + sWorld, Arrays.asList(sWorld));
                        this.saveConfig();

                        WorldInventories.logError("A world was found that doesn't belong to any groups! It was saved as its own group. To put it in a group, edit the WorldInventories config.yml: " + sWorld);
                    }

                    //List<String> sPlayer = Arrays.asList(fWorld.list());

                    for (File shareFile : fWorld.listFiles())
                    {
                        if (shareFile.getAbsolutePath().endsWith(".yml"))
                        {
                            String sFilename = shareFile.getName();
                            String playerName = sFilename.substring(0, sFilename.length() - 4);

                            Configuration playerConfig = YamlConfiguration.loadConfiguration(shareFile);

                            String sPlayerInventory = playerConfig.getString("survival");
                            PlayerInventoryHelper playerInventory = MultiInvImportHelper.playerInventoryFromMIString(sPlayerInventory);
                            if (playerInventory == null)
                            {
                                sPlayerInventory = playerConfig.getString("creative");
                            }
                            if (playerInventory == null)
                            {
                                logError("Failed to load MultiInv data - found player file but failed to convert it: " + playerName);
                            }
                            else
                            {
                                this.savePlayerInventory(playerName, group, playerInventory);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    // NetBeans complains about these log lines but message formatting breaks for me
    public static void logStandard(String line)
    {
        log.log(Level.INFO, "[WorldInventories] " + line);
    }

    public static void logError(String line)
    {
        log.log(Level.SEVERE, "[WorldInventories] " + line);
    }

    public static void logDebug(String line)
    {
        log.log(Level.FINE, "[WorldInventories] " + line);
    }

    private void loadConfigAndCreateDefaultsIfNecessary()
    {
        saveDefaultConfig();

        //getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public List<Group> getGroups()
    {
        return groups;
    }

    private boolean loadConfiguration()
    {
        WorldInventories.groups = new ArrayList<Group>();

        Set<String> nodes = getConfig().getConfigurationSection("groups").getKeys(false);
        for (String group : nodes)
        {
            List<String> worldnames = getConfig().getStringList("groups." + group);
            if (worldnames != null)
            {
                WorldInventories.groups.add(new Group(group, worldnames, getConfig().getBoolean("groups." + group + ".dokeepinv", false)));
                for (String world : worldnames)
                {
                    WorldInventories.logDebug("Adding " + group + ":" + world);
                }
            }
        }

        return true;
    }

    public static Group findFirstGroupForWorld(String world)
    {
        for (Group tGroup : WorldInventories.groups)
        {
            for (String tWorld : tGroup.getWorlds())
            {
                if (tWorld.equals(world))
                {
                    return tGroup;
                }
            }
        }

        return null;
    }

    @Override
    public void onEnable()
    {
        WorldInventories.logStandard("Initialising...");

        boolean bInitialised = true;

        WorldInventories.bukkitServer = this.getServer();
        WorldInventories.pluginManager = WorldInventories.bukkitServer.getPluginManager();

        WorldInventories.logStandard("Loading configuration...");
        this.loadConfigAndCreateDefaultsIfNecessary();

        boolean bConfiguration = this.loadConfiguration();

        if (!bConfiguration)
        {
            WorldInventories.logError("Failed to load configuration.");
            bInitialised = false;
        }
        else
        {
            WorldInventories.logStandard("Loaded configuration successfully");
        }

        if (bInitialised)
        {
            if (getConfig().getBoolean("domiimport"))
            {
                boolean bSuccess = this.importMultiInvData();

                this.getConfig().set("domiimport", false);
                this.saveConfig();

                if (bSuccess)
                {
                    WorldInventories.logStandard("MultiInv data import was a success!");
                }
            }

            getServer().getPluginManager().registerEvents(new EntityListener(this), this);
            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

            WorldInventories.logStandard("Initialised successfully!");

            if (getConfig().getInt("saveinterval") >= 30)
            {
                saveTimer.scheduleAtFixedRate(new SaveTask(this), getConfig().getInt("saveinterval") * 1000, getConfig().getInt("saveinterval") * 1000);
            }

        }
        else
        {
            WorldInventories.logError("Failed to initialise.");
        }

    }

    @Override
    public void onDisable()
    {
        savePlayers();

        WorldInventories.logStandard("Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        String command = cmd.getName();

        if (command.equalsIgnoreCase("wireload"))
        {
            if (args.length == 0)
            {
                if (sender.hasPermission("worldinventories.reload"))
                {
                    WorldInventories.logStandard("Reloading configuration...");
                    reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "Reloaded WorldInventories configuration successfully");
                }
            }

            return true;
        }

        return false;
    }
}
