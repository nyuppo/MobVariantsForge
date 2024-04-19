package com.github.nyuppo.variant;

import net.minecraft.util.RandomSource;

public record DiscardableModifier(double discardChance) implements VariantModifier {
    public boolean shouldDiscard(RandomSource random) {
        return random.nextDouble() < this.discardChance;
    }
}
