package me.drayshak.WorldInventories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class InventoriesSaveable implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<List<Map<String, Object>>> itemStacks;
    
    public InventoriesSaveable(List<Map<String, Object>>... argItemStacks)
    {
        itemStacks = new ArrayList<List<Map<String, Object>>>(argItemStacks.length);
        itemStacks.addAll(Arrays.asList(argItemStacks));
    }
    
    public void setItemStack(int ID, ItemStack[] items)
    {
        itemStacks.set(ID, InventoryHelper.formSerializedMap(items));
    }
    
    public void setItemStackList(int ID, List<Map<String, Object>> items)
    {
        itemStacks.set(ID, items);
    }

    public ItemStack[] getItemStack(int ID)
    {
        return InventoryHelper.formDeserializedArray(itemStacks.get(ID));
    }
    
    public List<Map<String, Object>> getItemStackList(int ID)
    {
        return itemStacks.get(ID);
    }  
}
