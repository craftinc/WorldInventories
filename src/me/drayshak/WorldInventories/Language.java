package me.drayshak.WorldInventories;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import static me.drayshak.WorldInventories.WorldInventories.logError;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Language
{
    private final static String diedMessageDefault = "You died! Wiped inventory and stats for group: ";
    private final static String changedMessageDefault = "Changed player information to match group: ";
    private final static String noChangeMessageDefault = "No player information change needed to match group: ";
    private final static String loadedMessageDefault = "Player information loaded for group: ";
    
    private final WorldInventories plugin;
    private HashMap<String, String> messages;
    
    public Language(WorldInventories plugin)
    {
        this.plugin = plugin;
        this.messages = new HashMap<String, String>();
    }
    
    public String get(String key)
    {
        return messages.get(key);
    }
    
    public boolean loadLanguages(String locale)
    {
        YamlConfiguration config = new YamlConfiguration();
        
        try
        {    
            config.load(new File(plugin.getDataFolder().getPath(), "lang.yml"));            
        }
        catch(FileNotFoundException e)
        {
            plugin.saveResource("lang.yml", true);
        }
        catch(Exception e)
        {
            logError("Failed to load languages, using defaults: " + e.getMessage());
        }

        messages.put("died-message", diedMessageDefault);
        messages.put("changed-message", changedMessageDefault);
        messages.put("nochange-message", noChangeMessageDefault);
        messages.put("loaded-message", loadedMessageDefault);
        
        try
        {
            boolean langexists = config.isConfigurationSection(locale);
            if(!langexists) throw new Exception("Language not found!");
            ConfigurationSection langs = config.getConfigurationSection(locale);
            
            for(String key : messages.keySet())
            {
                try
                {
                    messages.put(key, langs.getString(key));
                }
                catch(Exception e)
                {
                    logError("Failed to load language key, using default: " + key);
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            logError("Failed to load language '" + locale + "', using defaults: " + e.getMessage());
            return false;
        }
        
        return true;      
    }
}
