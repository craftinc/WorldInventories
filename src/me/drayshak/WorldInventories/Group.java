package me.drayshak.WorldInventories;

import java.util.List;

public class Group
{
    private String name;
    private List<String> worlds;
    private boolean bDoesKeepInventory = false;
    
    public Group(String tName, List<String> tWorlds, boolean tDoesKeepInventory)
    {
        this.name = tName;
        this.worlds = tWorlds;
        this.bDoesKeepInventory = tDoesKeepInventory;
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
    
    public void setDoesKeepInventory(boolean tDoesKeepInventory)
    {
        this.bDoesKeepInventory = tDoesKeepInventory;
    }
    
    public boolean doesKeepInventory()
    {
        return this.bDoesKeepInventory;
    }
}
