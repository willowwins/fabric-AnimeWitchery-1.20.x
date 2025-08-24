package net.willowins.animewitchery.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.entity.KamikazeRitualEntity;
import net.willowins.animewitchery.entity.client.model.KamikazeRitualModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KamikazeRitualRenderer extends GeoEntityRenderer<KamikazeRitualEntity> {
    
    public KamikazeRitualRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new KamikazeRitualModel());
    }

    @Override
    public Identifier getTextureLocation(KamikazeRitualEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/entity/kamikaze_ritual.png");
    }
}
