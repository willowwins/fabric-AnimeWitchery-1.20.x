package net.willowins.animewitchery.item.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.HealingStaff;
import software.bernie.geckolib.model.GeoModel;

public class HealingStaffModel extends GeoModel<HealingStaff> {
    @Override
    public Identifier getModelResource(HealingStaff animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/item/healing_staff_model.geo.json");
    }

    @Override
    public Identifier getTextureResource(HealingStaff animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/item/healing_staff_model.png");
    }

    @Override
    public Identifier getAnimationResource(HealingStaff animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/item/healing_staff_model.animation.json");
    }
}
