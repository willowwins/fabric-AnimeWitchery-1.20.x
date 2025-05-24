package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import net.willowins.animewitchery.block.entity.BindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.model.AlchemyTableModel;
import net.willowins.animewitchery.block.entity.model.BindingSpellModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AlchemyTableRenderer extends GeoBlockRenderer<AlchemyTableBlockEntity> {
    public AlchemyTableRenderer() {
        super(new AlchemyTableModel());
    }
}
