package net.willowins.animewitchery.entity.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.custom.MonsterStatueBlock;
import net.willowins.animewitchery.entity.custom.MonsterStatueBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class MonsterStatueModel extends GeoModel<MonsterStatueBlockEntity> {
    @Override
    public Identifier getModelResource(MonsterStatueBlockEntity animatable) {
        if (animatable.getCachedState().get(MonsterStatueBlock.ACTIVATED)) {
            return new Identifier(AnimeWitchery.MOD_ID, "geo/block/monster_statue_active.geo.json");
        }
        return new Identifier(AnimeWitchery.MOD_ID, "geo/block/monster_statue.geo.json");
    }

    @Override
    public Identifier getTextureResource(MonsterStatueBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/block/monster_statue.png");
    }

    @Override
    public Identifier getAnimationResource(MonsterStatueBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/block/monster_statue.animation.json"); // Default/Placeholder
    }
}
