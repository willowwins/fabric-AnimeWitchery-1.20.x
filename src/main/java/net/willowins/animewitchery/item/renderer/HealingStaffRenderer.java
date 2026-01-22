package net.willowins.animewitchery.item.renderer;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.HealingStaff;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.willowins.animewitchery.item.client.model.HealingStaffModel;

public class HealingStaffRenderer extends GeoItemRenderer<HealingStaff> {
    public HealingStaffRenderer() {
        super(new HealingStaffModel());
    }
}
