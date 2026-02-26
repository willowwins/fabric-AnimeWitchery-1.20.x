package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.DeepslateThresholdBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class DeepslateThresholdModel extends GeoModel<DeepslateThresholdBlockEntity> {
    @Override
    public Identifier getModelResource(DeepslateThresholdBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/block/deepslate_threshold.geo.json");
    }

    @Override
    public Identifier getTextureResource(DeepslateThresholdBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/block/deepslate_threshold.png");
    }

    @Override
    public Identifier getAnimationResource(DeepslateThresholdBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/block/deepslate_threshold.animation.json");
    }
}
