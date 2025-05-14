package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.ActiveBindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.BindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.model.ActiveBindingSpellModel;
import net.willowins.animewitchery.block.entity.model.BindingSpellModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BindingSpellRenderer extends GeoBlockRenderer<BindingSpellBlockEntity> {
    public BindingSpellRenderer() {
        super(new BindingSpellModel());
    }
}
