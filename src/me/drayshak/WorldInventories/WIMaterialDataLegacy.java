package me.drayshak.WorldInventories;

import java.io.Serializable;
import org.bukkit.Material;

public class WIMaterialDataLegacy implements Serializable
{
    private static final long serialVersionUID = 1385103110780786554L;
    private final int type;
    private byte data = 0;

    public WIMaterialDataLegacy(final int type)
    {
        this(type, (byte) 0);
    }

    public WIMaterialDataLegacy(final Material type)
    {
        this(type, (byte) 0);
    }        

    public WIMaterialDataLegacy(final int type, final byte data)
    {
        this.type = type;
        this.data = data;
    }

    public WIMaterialDataLegacy(final Material type, final byte data)
    {
        this(type.getId(), data);
    }
    
    public byte getData()
    {
        return data;
    }
    
    public void setData(byte data)
    {
        this.data = data;
    }    
}
