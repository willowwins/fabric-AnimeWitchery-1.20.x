package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.BossObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.model.BossObeliskModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BossObeliskRenderer extends GeoBlockRenderer<BossObeliskBlockEntity> {
    public BossObeliskRenderer() {
        super(new BossObeliskModel());
    }
}


