package de.craftinc.inventories;

import de.craftinc.inventories.importers.VanillaImporter;
import de.craftinc.inventories.listener.*;

import java.io.IOException;
import java.util.*;

import de.craftinc.inventories.persistence.InventoryPersistenceManager;
import de.craftinc.inventories.persistence.SaveTask;
import de.craftinc.inventories.utils.ConfigurationKeys;
import de.craftinc.inventories.utils.Language;
import de.craftinc.inventories.utils.Logger;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.mcstats.Metrics;


public class WorldInventories extends JavaPlugin
{
    protected static WorldInventories sharedInstance;

    protected ArrayList<Group> groups = null;
    protected List<String> exempts = null;

    protected Timer saveTimer = new Timer();
    protected Language locale;


    public WorldInventories()
    {
        sharedInstance = this;
    }


    public static WorldInventories getSharedInstance()
    {
        return sharedInstance;
    }


    public Group findGroup(String world)
    {
        for (Group tGroup : groups) {
            int index = tGroup.getWorlds().indexOf(world);

            if(index != -1) {
                return tGroup;
            }
        }

        return groups.get(0);
    }


    public ArrayList<Group> getAllGroups()
    {
        return groups;
    }


    public Language getLocale()
    {
        return locale;
    }


    public void addPlayerToExemptList(String playerName)
    {
        this.exempts.add(playerName.toLowerCase());
        this.getConfig().set(ConfigurationKeys.exemptPlayersGroupKey, this.exempts);
        this.saveConfig();
    }


    public boolean isPlayerOnExemptList(String playerName)
    {
        return this.exempts.contains(playerName.toLowerCase());
    }


    public void removePlayerFromExemptList(String playerName)
    {
        this.exempts.remove(playerName.toLowerCase());
        this.getConfig().set(ConfigurationKeys.exemptPlayersGroupKey, this.exempts);
        this.saveConfig();
    }


    //    private boolean loadConfig(boolean createDefaults)
//    {
//        try
//        {
//            YamlConfiguration config = new YamlConfiguration();
//            config.load(new File(this.getDataFolder().getPath(), "config.yml"));
//        }
//        catch(FileNotFoundException e)
//        {
//            if(createDefaults)
//            {
//                saveDefaultConfig();
//                return true;
//            }
//            else return false;
//        }
//        catch(Exception e)
//        {
//            logError("Failed to load configuration: " + e.getMessage());
//
//            return false;
//        }
//
//        getConfig().options().copyDefaults(true);
//        saveConfig();
//
//        return true;
//    }

    protected boolean loadConfiguration()
    {
        groups = new ArrayList<Group>();

        String defaultMode = getConfig().getString(ConfigurationKeys.defaultGameModeKey, "SURVIVAL");
        
        Set<String> nodes;

        try {
            nodes = getConfig().getConfigurationSection(ConfigurationKeys.worldGroupsKey).getKeys(false);
        }
        catch(NullPointerException e) {
            nodes = new HashSet<String>();
            Logger.logError("Warning: No groups found. Everything will be in the 'default' group.");
        }
        
        List<String> empty = Collections.emptyList();
        Group defaultGroup = new Group("default", empty, GameMode.valueOf(defaultMode));
        groups.add(defaultGroup);
        
        for (String groupName : nodes) {
            List<String> worldNames = getConfig().getStringList(ConfigurationKeys.worldGroupsKey + "." + groupName);

            if (worldNames != null) {
                GameMode groupGameMode = GameMode.valueOf(getConfig().getString(ConfigurationKeys.gameModesGroupKey + groupName, defaultMode));
                Group group = new Group(groupName,
                                        worldNames,
                                        groupGameMode);

                groups.add(group);

                for (String world : worldNames) {
                    Logger.logDebug("Adding " + groupName + ":" + world + ":" + group.getGameMode().toString());
                }
            }
        }

        try {
            exempts = getConfig().getStringList(ConfigurationKeys.exemptPlayersGroupKey);
        }
        catch(NullPointerException e) {
            exempts = new ArrayList<String>();
        }
        
        for (String player : exempts) {
            Logger.logDebug("Adding " + player + " to exemption list");
        }

        Logger.logStandard("Loaded " + Integer.toString(exempts.size()) + " player exemptions.");
        
        return true;
    }

    protected boolean loadLanguage()
    {
        String languageName = this.getConfig().getString(ConfigurationKeys.languageKey);
        locale = new Language();

        boolean success = locale.loadLanguage(languageName);

        if (success) {
            Logger.logStandard("Loaded language " + languageName + " successfully");
        }
        else {
            Logger.logStandard("Problems encountered whilst loading language " + languageName + ", used defaults.");
        }
        
        return success;
    }
    
    @Override
    public void onEnable()
    {
        Logger.logStandard("Initialising...");

        boolean bInitialised = true;

        Logger.logStandard("Loading configuration...");
//        boolean loaded = this.loadConfig(true);
        this.saveDefaultConfig();

//        if (!loaded) {
//            logError("Failed to load configuration! See the message above for details.");
//            pluginManager.disablePlugin(this);
//            return;
//        }

        boolean bConfiguration = this.loadConfiguration();

        if (!bConfiguration) {
            Logger.logError("Failed to load configuration.");
            bInitialised = false;
        }
        else {
            Logger.logStandard("Loaded configuration successfully");
        }
        
        if (bInitialised) {
            this.loadLanguage();
            
            if (getConfig().getBoolean(ConfigurationKeys.doVanillaImportKey)) {
                boolean importSuccess = VanillaImporter.performImport();

                this.getConfig().set(ConfigurationKeys.doVanillaImportKey, false);
                this.saveConfig();
                
                if (importSuccess) {
                    Logger.logStandard("Vanilla saves importers was a success!");
                }                
            }

            // setup listeners
            getServer().getPluginManager().registerEvents(new EntityListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerListener(), this);
            getServer().getPluginManager().registerEvents(new InventoryListener(), this);

            // setup metrics
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            }
            catch (IOException e) {
                Logger.logDebug("Failed to submit Metrics statistics.");
            }            

            // start timed saving
            int interval = getConfig().getInt(ConfigurationKeys.saveTimerIntervalKey);

            if (interval >= 30) {
                interval *= 1000;
                saveTimer.scheduleAtFixedRate(new SaveTask(), interval, interval);
            }

            Logger.logStandard("Initialised successfully!");
        }
        else {
            Logger.logError("Failed to initialise.");
        }
    }

    @Override
    public void onDisable()
    {
        InventoryPersistenceManager.savePlayers(true);
        Logger.logStandard("Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return CommandHandler.onCommand(sender, cmd, args);
    }
}
