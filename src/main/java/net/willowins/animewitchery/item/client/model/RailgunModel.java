package net.willowins.animewitchery.item.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.RailgunItem;
import software.bernie.geckolib.model.GeoModel;

public class RailgunModel extends GeoModel<RailgunItem> {
    @Override
    public Identifier getModelResource(RailgunItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/item/railgun_model.geo.json");
    }

    @Override
    public Identifier getTextureResource(RailgunItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/item/railgun_model.png");
    }

    @Override
    public Identifier getAnimationResource(RailgunItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/item/railgun_model.animation.json");
    }
}
