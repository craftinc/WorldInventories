package me.drayshak.WorldInventories;

import com.thoughtworks.xstream.XStream;
import java.io.File;

public class Import15Helper
{
    public static EnderChestHelperOld load15PlayerEnderChest(File file, XStream xstream)
    {
        InventoriesLists playerInventory = null;
      
        try
        {
            playerInventory = (InventoriesLists) xstream.fromXML(file);
        }
        catch(Exception e)
        {
            playerInventory = null;
            e.printStackTrace();
            return null;
        }
        
        return new EnderChestHelperOld(playerInventory);        
    }
    
    public static PlayerInventoryHelperOld load15PlayerInventory(File file, XStream xstream)
    {
        InventoriesLists playerInventory = null;
      
        try
        {
            playerInventory = (InventoriesLists) xstream.fromXML(file);
        }
        catch(Exception e)
        {
            playerInventory = null;
            e.printStackTrace();
            return null;
        }
        return new PlayerInventoryHelperOld(playerInventory);
    }

    public static PlayerStats load15PlayerStats(File file, XStream xstream)
    {
        PlayerStats playerstats = null;

        try
        {
            playerstats = (PlayerStats) xstream.fromXML(file);
        }
        catch(Exception e)
        {
            playerstats = null;
            e.printStackTrace();
            return null;
        }

        return playerstats;
    }
}
