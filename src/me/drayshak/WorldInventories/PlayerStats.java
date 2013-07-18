package me.drayshak.WorldInventories;

import java.util.Collection;
import org.bukkit.potion.PotionEffect;

public class PlayerStats
{
    private double health;
    private int foodLevel;
    private float exhaustion;
    private float saturation;
    
    private int level;
    private float exp;
    
    private Collection<PotionEffect> potionEffects;
    
    public PlayerStats(double health, int foodLevel, float exhaustion, float saturation, int level, float exp, Collection<PotionEffect> potionEffects)
    {
        this.health = health;
        this.foodLevel = foodLevel;
        this.exhaustion = exhaustion;
        this.saturation = saturation;
        this.level = level;
        this.exp = exp;
        this.potionEffects = potionEffects;
    }
    
    public Collection<PotionEffect> getPotionEffects()
    {
        return this.potionEffects;
    }
    
//    public void setPotionEffects(Collection<PotionEffect> potionEffects)
//    {
//        this.potionEffects = potionEffects;
//    }
    
    public double getHealth()
    {
        return this.health;
    }
    
//    public void setHealth(double health)
//    {
//        this.health = health;
//    }
//
    public int getFoodLevel()
    {
        return this.foodLevel;
    }
    
//    public void setFoodLevel(int foodlevel)
//    {
//        this.foodLevel = foodlevel;
//    }
    
    public float getExhaustion()
    {
        return this.exhaustion;
    }
    
//    public void setExhaustion(float exhaustion)
//    {
//        this.exhaustion = exhaustion;
//    }
    
    public float getSaturation()
    {
        return this.saturation;
    }
    
//    public void setSaturation(float saturation)
//    {
//        this.saturation = saturation;
//    }
    
    public int getLevel()
    {
        return this.level;
    }
    
//    public void setLevel(int level)
//    {
//        this.level = level;
//    }
    
    public float getExp()
    {
        return this.exp;
    }
    
//    public void setExp(float exp)
//    {
//        this.exp = exp;
//    }
}