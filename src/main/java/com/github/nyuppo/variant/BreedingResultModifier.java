package com.github.nyuppo.variant;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public record BreedingResultModifier(ResourceLocation parent1, ResourceLocation parent2, double breedingChance) implements VariantModifier {
    public boolean validParents(MobVariant parent1, MobVariant parent2) {
        return (parent1.getIdentifier().equals(this.parent1) && parent2.getIdentifier().equals(this.parent2))
                || (parent1.getIdentifier().equals(this.parent2) && parent2.getIdentifier().equals(this.parent1));
    }

    public boolean shouldBreed(RandomSource random) {
        return random.nextDouble() < this.breedingChance;
    }
}
