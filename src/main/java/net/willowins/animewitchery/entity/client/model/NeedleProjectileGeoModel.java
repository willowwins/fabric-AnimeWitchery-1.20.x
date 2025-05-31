package net.willowins.animewitchery.entity.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.entity.projectile.NeedleProjectileEntity;
import software.bernie.geckolib.model.GeoModel;

public class NeedleProjectileGeoModel extends GeoModel<NeedleProjectileEntity> {

    @Override
    public Identifier getModelResource(NeedleProjectileEntity object) {
        return new Identifier("animewitchery", "geo/needle_projectile_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(NeedleProjectileEntity object) {
        return new Identifier("animewitchery", "textures/entity/needle_projectile_entity.png");
    }


    @Override
    public Identifier getAnimationResource(NeedleProjectileEntity animatable) {
        return new Identifier("animewitchery", "animations/needle_projectile_entity.animation.json");
    }
}
