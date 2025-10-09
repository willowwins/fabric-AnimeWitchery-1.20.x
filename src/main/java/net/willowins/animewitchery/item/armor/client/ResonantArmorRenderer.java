package net.willowins.animewitchery.item.armor.client;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.armor.ResonantArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class ResonantArmorRenderer extends GeoArmorRenderer<ResonantArmorItem> {
    public ResonantArmorRenderer() {
        super(new DefaultedItemGeoModel<>(new Identifier(AnimeWitchery.MOD_ID, "armor/resonant_armor")));
    }
}
