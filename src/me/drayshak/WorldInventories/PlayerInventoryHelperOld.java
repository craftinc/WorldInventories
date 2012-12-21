package me.drayshak.WorldInventories;

import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryHelperOld extends InventoryHelperOld
{
    public static int ARMOUR = 1;
    
    public PlayerInventoryHelperOld(ItemStack[] tPlayerItems, ItemStack[] tPlayerArmour)
    {
        inventories = new InventoriesListsOld(ItemStackHelper.formSerializedMap(tPlayerItems), ItemStackHelper.formSerializedMap(tPlayerArmour));
    }
 
    public PlayerInventoryHelperOld(List<Map<String, Object>> tPlayerItems, List<Map<String, Object>> tPlayerArmour)
    {
        inventories = new InventoriesListsOld(tPlayerItems, tPlayerArmour);
    }
    
    public PlayerInventoryHelperOld(InventoriesListsOld inventories)
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
