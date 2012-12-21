package me.drayshak.WorldInventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class InventoriesListsOld
{
    private List<List<Map<String, Object>>> itemStacks;
  
    public InventoriesListsOld(List<Map<String, Object>>... argItemStacks)
    {
        itemStacks = new ArrayList<List<Map<String, Object>>>(argItemStacks.length);
        itemStacks.addAll(Arrays.asList(argItemStacks));
    }
    
    public InventoriesListsOld(List<List<Map<String, Object>>> itemStacks)
    {
        this.itemStacks = itemStacks;
    }
    
    public void setItemStacks(List<Map<String, Object>>... itemStacks)
    {
        this.itemStacks = new ArrayList<List<Map<String, Object>>>(itemStacks.length);
        this.itemStacks.addAll(Arrays.asList(itemStacks));
    }
    
    public List<List<Map<String, Object>>> getItemStacks()
    {
        return this.itemStacks;
    }
    
    public void setItemStack(int ID, ItemStack[] items)
    {
        itemStacks.set(ID, ItemStackHelper.formSerializedMap(items));
    }
    
    public void setItemStackList(int ID, List<Map<String, Object>> items)
    {
        itemStacks.set(ID, items);
    }

    public ItemStack[] getItemStack(int ID)
    {
        return ItemStackHelper.formDeserializedArray(itemStacks.get(ID));
    }
    
    public List<Map<String, Object>> getItemStackList(int ID)
    {
        return itemStacks.get(ID);
    }  
}
