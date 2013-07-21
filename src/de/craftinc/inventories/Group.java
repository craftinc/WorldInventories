package de.craftinc.inventories;

//importers java.util.ArrayList;
import java.util.List;
import org.bukkit.GameMode;

public class Group
{
    private String name;
    private List<String> worlds;
    private GameMode gamemode;

    // TODO: remove unsused methods?
//    public Group(String tName)
//    {
//        this.name = tName;
//        this.worlds = new ArrayList<String>();
//        this.gamemode = GameMode.SURVIVAL;
//    }
//
//    public Group(String tName, List<String> tWorlds)
//    {
//        this.name = tName;
//        this.worlds = tWorlds;
//        this.gamemode = GameMode.SURVIVAL;
//    }
    
    public Group(String tName, List<String> tWorlds, GameMode mode)
    {
        this.name = tName;
        this.worlds = tWorlds;
        this.gamemode = mode;        
    }
    
//    public void addWorld(String world)
//    {
//        if(!this.worlds.contains(world))
//        {
//            this.worlds.add(world);
//        }
//    }
    
    public String getName()
    {
        return this.name;
    }
    
//    public void setName(String tName)
//    {
//        this.name = tName;
//    }
    
    public List<String> getWorlds()
    {
        return this.worlds;
    }
    
//    public void setWorlds(List<String> tWorlds)
//    {
//        this.worlds = tWorlds;
//    }
    
    public GameMode getGameMode()
    {
        return this.gamemode;
    }
    
//    public void setGameMode(GameMode mode)
//    {
//        this.gamemode = mode;
//    }
//
//    public boolean contains(String tWorldName)
//    {
//        return this.worlds.contains(tWorldName);
//    }
//
//    public static String getName(Group g)
//    {
//        return g.getName();
//    }
}
