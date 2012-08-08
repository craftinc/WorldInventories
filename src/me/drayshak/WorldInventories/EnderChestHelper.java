package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class EnderChestHelper extends InventoryHelper
{
    public EnderChestHelper(ItemStack[] items)
    {
        inventories = new InventoriesSaveable(ItemStackHelper.formSerializedMap(items));
    }
 
    public EnderChestHelper(List<Map<String, Object>> items)
    {
        inventories = new InventoriesSaveable(items);
    }
    
    public EnderChestHelper(InventoriesSaveable inventories)
    {
        this.inventories = inventories;
    }
}
