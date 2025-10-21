package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGameRules {
    
    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_CAPTURING = 
        GameRuleRegistry.register(
            "allowCapturing", 
            GameRules.Category.PLAYER, 
            GameRuleFactory.createBooleanRule(true) // Default to true (enabled)
        );
    
    public static final GameRules.Key<GameRules.BooleanRule> DO_CREEPER_BLOCK_DAMAGE = 
        GameRuleRegistry.register(
            "doCreeperBlockDamage", 
            GameRules.Category.MOBS, 
            GameRuleFactory.createBooleanRule(true) // Default to true (vanilla behavior)
        );
    
    public static void register() {
        // Registration happens during static initialization
    }
}



