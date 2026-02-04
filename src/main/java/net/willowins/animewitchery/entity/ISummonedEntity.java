package net.willowins.animewitchery.entity;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ISummonedEntity {
    @Nullable
    UUID getSummonerUuid();

    void setSummonerUuid(@Nullable UUID uuid);

    @Nullable
    LivingEntity getSummoner();
}
