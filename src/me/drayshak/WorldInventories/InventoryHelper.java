package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper
{
    public static int ITEMS = 0;
    
    protected InventoriesSaveable inventories;
       
    public InventoriesSaveable getSerializable()
    {
        return this.inventories;
    }
    
    public void setItems(ItemStack[] items)
    {
        inventories.setItemStack(ITEMS, items);
    }    
    
    public ItemStack[] getItems()
    {
        return inventories.getItemStack(ITEMS);
    }    
    
    public List<Map<String, Object>> getItemsList()
    {
        return inventories.getItemStackList(ITEMS);
    }
}
