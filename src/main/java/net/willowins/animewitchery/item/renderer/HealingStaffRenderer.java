package net.willowins.animewitchery.item.renderer;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.HealingStaff;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HealingStaffRenderer extends GeoItemRenderer<HealingStaff> {
    public HealingStaffRenderer() {
        super(new DefaultedItemGeoModel(new Identifier(AnimeWitchery.MOD_ID, "healing_staff_model")));
    }
}
