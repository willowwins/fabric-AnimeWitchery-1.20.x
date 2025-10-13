package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.util.SpellConfiguration;

public class SpellTriggerBlockEntity extends BlockEntity {
    
    private static final String SPELL_KEY = "encoded_spell"; // Legacy support
    private static final String SPELL_CONFIG_KEY = "spell_configuration";
    private String encodedSpell = ""; // Legacy single spell
    private SpellConfiguration spellConfiguration = null; // New multi-spell config
    
    public SpellTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPELL_TRIGGER_BLOCK_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(SPELL_KEY)) {
            this.encodedSpell = nbt.getString(SPELL_KEY);
        }
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!encodedSpell.isEmpty()) {
            nbt.putString(SPELL_KEY, encodedSpell);
        }
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    
    public void encodeSpell(String spell) {
        this.encodedSpell = spell;
        this.markDirty();
        
        // Send update to client
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public boolean hasSpell() {
        return !encodedSpell.isEmpty();
    }
    
    public String getEncodedSpell() {
        return encodedSpell;
    }
    
    public void clearSpell() {
        this.encodedSpell = "";
        this.markDirty();
        
        // Send update to client
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
