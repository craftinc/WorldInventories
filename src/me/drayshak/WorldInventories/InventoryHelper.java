package me.drayshak.WorldInventories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper
{
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
}
