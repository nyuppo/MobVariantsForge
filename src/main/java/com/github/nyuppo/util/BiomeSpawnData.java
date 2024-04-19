package com.github.nyuppo.util;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record BiomeSpawnData(TagKey<Biome> validSpawnBiomes, Holder<Biome> spawnBiome) {
    public boolean canSpawn() {
        return spawnBiome.is(validSpawnBiomes);
    }
}
