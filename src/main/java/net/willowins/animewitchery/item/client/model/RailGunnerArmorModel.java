package net.willowins.animewitchery.item.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.armor.RailGunnerArmorItem;
import software.bernie.geckolib.model.GeoModel;

public class RailGunnerArmorModel extends GeoModel<RailGunnerArmorItem> {
    @Override
    public Identifier getModelResource(RailGunnerArmorItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/item/armor/railgunner_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(RailGunnerArmorItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/item/armor/railgunner_armor.png");
    }

    @Override
    public Identifier getAnimationResource(RailGunnerArmorItem animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/item/armor/railgunner_armor.animation.json");
    }
}
