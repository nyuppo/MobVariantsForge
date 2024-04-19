package com.github.nyuppo.variant;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record SpawnableBiomesModifier(TagKey<Biome> spawnBiomes) implements VariantModifier {
    public boolean canSpawnInBiome(Holder<Biome> biome) {
        return biome.is(this.spawnBiomes);
    }
}
