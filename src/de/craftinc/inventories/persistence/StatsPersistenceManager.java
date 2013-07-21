package de.craftinc.inventories.persistence;

import de.craftinc.inventories.Group;
import de.craftinc.inventories.PlayerStats;
import de.craftinc.inventories.WorldInventories;
import de.craftinc.inventories.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.Collection;
import java.util.List;


public class StatsPersistenceManager
{
    public static final String statsFileVersion = "v5";

    public static void setPlayerStats(Player player, PlayerStats playerstats)
    {
        // Never kill a player - must be a bug if it was 0
        double health = Math.max(playerstats.getHealth(), 1.0);
        player.setHealth(health);

        player.setFoodLevel(playerstats.getFoodLevel());
        player.setExhaustion(playerstats.getExhaustion());
        player.setSaturation(playerstats.getSaturation());
        player.setLevel(playerstats.getLevel());
        player.setExp(playerstats.getExp());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        Collection<PotionEffect> potionEffects = playerstats.getPotionEffects();

        if (potionEffects != null) {
            player.addPotionEffects(playerstats.getPotionEffects());
        }
    }


    public static  PlayerStats loadPlayerStats(String player, Group group)
    {
        WorldInventories plugin = WorldInventories.getSharedInstance();
        String path = File.separator + group.getName();
        path = plugin.getDataFolder().getAbsolutePath() + path;

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
            Logger.logError("Failed to load stats for player: " + player + ": " + e.getMessage());
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
            Logger.logDebug("Player " + player + " will get a new stats file on next save (clearing now).");
        }
        else {
            playerstats = new PlayerStats(health, foodLevel, (float)exhaustion, (float)saturation, level, (float)exp, potionEffects);
        }

        setPlayerStats(plugin.getServer().getPlayer(player), playerstats);

        Logger.logDebug("Loaded stats for player: " + player + " " + path);

        return playerstats;
    }


    public static void savePlayerStats(Player player, Group group)
    {
        savePlayerStats(player.getName(), group, new PlayerStats(player.getHealth(), player.getFoodLevel(), player.getExhaustion(), player.getSaturation(), player.getLevel(), player.getExp(), player.getActivePotionEffects()));
    }


    public static void savePlayerStats(String player, Group group, PlayerStats playerstats)
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
            Logger.logError("Failed to save stats for player: " + player + ": " + e.getMessage());
        }

        Logger.logDebug("Saved stats for player: " + player + " " + path);
    }
}
