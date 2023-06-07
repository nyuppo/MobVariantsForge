package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantBlacklist;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.CatVariantTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Cat.class)
public class CatBlacklistMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void handleBlacklistedVariants(ServerLevelAccessor p_28134_, DifficultyInstance p_28135_, MobSpawnType p_28136_, @Nullable SpawnGroupData p_28137_, @Nullable CompoundTag p_28138_, CallbackInfoReturnable<SpawnGroupData> cir) {
        boolean isValidVariant = false;

        while (!isValidVariant) {
            CatVariant currentVariant = ((Cat)(Object)this).getVariant();
            if ((currentVariant.equals(MoreMobVariants.DOUG) && VariantBlacklist.isBlacklisted("cat", "doug"))
                    || (currentVariant.equals(MoreMobVariants.HANDSOME) && VariantBlacklist.isBlacklisted("cat", "handsome"))
                    || (currentVariant.equals(MoreMobVariants.GRAY_TABBY) && VariantBlacklist.isBlacklisted("cat", "gray_tabby"))
                    || (currentVariant.equals(MoreMobVariants.TORTOISESHELL) && VariantBlacklist.isBlacklisted("cat", "tortoiseshell"))) {
                boolean bl = p_28134_.getMoonBrightness() > 0.9F;
                TagKey<CatVariant> tagKey = bl ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
                BuiltInRegistries.CAT_VARIANT.getTag(tagKey).flatMap((list) -> {
                    return list.getRandomElement(p_28134_.getRandom());
                }).ifPresent((variant) -> {
                    ((Cat)(Object)this).setVariant((CatVariant)variant.value());
                });
            } else {
                isValidVariant = true;
            }
        }
    }
}
