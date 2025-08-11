package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.GuardianStatueBlockEntity;
import net.willowins.animewitchery.block.entity.model.GuardianStatueModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GuardianStatueRenderer extends GeoBlockRenderer<GuardianStatueBlockEntity> {
    
    public GuardianStatueRenderer() {
        super(new GuardianStatueModel());
    }
}
