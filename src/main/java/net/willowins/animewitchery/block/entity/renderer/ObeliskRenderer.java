package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.model.ObeliskModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ObeliskRenderer extends GeoBlockRenderer<ObeliskBlockEntity> {
    public ObeliskRenderer() {
        super(new ObeliskModel());
    }
} 