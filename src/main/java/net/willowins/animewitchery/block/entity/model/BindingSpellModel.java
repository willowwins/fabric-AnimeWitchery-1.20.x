package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.ActiveBindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.BindingSpellBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class BindingSpellModel extends DefaultedBlockGeoModel<BindingSpellBlockEntity> {
    public BindingSpellModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "binding_spell"));
    }
}
