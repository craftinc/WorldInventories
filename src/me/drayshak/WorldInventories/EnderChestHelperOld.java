package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class EnderChestHelperOld extends InventoryHelperOld
{
    public EnderChestHelperOld(ItemStack[] items)
    {
        inventories = new InventoriesLists(ItemStackHelper.formSerializedMap(items));
    }
 
    public EnderChestHelperOld(List<Map<String, Object>> items)
    {
        inventories = new InventoriesLists(items);
    }
    
    public EnderChestHelperOld(InventoriesLists inventories)
    {
        this.inventories = inventories;
    }
}
