package net.willowins.animewitchery.item.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.ObeliskSwordItem;
import software.bernie.geckolib.model.GeoModel;

public class ObeliskSwordModel extends GeoModel<ObeliskSwordItem> {
    @Override
    public Identifier getModelResource(ObeliskSwordItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/item/obelisk_sword.geo.json");
    }

    @Override
    public Identifier getTextureResource(ObeliskSwordItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/item/obelisk_sword.png");
    }

    @Override
    public Identifier getAnimationResource(ObeliskSwordItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/item/obelisk_sword.animation.json");
    }
}
