package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WandItem extends Item {
    
    private static final String SPELL_KEY = "encoded_spell";
    private static final String POWER_KEY = "wand_power";
    
    public WandItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (world.isClient) {
            return new TypedActionResult<>(ActionResult.PASS, stack);
        }
        
        // Check if player is holding a rune stone in off-hand to encode
        ItemStack offhandStack = user.getOffHandStack();
        if (net.willowins.animewitchery.util.SpellEncoder.isRuneStone(offhandStack)) {
            ActionResult result = net.willowins.animewitchery.util.SpellEncoder.encodeSpellFromRune(
                user, world, hand, stack, offhandStack);
            return new TypedActionResult<>(result, stack);
        }
        
        // Check if wand has an encoded spell
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains(SPELL_KEY)) {
            String spell = nbt.getString(SPELL_KEY);
            user.sendMessage(Text.translatable("message.animewitchery.wand_cast_spell", spell), true);
            
            // Cast the spell!
            net.willowins.animewitchery.util.SpellCaster.castSpell(user, world, spell);
            
            // Add cooldown
            user.getItemCooldownManager().set(this, 20); // 1 second cooldown
            
        } else {
            user.sendMessage(Text.translatable("message.animewitchery.wand_no_spell"), true);
        }
        
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(SPELL_KEY)) {
            String spell = nbt.getString(SPELL_KEY);
            tooltip.add(Text.translatable("tooltip.animewitchery.wand_encoded_spell", spell));
        } else {
            tooltip.add(Text.translatable("tooltip.animewitchery.wand_empty"));
        }
        
        if (nbt != null && nbt.contains(POWER_KEY)) {
            int power = nbt.getInt(POWER_KEY);
            tooltip.add(Text.translatable("tooltip.animewitchery.wand_power", power));
        }
        
        super.appendTooltip(stack, world, tooltip, context);
    }
    
    // Methods for encoding spells (to be used by spell encoding system later)
    public static void encodeSpell(ItemStack wand, String spell) {
        NbtCompound nbt = wand.getOrCreateNbt();
        nbt.putString(SPELL_KEY, spell);
    }
    
    public static void setWandPower(ItemStack wand, int power) {
        NbtCompound nbt = wand.getOrCreateNbt();
        nbt.putInt(POWER_KEY, power);
    }
    
    public static boolean hasSpell(ItemStack wand) {
        NbtCompound nbt = wand.getNbt();
        return nbt != null && nbt.contains(SPELL_KEY);
    }
    
    public static String getEncodedSpell(ItemStack wand) {
        NbtCompound nbt = wand.getNbt();
        if (nbt != null && nbt.contains(SPELL_KEY)) {
            return nbt.getString(SPELL_KEY);
        }
        return "";
    }
    
    public static int getWandPower(ItemStack wand) {
        NbtCompound nbt = wand.getNbt();
        if (nbt != null && nbt.contains(POWER_KEY)) {
            return nbt.getInt(POWER_KEY);
        }
        return 0;
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasSpell(stack);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(SPELL_KEY)) {
            String spell = nbt.getString(SPELL_KEY);
            Formatting color = getSpellColor(spell);
            return Text.translatable(this.getTranslationKey(stack)).formatted(color);
        }
        return super.getName(stack);
    }
    
    /**
     * Gets the color formatting for a spell type
     */
    private static Formatting getSpellColor(String spellName) {
        return switch (spellName) {
            case "Fire Blast" -> Formatting.RED;
            case "Water Shield" -> Formatting.AQUA;
            case "Earth Spike" -> Formatting.DARK_GREEN;
            case "Wind Gust" -> Formatting.WHITE;
            case "Healing Wave" -> Formatting.GREEN;
            case "Wither Touch" -> Formatting.DARK_PURPLE;
            case "Light Burst" -> Formatting.YELLOW;
            case "Shadow Bind" -> Formatting.DARK_GRAY;
            default -> Formatting.RESET;
        };
    }
}
