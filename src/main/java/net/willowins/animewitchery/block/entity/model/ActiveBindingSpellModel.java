package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.ActiveBindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.ActiveEffigyFountainBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ActiveBindingSpellModel extends DefaultedBlockGeoModel<ActiveBindingSpellBlockEntity> {
    public ActiveBindingSpellModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "active_binding_spell"));
    }
}
