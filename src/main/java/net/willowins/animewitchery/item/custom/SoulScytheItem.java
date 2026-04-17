package net.willowins.animewitchery.item.custom;

import net.minecraft.item.ToolMaterial;

public class SoulScytheItem extends ScytheItem {
    public SoulScytheItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, 4, settings);
    }

    // Logic is handled in CapturingHandler
}
