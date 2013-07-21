package de.craftinc.inventories;

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
    
    public double getHealth()
    {
        return this.health;
    }

    public int getFoodLevel()
    {
        return this.foodLevel;
    }
    
    public float getExhaustion()
    {
        return this.exhaustion;
    }
    
    public float getSaturation()
    {
        return this.saturation;
    }
    
    public int getLevel()
    {
        return this.level;
    }
    
    public float getExp()
    {
        return this.exp;
    }
}