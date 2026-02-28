package net.willowins.animewitchery.item.custom;

import net.minecraft.item.ToolMaterial;

public class SoulScytheItem extends ScytheItem {
    public SoulScytheItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // Logic is handled in CapturingHandler
}
