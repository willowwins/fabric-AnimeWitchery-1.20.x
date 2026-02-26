package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import java.util.UUID;

public class HoarderArmorItem extends ArmorItem {
    private static final UUID[] MODIFIERS = new UUID[] {
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    };

    public HoarderArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    // Dynamic attribute modification based on inventory is tricky because
    // getAttributeModifiers is static/cached often.
    // Instead, we can apply a transient effect or verify in damage event.
    // For simplicity, let's just use Tick Update to give Strength if gold count >
    // X.

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && slot == EquipmentSlot.HEAD.getEntitySlotId()) {
            // Check Gold Count
            if (player.age % 40 == 0) { // Check occasionally
                int goldCount = 0;
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack s = player.getInventory().getStack(i);
                    if (s.getItem() == Items.GOLD_INGOT)
                        goldCount += s.getCount();
                    if (s.getItem() == Items.GOLD_BLOCK)
                        goldCount += s.getCount() * 9;
                    if (s.getItem() == Items.GOLD_NUGGET)
                        goldCount += s.getCount() / 9;
                }

                // Grant buffs? Or just keep it as mechanic concept.
                // Let's grant Absorption if rich.
                if (goldCount > 64) {
                    // player.addStatusEffect(...)
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
