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
        String variant = this.getRandomVariant(chickenEntity.getRandom());

        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (chickenEntity.level().getBiome(chickenEntity.blockPosition()).is(BiomeTags.IS_NETHER) && chickenEntity.getRandom().nextInt(6) == 0) {
                variant = "bone";
            }
        }


        CompoundTag newNbt = new CompoundTag();
        chickenEntity.saveWithoutId(newNbt);
        newNbt.putString("Variant", variant);
        chickenEntity.readAdditionalSaveData(newNbt);

        return chickenEntity;
    }

    public String getRandomVariant(RandomSource random) {
        return VariantWeights.getRandomVariant("chicken", random);
    }
}
