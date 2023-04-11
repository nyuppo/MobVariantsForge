package com.github.nyuppo.mixin;

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

        if (chickenEntity.level.getBiome(chickenEntity.blockPosition()).is(BiomeTags.IS_NETHER) && chickenEntity.getRandom().nextInt(6) == 0) {
            i = 7;
        }

        CompoundTag newNbt = new CompoundTag();
        chickenEntity.saveWithoutId(newNbt);
        newNbt.putInt("Variant", i);
        chickenEntity.readAdditionalSaveData(newNbt);

        return chickenEntity;
    }

    private int getRandomVariant(RandomSource random) {
        int i = random.nextInt(14);
        if (i == 0) {
            // Ayam Cemani
            return 6;
        } else if (i > 0 && i <= 2) {
            // Golden
            return 1;
        } else if (i > 2 && i <= 4) {
            // Gold Crested
            return 2;
        } else if (i > 4 && i <= 6) {
            // Welsummer
            return 3;
        } else if (i > 6 && i <= 8) {
            // Cochin
            return 4;
        } else if (i > 8 && i <= 10) {
            // Bantam
            return 5;
        }
        // Default
        return 0;
    }
}
