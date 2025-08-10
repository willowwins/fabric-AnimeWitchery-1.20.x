package net.willowins.animewitchery.item.renderer;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.ObeliskSwordItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ObeliskSwordRenderer extends GeoItemRenderer<ObeliskSwordItem> {
    public ObeliskSwordRenderer() {
        super(new DefaultedItemGeoModel(new Identifier(AnimeWitchery.MOD_ID, "obelisk_sword")));
    }
}


