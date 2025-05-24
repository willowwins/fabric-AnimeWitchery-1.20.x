package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import net.willowins.animewitchery.block.entity.BindingSpellBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class AlchemyTableModel extends DefaultedBlockGeoModel<AlchemyTableBlockEntity> {
    public AlchemyTableModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "alchemy_table"));
    }
}
