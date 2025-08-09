package net.willowins.animewitchery.item.armor.client;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.armor.ObeliskArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class ObeliskArmorRenderer extends GeoArmorRenderer<ObeliskArmorItem> {
    public ObeliskArmorRenderer() {
        super(new DefaultedItemGeoModel<>(new Identifier(AnimeWitchery.MOD_ID, "armor/obelisk_armor")));
    }
}
