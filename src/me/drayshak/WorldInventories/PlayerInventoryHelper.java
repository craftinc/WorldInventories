package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryHelper extends InventoryHelper
{
    public static int ARMOUR = 1;
    
    public PlayerInventoryHelper(ItemStack[] tPlayerItems, ItemStack[] tPlayerArmour)
    {
        inventories = new InventoriesLists(ItemStackHelper.formSerializedMap(tPlayerItems), ItemStackHelper.formSerializedMap(tPlayerArmour));
    }
 
    public PlayerInventoryHelper(List<Map<String, Object>> tPlayerItems, List<Map<String, Object>> tPlayerArmour)
    {
        inventories = new InventoriesLists(tPlayerItems, tPlayerArmour);
    }
    
    public PlayerInventoryHelper(InventoriesLists inventories)
    {
        this.inventories = inventories;
    }
    
    public void setArmour(ItemStack[] armour)
    {
        inventories.setItemStack(ARMOUR, armour);
    }

    public ItemStack[] getArmour()
    {
        return inventories.getItemStack(ARMOUR); 
    }    
    
    public List<Map<String, Object>> getArmourList()
    {
        return inventories.getItemStackList(ARMOUR);
    }
    

}
