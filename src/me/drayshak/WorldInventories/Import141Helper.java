package me.drayshak.WorldInventories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Import141Helper
{
    public static PlayerInventoryHelperOld load141PlayerInventory(File file)
    {
        InventoriesSaveable playerInventory = null;

        FileInputStream fIS = null;
        ObjectInputStream obIn = null;

        String path = file.getAbsolutePath();
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerInventory = (InventoriesSaveable) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if(obIn != null)
            {
                try { obIn.close(); } catch (IOException e) {}
            }            
            if (fIS != null)
            {
                try { fIS.close(); } catch (IOException e) {}
            }
        }          

        return new PlayerInventoryHelperOld(new InventoriesLists(playerInventory.getItemStacks()));
    }
    
    public static EnderChestHelper load141EnderChest(File file)
    {
        InventoriesSaveable playerInventory = null;

        FileInputStream fIS = null;
        ObjectInputStream obIn = null;

        String path = file.getAbsolutePath();
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerInventory = (InventoriesSaveable) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if(obIn != null)
            {
                try { obIn.close(); } catch (IOException e) {}
            }            
            if (fIS != null)
            {
                try { fIS.close(); } catch (IOException e) {}
            }
        }        

        return new EnderChestHelper(new InventoriesLists(playerInventory.getItemStacks()));
    }    
    
    public static PlayerStats load141PlayerStats(File file)
    {
        WIPlayerStats oldstats = null;

        FileInputStream fIS = null;
        ObjectInputStream obIn = null;

        String path = file.getAbsolutePath();
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            oldstats = (WIPlayerStats) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if(obIn != null)
            {
                try { obIn.close(); } catch (IOException e) {}
            }            
            if (fIS != null)
            {
                try { fIS.close(); } catch (IOException e) {}
            }
        }          
        
        return new PlayerStats(oldstats.getHealth(), oldstats.getFoodLevel(), oldstats.getExhaustion(), oldstats.getSaturation(), oldstats.getLevel(), oldstats.getExp(), null);
    }
}
