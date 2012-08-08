package me.drayshak.WorldInventories;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class MultiInvImportHelper
{
    public static ItemStack itemFromMIString(String sItem)
    {
        String[] sSplit = sItem.split(",");
        
        if (sSplit.length >= 4)
        {
            int typeId = Integer.parseInt(sSplit[0]);
            short durability = 0;
            byte data = 0;
            if(Material.getMaterial(typeId).getMaxDurability() > 0)
                durability = Short.parseShort(sSplit[3]);
            else
                data = Byte.parseByte(sSplit[3]);
            
            ItemStack itemret = new ItemStack(typeId, Integer.parseInt(sSplit[1]), durability, data);
            itemret.addUnsafeEnchantments(new HashMap<Enchantment, Integer>());
            return itemret;
        }
        else
        {
            return null;
        }
    }
    
    public static WIPlayerInventory playerInventoryFromMIString(String string)
    {
        if(string == null) return null;
        
        String[] sSplit = string.split(";-;");
        
        ItemStack[] playerItems = null;
        ItemStack[] playerArmour = null;
        
        if (sSplit.length >= 3)
        {
            if (!sSplit[0].equals("!!!"))
            {
                playerItems = new ItemStack[36];
                
                String[] itemsSplit = sSplit[0].split(";");
                
                int iMin = Math.min(36, itemsSplit.length);
                for(int i = 0; i < iMin; i++)
                {
                    playerItems[i] = itemFromMIString(itemsSplit[i]);
                }
            }

            if (!sSplit[1].equals("!!!"))
            {
                playerArmour = new ItemStack[4];
                
                String[] armourSplit = sSplit[1].split(";");

                int iMin = Math.min(4, armourSplit.length);
                for(int i = 0; i < iMin; i++)
                {
                    playerArmour[i] = itemFromMIString(armourSplit[i]);
                }
            }
        }
        
        return new WIPlayerInventory(playerItems, playerArmour);
    }
}
