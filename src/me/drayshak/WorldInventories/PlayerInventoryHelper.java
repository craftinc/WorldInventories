package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryHelper
{
    public static int ITEMS = 0;
    public static int ARMOUR = 1;
    
    private InventoriesSaveable inventories;
    
    public PlayerInventoryHelper(ItemStack[] tPlayerItems, ItemStack[] tPlayerArmour)
    {
        inventories = new InventoriesSaveable(InventoryHelper.formSerializedMap(tPlayerItems), InventoryHelper.formSerializedMap(tPlayerArmour));
    }
 
    public PlayerInventoryHelper(List<Map<String, Object>> tPlayerItems, List<Map<String, Object>> tPlayerArmour)
    {
        inventories = new InventoriesSaveable(tPlayerItems, tPlayerArmour);
    }
    
    public PlayerInventoryHelper(InventoriesSaveable inventories)
    {
        this.inventories = inventories;
    }
    
    public void setItems(ItemStack[] items)
    {
        inventories.setItemStack(ITEMS, items);
    }
   
    public void setArmour(ItemStack[] armour)
    {
        inventories.setItemStack(ARMOUR, armour);
    }
    
    public ItemStack[] getItems()
    {
        return inventories.getItemStack(ITEMS);
    }
    
    public ItemStack[] getArmour()
    {
        return inventories.getItemStack(ARMOUR); 
    }    
    
    public List<Map<String, Object>> getItemsList()
    {
        return inventories.getItemStackList(ITEMS);
    }
    
    public List<Map<String, Object>> getArmourList()
    {
        return inventories.getItemStackList(ARMOUR);
    }
    
    public InventoriesSaveable getSerializable()
    {
        return this.inventories;
    }
}
