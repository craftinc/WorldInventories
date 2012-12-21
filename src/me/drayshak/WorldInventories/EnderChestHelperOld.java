package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class EnderChestHelperOld extends InventoryHelperOld
{
    public EnderChestHelperOld(ItemStack[] items)
    {
        inventories = new InventoriesListsOld(ItemStackHelper.formSerializedMap(items));
    }
 
    public EnderChestHelperOld(List<Map<String, Object>> items)
    {
        inventories = new InventoriesListsOld(items);
    }
    
    public EnderChestHelperOld(InventoriesListsOld inventories)
    {
        this.inventories = inventories;
    }
}
