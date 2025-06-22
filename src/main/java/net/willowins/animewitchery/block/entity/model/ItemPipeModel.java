package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.willowins.animewitchery.block.entity.ItemPipeBlockEntity;

import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;


public class ItemPipeModel extends GeoModel<ItemPipeBlockEntity> {

    @Override
    public Identifier getModelResource(ItemPipeBlockEntity object) {
        return new Identifier("animewitchery", "geo/block/item_pipe.geo.json");
    }

    @Override
    public Identifier getTextureResource(ItemPipeBlockEntity object) {
        return new Identifier("animewitchery", "textures/block/item_pipe.png");
    }

    @Override
    public Identifier getAnimationResource(ItemPipeBlockEntity animatable) {
        return new Identifier("animewitchery", "animations/item_pipe.animation.json");
    }

}