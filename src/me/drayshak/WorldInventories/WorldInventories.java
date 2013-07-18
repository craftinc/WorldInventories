package me.drayshak.WorldInventories;

import me.drayshak.WorldInventories.listener.*;
import me.drayshak.WorldInventories.api.WorldInventoriesAPI;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import org.mcstats.Metrics;

public class WorldInventories extends JavaPlugin
{
    protected ArrayList<Group> groups = null;
    protected List<String> exempts = null;
    protected Timer saveTimer = new Timer();

    public static final String statsFileVersion = "v5";
    public static final String inventoryFileVersion = "v5";

    protected Language locale;
    protected CommandHandler commandHandler;


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
        this.getConfig().set("exempt", this.exempts);
        this.saveConfig();
    }


    public boolean isPlayerOnExemptList(String playerName)
    {
        return this.exempts.contains(playerName.toLowerCase());
    }


    public void removePlayerFromExemptList(String playerName)
    {
        this.exempts.remove(playerName.toLowerCase());
        this.getConfig().set("exempt", this.exempts);
        this.saveConfig();
    }


    public void setPlayerInventory(Player player, HashMap<Integer, ItemStack[]> playerInventory)
    {
        if (playerInventory != null)
        {
            player.getInventory().setContents(playerInventory.get(InventoryStoredType.INVENTORY));
            player.getInventory().setArmorContents(playerInventory.get(InventoryStoredType.ARMOUR));
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
        
	    for (PotionEffect effect : player.getActivePotionEffects())
        {
            player.removePotionEffect(effect.getType());
        }
        
        Collection<PotionEffect> potioneffects = playerstats.getPotionEffects();

        if(potioneffects != null)
        {
            player.addPotionEffects(playerstats.getPotionEffects());
        }
    }

    public void savePlayers(boolean outputtoconsole)
    {
        if(outputtoconsole) {
            logStandard("Saving player information...");
        }

        for (Player player : this.getServer().getOnlinePlayers()) {
            String world = player.getLocation().getWorld().getName();
            Group tGroup = this.findGroup(world);
            
            HashMap<Integer, ItemStack[]> tosave = new HashMap<Integer, ItemStack[]>();
            // Don't save if we don't care where we are (default group)
            if (!"default".equals(tGroup.getName()))
            {
                tosave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
                tosave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());
                
                savePlayerInventory(player.getName(), this.findGroup(world), InventoryLoadType.INVENTORY, tosave);
                
                if (getConfig().getBoolean("dostats")) {
                    savePlayerStats(player, this.findGroup(world));
                }
            }
        }

        if (outputtoconsole) {
            logStandard("Done.");
        }
    }

    public void savePlayerInventory(String player, Group group, InventoryLoadType type, HashMap<Integer, ItemStack[]> inventory)
    {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        String path = File.separator + group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;

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
            logError("Failed to save " + sType + " for player: " + player + ": " + e.getMessage());
        }    
        
        logDebug("Saved " + sType + " for player: " + player + " " + path);
    }
    
    public HashMap<Integer, ItemStack[]> loadPlayerInventory(String player, Group group, InventoryLoadType type)
    {
        String path = File.separator + group.getName();

        path = this.getDataFolder().getAbsolutePath() + path;

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
            logError("Failed to load " + sType + " for player: " + player + ": " + e.getMessage());
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
                logDebug("Player " + player + " will get new armour on next save (clearing now).");              

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
                logDebug("Player " + player + " will get new items on next save (clearing now).");           

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
        
        logDebug("Loaded " + sType + " for player: " + player + " " + path);

        return ret;
    }

    public PlayerStats loadPlayerStats(String player, Group group)
    {
        String path = File.separator + group.getName();
        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);

        if (!file.exists()) {
            file.mkdir();
        }         

        path += File.separator + player + ".stats." + statsFileVersion + ".yml";      
        
        file = new File(path);
        FileConfiguration pc;

        try {
            file.createNewFile();
            pc = YamlConfiguration.loadConfiguration(new File(path));        
        }
        catch (Exception e) {
            logError("Failed to load stats for player: " + player + ": " + e.getMessage());
            return null;
        }
        
        int health;
        int foodLevel;
        double exhaustion;
        double saturation;
        int level;
        double exp;
        List<PotionEffect> potionEffects;

        health = pc.getInt("health", -1);
        foodLevel = pc.getInt("foodlevel", 20);
        exhaustion = pc.getDouble("exhaustion", 0);
        saturation = pc.getDouble("saturation", 0);
        level = pc.getInt("level", 0);
        exp = pc.getDouble("exp", 0);
        potionEffects = (List<PotionEffect>) pc.getList("potioneffects", null);
        
        PlayerStats playerstats = new PlayerStats(20, 20, 0, 0, 0, 0F, null);
        
        if(health == -1) {
            logDebug("Player " + player + " will get a new stats file on next save (clearing now).");           
        }
        else {
            playerstats = new PlayerStats(health, foodLevel, (float)exhaustion, (float)saturation, level, (float)exp, potionEffects);
        }
        
        this.setPlayerStats(this.getServer().getPlayer(player), playerstats);
        
        logDebug("Loaded stats for player: " + player + " " + path);

        return playerstats;
    }

    public void savePlayerStats(Player player, Group group)
    {
        savePlayerStats(player.getName(), group, new PlayerStats(player.getHealth(), player.getFoodLevel(), player.getExhaustion(), player.getSaturation(), player.getLevel(), player.getExp(), player.getActivePotionEffects()));
    }
    
    public void savePlayerStats(String player, Group group, PlayerStats playerstats)
    {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        String path = File.separator + group.getName();
        path = this.getDataFolder().getAbsolutePath() + path;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }      

        path += File.separator + player + ".stats." + statsFileVersion + ".yml";

        file = new File(path);

        try {
            file.createNewFile();
            FileConfiguration pc = YamlConfiguration.loadConfiguration(file);

            pc.set("health", playerstats.getHealth());
            pc.set("foodlevel", playerstats.getFoodLevel());
            pc.set("exhaustion", playerstats.getExhaustion());
            pc.set("saturation", playerstats.getSaturation());
            pc.set("level", playerstats.getLevel());
            pc.set("exp", playerstats.getExp());
            pc.set("potioneffects", playerstats.getPotionEffects());
            
            pc.save(file);
        }    
        catch (Exception e) {
            logError("Failed to save stats for player: " + player + ": " + e.getMessage());
        }
        
        logDebug("Saved stats for player: " + player + " " + path);
    }
    
    public boolean importVanilla()
    {
        int imported = 0;
        int failed = 0;
        
        logStandard("Starting vanilla players import...");
        
        Group group = this.findGroup(getConfig().getString("vanillatogroup"));

        if (group == null) {
            logStandard("Warning: importing from vanilla in to the default group (does the group specified exist?)");
        }
        
        OfflinePlayer[] offlinePlayers = getServer().getOfflinePlayers();

        if (offlinePlayers.length <= 0) {
            logStandard("Found no offline players to import!");
            return false;
        }
        
        for (OfflinePlayer offlineplayer : offlinePlayers) {
            Player player = null;

            try {
                player = (Player) offlineplayer;
            }
            catch(Exception e) {
                logError("  (Warning) Couldn't convert a player: " + e.getMessage());
            }

            if (player == null) {

                String playerName = "unknown";

                if (offlineplayer != null) {
                    playerName = offlineplayer.getName();
                }

                logStandard("Failed to import '" + playerName + "', couldn't create EntityPlayer.");
                failed++;

                continue;
            }

            savePlayerStats(player, group);

            HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
            toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
            toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());

            savePlayerInventory(player.getName(), group, InventoryLoadType.INVENTORY, toSave);

            toSave.put(InventoryStoredType.ARMOUR, null);
            toSave.put(InventoryStoredType.INVENTORY, player.getEnderChest().getContents());

            this.savePlayerInventory(player.getName(), group, InventoryLoadType.ENDERCHEST, toSave);

            imported++;
        }
        
        logStandard("Imported " + Integer.toString(imported) + "/" + Integer.toString(offlinePlayers.length) + " (" + Integer.toString(failed) + " failures).");

        return (failed < offlinePlayers.length);
    }
    
    public static void logStandard(String line)
    {
        Logger.getLogger("Minecraft").log(Level.INFO, "[WorldInventories] {0}", line);
    }

    public static void logError(String line)
    {
        Logger.getLogger("Minecraft").log(Level.SEVERE, "[WorldInventories] {0}", line);
    }

    public static void logDebug(String line)
    {
        Logger.getLogger("Minecraft").log(Level.FINE, "[WorldInventories] {0}", line);
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

    public boolean loadConfiguration()
    {
        groups = new ArrayList<Group>();

        String defaultmode = getConfig().getString("gamemodes.default", "SURVIVAL");
        
        Set<String> nodes;
        try
        {
            nodes = getConfig().getConfigurationSection("groups").getKeys(false);
        }
        catch(NullPointerException e)
        {
            nodes = new HashSet<String>();
            logError("Warning: No groups found. Everything will be in the 'default' group.");
        }
        
        List<String> empty = Collections.emptyList();
        Group defaultgroup = new Group("default", empty, GameMode.valueOf(getConfig().getString("gamemodes.default", defaultmode)));
        groups.add(defaultgroup);
        
        for (String sgroup : nodes)
        {
            List<String> worldnames = getConfig().getStringList("groups." + sgroup);
            if (worldnames != null)
            {
                Group group = new Group(sgroup, worldnames, GameMode.valueOf(getConfig().getString("gamemodes." + sgroup, defaultmode)));
                groups.add(group);
                for (String world : worldnames)
                {
                    logDebug("Adding " + sgroup + ":" + world + ":" + group.getGameMode().toString());
                }
            }
        }

        try
        {
            exempts = getConfig().getStringList("exempt");
        }
        catch(NullPointerException e)
        {
            exempts = new ArrayList<String>();
        }
        
        for (String player : exempts)
        {
            logDebug("Adding " + player + " to exemption list");
        }
        
        logStandard("Loaded " + Integer.toString(exempts.size()) + " player exemptions.");
        
        return true;
    }

    public boolean loadLanguage()
    {
        String sLanguage = this.getConfig().getString("language");
        locale = new Language(this);

        boolean bLanguage = locale.loadLanguages(sLanguage);

        if(bLanguage)
        {
            logStandard("Loaded language " + sLanguage + " successfully");
        }
        else
        {
            logStandard("Problems encountered whilst loading language " + sLanguage + ", used defaults.");
        }
        
        return bLanguage;
    }
    
    @Override
    public void onEnable()
    {
        logStandard("Initialising...");

        this.commandHandler = new CommandHandler(this);

        boolean bInitialised = true;

//        bukkitServer = this.getServer();
//        pluginManager = bukkitServer.getPluginManager();

        logStandard("Loading configuration...");
//        boolean loaded = this.loadConfig(true);
        this.saveDefaultConfig();

//        if (!loaded) {
//            logError("Failed to load configuration! See the message above for details.");
//            pluginManager.disablePlugin(this);
//            return;
//        }

        boolean bConfiguration = this.loadConfiguration();

        if (!bConfiguration)
        {
            logError("Failed to load configuration.");
            bInitialised = false;
        }
        else
        {
            logStandard("Loaded configuration successfully");
        }
        
        if (bInitialised)
        {
            this.loadLanguage();
            
            if(getConfig().getBoolean("dovanillaimport"))
            {
                boolean bSuccess = this.importVanilla();
                
                this.getConfig().set("dovanillaimport", false);
                this.saveConfig();
                
                if(bSuccess)
                {
                    logStandard("Vanilla saves import was a success!");
                }                
            }
            
            getServer().getPluginManager().registerEvents(new EntityListener(this), this);
            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
            getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            } catch (IOException e) {
                logDebug("Failed to submit Metrics statistics.");
            }            
            
            logStandard("Initialised successfully!");

            if (getConfig().getInt("saveinterval") >= 30)
            {
                saveTimer.scheduleAtFixedRate(new SaveTask(this), getConfig().getInt("saveinterval") * 1000, getConfig().getInt("saveinterval") * 1000);
            }
        }
        else
        {
            logError("Failed to initialise.");
        }
    }

    @Override
    public void onDisable()
    {
        savePlayers(true);
        logStandard("Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return this.commandHandler.onCommand(sender, cmd, args);
    }
    
    /*
     * Checks if the given message key is hidden or not, send if appropriate
     */
    public void sendMessage(String key, Player player, String message)
    {
        if(!this.getConfig().getBoolean("message-hidden." + key, false) && this.getConfig().getBoolean("donotifications" + key, true))
        {
            player.sendMessage(message);
        }
    }
}
