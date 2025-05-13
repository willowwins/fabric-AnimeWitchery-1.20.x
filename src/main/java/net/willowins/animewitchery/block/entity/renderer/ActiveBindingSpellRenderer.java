package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.ActiveBindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.ActiveEffigyFountainBlockEntity;
import net.willowins.animewitchery.block.entity.model.ActiveBindingSpellModel;
import net.willowins.animewitchery.block.entity.model.ActiveEffigyFountainModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ActiveBindingSpellRenderer extends GeoBlockRenderer<ActiveBindingSpellBlockEntity> {
    public ActiveBindingSpellRenderer() {
        super(new ActiveBindingSpellModel());
    }
}
