package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantWeights;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrownEgg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownEgg.class)
public class ChickenEggMixin {
    @ModifyVariable(
            method = "onHit",
            at = @At("STORE")
    )
    private Chicken mixin(Chicken chickenEntity) {
        int i = this.getRandomVariant(chickenEntity.getRandom());

        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (chickenEntity.level.getBiome(chickenEntity.blockPosition()).is(BiomeTags.IS_NETHER) && chickenEntity.getRandom().nextInt(6) == 0) {
                i = 7;
            }
        }


        CompoundTag newNbt = new CompoundTag();
        chickenEntity.saveWithoutId(newNbt);
        newNbt.putInt("Variant", i);
        chickenEntity.readAdditionalSaveData(newNbt);

        return chickenEntity;
    }

    public int getVariantID(String variantName) {
        return switch(variantName) {
            case "amber" -> 1;
            case "gold_crested" -> 2;
            case "bronzed" -> 3;
            case "skewbald" -> 4;
            case "stormy" -> 5;
            case "midnight" -> 6;
            default -> 0;
        };
    }

    public int getRandomVariant(RandomSource random) {
        return getVariantID(VariantWeights.getRandomVariant("chicken", random));
    }
}
