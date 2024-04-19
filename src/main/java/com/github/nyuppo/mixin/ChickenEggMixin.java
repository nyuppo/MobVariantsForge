package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
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
        MobVariant variant = Variants.getRandomVariant(EntityType.CHICKEN, chickenEntity.level().getRandom().nextLong(), chickenEntity.level().getBiome(chickenEntity.blockPosition()), null, chickenEntity.level().getMoonBrightness());

        CompoundTag newNbt = new CompoundTag();
        chickenEntity.saveWithoutId(newNbt);
        newNbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        chickenEntity.readAdditionalSaveData(newNbt);

        return chickenEntity;
    }
}
