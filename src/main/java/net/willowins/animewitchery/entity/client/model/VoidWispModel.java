package net.willowins.animewitchery.entity.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.entity.VoidWispEntity;
import software.bernie.geckolib.model.GeoModel;

public class VoidWispModel extends GeoModel<VoidWispEntity> {
    @Override
    public Identifier getModelResource(VoidWispEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/void_wisp.geo.json");
    }

    @Override
    public Identifier getTextureResource(VoidWispEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/entity/void_wisp.png");
    }

    @Override
    public Identifier getAnimationResource(VoidWispEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/entity/void_wisp.animation.json");
    }
}
