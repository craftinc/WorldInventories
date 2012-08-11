package me.drayshak.WorldInventories;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Import78Helper
{
    public static WIPlayerInventory load78PlayerInventory(File file)
    {
        WIPlayerInventory playerInventory = null;

        FileInputStream fIS = null;
        ObjectInputStream obIn = null;

        String path = file.getAbsolutePath();
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerInventory = (WIPlayerInventory) obIn.readObject();
            obIn.close();
            fIS.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return playerInventory;
    }
}
