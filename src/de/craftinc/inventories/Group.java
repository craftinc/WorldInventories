package de.craftinc.inventories;

import java.util.List;
import org.bukkit.GameMode;

public class Group
{
    private String name;
    private List<String> worlds;
    private GameMode gamemode;
    
    public Group(String tName, List<String> tWorlds, GameMode mode)
    {
        this.name = tName;
        this.worlds = tWorlds;
        this.gamemode = mode;        
    }

    public String getName()
    {
        return this.name;
    }
    
    public List<String> getWorlds()
    {
        return this.worlds;
    }
    
    public GameMode getGameMode()
    {
        return this.gamemode;
    }
}
