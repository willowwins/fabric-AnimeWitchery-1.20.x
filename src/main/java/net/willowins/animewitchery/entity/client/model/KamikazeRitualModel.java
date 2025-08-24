package net.willowins.animewitchery.entity.client.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.entity.KamikazeRitualEntity;
import software.bernie.geckolib.model.GeoModel;

public class KamikazeRitualModel extends GeoModel<KamikazeRitualEntity> {
    @Override
    public Identifier getModelResource(KamikazeRitualEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "geo/kamikaze.geo.json");
    }

    @Override
    public Identifier getTextureResource(KamikazeRitualEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/entity/kamikaze_ritual.png");
    }

    @Override
    public Identifier getAnimationResource(KamikazeRitualEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "animations/entity/kamikaze.animation.json");
    }
}
