package com.github.nyuppo.variant;

import java.util.Random;

public record DiscardableModifier(double discardChance) implements VariantModifier {
    public boolean shouldDiscard(Random random) {
        return random.nextDouble() < this.discardChance;
    }
}
