package me.drayshak.WorldInventories;

import java.io.Serializable;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.enchantments.Enchantment;

public class WIItemStack implements Serializable
{
    private static final long serialVersionUID = -6239771143618730223L;
    private int type = 0;
    private int amount = 0;
    private WIMaterialData data = null;
    private short durability = 0; 
    
    private Map< Enchantment, Integer > enchantments = null;
    
    public WIItemStack(final int ttype, final int tamount, final short tdamage, final Byte tdata, final Map< Enchantment, Integer > tenchantments)
    {
        this.enchantments = tenchantments;
        this.type = ttype;
        this.amount = tamount;
        this.durability = tdamage;
        if (tdata != null)
        {
            Material tMat = Material.getMaterial(ttype);

            if (tMat == null)   this.data = new WIMaterialData(ttype, tdata);
            else 
            {
                final MaterialData mdata = tMat.getNewData(tdata);
                this.data = new WIMaterialData(mdata.getItemTypeId(), mdata.getData());
            }                
            
                
            this.durability = tdata;
        }
    }
    
    public int getTypeId()
    {
         return this.type;
    }
    
    public int getAmount()
    {
        return this.amount;
    }
    
    public short getDurability()
    {
        return this.durability;
    }
    
    public WIMaterialData getData()
    {
        return this.data;
    }

    public Map< Enchantment, Integer > getEnchantments()
    {
        return this.enchantments;
    }
}
