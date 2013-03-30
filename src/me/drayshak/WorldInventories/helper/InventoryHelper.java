package me.drayshak.WorldInventories.helper;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper
{
    private ArrayList<ItemStack[]> itemstacks;
    private static int INVENTORY = 0;
    private static int ARMOUR = 1;

    public InventoryHelper()
    {
        this.itemstacks = new ArrayList();
    }
    
    public ItemStack[] getArmour()
    {
        return itemstacks.get(ARMOUR);
    }
    
    public ItemStack[] getInventory()
    {
        return itemstacks.get(INVENTORY);
    }
    
    public void setArmour(ItemStack[] armour)
    {
        itemstacks.set(ARMOUR, armour);
    }
    
    public void setInventory(ItemStack[] inventory)
    {
        itemstacks.set(INVENTORY, inventory);
    }
}
