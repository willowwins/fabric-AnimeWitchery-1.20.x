package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.ActiveEffigyFountainBlockEntity;
import net.willowins.animewitchery.block.entity.model.ActiveEffigyFountainModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ActiveEffigyFountainRenderer extends GeoBlockRenderer<ActiveEffigyFountainBlockEntity> {
    public ActiveEffigyFountainRenderer() {
        super(new ActiveEffigyFountainModel());
    }
}
