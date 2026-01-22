package net.willowins.animewitchery.item.armor.client;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.armor.RailGunnerArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Supplier;

import net.willowins.animewitchery.item.client.model.RailGunnerArmorModel;

public final class RailGunnerArmorRenderer extends GeoArmorRenderer<RailGunnerArmorItem> {
    public RailGunnerArmorRenderer() {
        super(new RailGunnerArmorModel());
    }
}
