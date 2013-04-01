package me.drayshak.WorldInventories;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import static me.drayshak.WorldInventories.WorldInventories.logError;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Language
{
    private static String died_message_default = "You died! Wiped inventory and stats for group: ";
    private static String changed_message_default = "Changed player information to match group: ";
    private static String nochange_message_default = "No player information change needed to match group: ";
    private static String loaded_message_default = "Player information loaded for group: ";
    
    private final WorldInventories plugin;
    private HashMap<String, String> messages;
    
    public Language(WorldInventories plugin)
    {
        this.plugin = plugin;
        this.messages = new HashMap();
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
        
        messages.put("died-message", died_message_default);
        messages.put("changed-message", changed_message_default);
        messages.put("nochange-message", nochange_message_default);
        messages.put("loaded-message", loaded_message_default);        
        
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
