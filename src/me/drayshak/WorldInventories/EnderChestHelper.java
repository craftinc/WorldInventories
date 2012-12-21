package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class EnderChestHelper extends InventoryHelperOld
{
    public EnderChestHelper(ItemStack[] items)
    {
        inventories = new InventoriesLists(ItemStackHelper.formSerializedMap(items));
    }
 
    public EnderChestHelper(List<Map<String, Object>> items)
    {
        inventories = new InventoriesLists(items);
    }
    
    public EnderChestHelper(InventoriesLists inventories)
    {
        this.inventories = inventories;
    }
}
