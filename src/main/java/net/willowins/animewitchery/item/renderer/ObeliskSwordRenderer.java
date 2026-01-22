package net.willowins.animewitchery.item.renderer;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.ObeliskSwordItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.willowins.animewitchery.item.client.model.ObeliskSwordModel;

public class ObeliskSwordRenderer extends GeoItemRenderer<ObeliskSwordItem> {
    public ObeliskSwordRenderer() {
        super(new ObeliskSwordModel());
    }
}
