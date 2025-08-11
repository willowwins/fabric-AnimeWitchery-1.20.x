package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.model.ObeliskModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ObeliskRenderer extends GeoBlockRenderer<ObeliskBlockEntity> {
    public ObeliskRenderer() {
        super(new ObeliskModel());
    }
    
    @Override
    public Identifier getTextureLocation(ObeliskBlockEntity animatable) {
        int variant = animatable.getTextureVariant();
        if (variant == 0) {
            return new Identifier(AnimeWitchery.MOD_ID, "textures/block/obelisk.png");
        } else {
            return new Identifier(AnimeWitchery.MOD_ID, "textures/block/obelisk" + (variant + 1) + ".png");
        }
    }
} 