package me.drayshak.WorldInventories;

import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper
{
    private HashMap<String, ItemStack[]> itemstacks;

    public InventoryHelper()
    {
        this.itemstacks = new HashMap();
    }
    
    public ItemStack[] getArmour()
    {
        return itemstacks.get("armour");
    }
    
    public ItemStack[] getInventory()
    {
        return itemstacks.get("inventory");
    }
    
    public void setArmour(ItemStack[] armour)
    {
        itemstacks.put("armour", armour);
    }
    
    public void setInventory(ItemStack[] inventory)
    {
        itemstacks.put("inventory", inventory);
    }
}
