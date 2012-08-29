package me.drayshak.WorldInventories;

import java.util.ArrayList;
import java.util.List;

public class Group
{
    private String name;
    private List<String> worlds;
    
    public Group(String tName)
    {
        this.name = tName;
        this.worlds = new ArrayList<String>();
    }
    public Group(String tName, List<String> tWorlds)
    {
        this.name = tName;
        this.worlds = tWorlds;
    }
    
    public void addWorld(String world)
    {
        if(!this.worlds.contains(world))
        {
            this.worlds.add(world);
        }
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName(String tName)
    {
        this.name = tName;
    }
    
    public List<String> getWorlds()
    {
        return this.worlds;
    }
    
    public void setWorlds(List<String> tWorlds)
    {
        this.worlds = tWorlds;
    }
    
    public boolean contains(String tWorldName)
    {
        return this.worlds.contains(tWorldName);
    }
}
