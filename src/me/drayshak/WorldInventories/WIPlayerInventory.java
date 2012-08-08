package me.drayshak.WorldInventories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class WIPlayerInventory implements Serializable
{
    private static final long serialVersionUID = 1312L;
    List<Map<String, Object>> playerItems = null;
    List<Map<String, Object>> playerArmour = null;

    public static Map<String, Object> serializeItemStack(ItemStack items)
    {
        if(items == null)
        {
            return null;
        }
        else
        {
            return items.serialize();
        }
    }
    
    public static List<Map<String, Object>> formSerializedMap(ItemStack[] items)
    {
        List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>(items.length);
        
        for(ItemStack itemstack : items)
        {
            retlist.add(serializeItemStack(itemstack));
        }
        
        return retlist;
    }
    
    public static ItemStack[] formDeserializedArray(List<Map<String, Object>> items)
    {
        ItemStack[] itemRet = new ItemStack[items.size()];
        Map<String, Object> item = null;
        
        for(int i = 0; i < itemRet.length; i++)
        {
            item = items.get(i);
            if(item == null)
            {
                itemRet[i] = null;
               
            }
            else
            {
                itemRet[i] = ItemStack.deserialize(items.get(i));
            }
        }
        
        return itemRet;        
    }
    
    public WIPlayerInventory(ItemStack[] tPlayerItems, ItemStack[] tPlayerArmour)
    {
        playerItems = formSerializedMap(tPlayerItems);
        playerArmour = formSerializedMap(tPlayerArmour);
    }
 
    public WIPlayerInventory(List<Map<String, Object>> tPlayerItems, List<Map<String, Object>> tPlayerArmour)
    {
        this.playerItems = tPlayerItems;
        this.playerArmour = tPlayerArmour;
    }    
    
    public void setItems(ItemStack[] items)
    {
        playerItems = formSerializedMap(items);
    }
   
    public void setArmour(ItemStack[] items)
    {
        playerArmour = formSerializedMap(items);   
    }
    
    public List<Map<String, Object>> getItemsWI()
    {
        return this.playerItems;
    }
    
    public List<Map<String, Object>> getArmourWI()
    {
        return this.playerArmour;
    }
    
    public ItemStack[] getItems()
    {
        return formDeserializedArray(playerItems);
    }
    
    public ItemStack[] getArmour()
    {
        return formDeserializedArray(playerArmour);  
    }
}
