package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class GrandShulkerBoxModel extends DefaultedBlockGeoModel<GrandShulkerBoxBlockEntity> {
    
    public GrandShulkerBoxModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box"));
    }
    
    @Override
    public Identifier getModelResource(GrandShulkerBoxBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/block/grand_shulker.geo.json");
    }
    
    @Override
    public Identifier getTextureResource(GrandShulkerBoxBlockEntity animatable) {
        // Always use the base grand shulker box texture
        return new Identifier(AnimeWitchery.MOD_ID, "textures/block/grand_shulker_box.png");
    }
    
    @Override
    public Identifier getAnimationResource(GrandShulkerBoxBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/grand_shulker_box.animation.json");
    }
}
