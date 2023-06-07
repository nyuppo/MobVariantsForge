package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantSettings;
import com.github.nyuppo.config.VariantWeights;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<Integer> VARIANT_ID =
            SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
    private static final String NBT_KEY = "Variant";
    // 0 = default
    // 1 = jupiter
    // 2 = husky
    // 3 = german shepherd
    // 4 = golden retriever
    // 5 = french bulldog

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Wolf)(Object)this).getEntityData().define(VARIANT_ID, 0);
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putInt(NBT_KEY, ((Wolf)(Object)this).getEntityData().get(VARIANT_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Wolf)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getInt(NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        int i = this.getRandomVariant(p_21434_.getRandom());
        ((Wolf)(Object)this).getEntityData().set(VARIANT_ID, i);
    }

    @Inject(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Wolf;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_, CallbackInfoReturnable<Wolf> ci) {
        Wolf child = (Wolf) EntityType.WOLF.create(p_148890_);

        int i = 0;
        if (p_148891_.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            CompoundTag thisNbt = new CompoundTag();
            ((Wolf)(Object)this).saveWithoutId(thisNbt);
            CompoundTag parentNbt = new CompoundTag();
            p_148891_.saveWithoutId(parentNbt);

            if (thisNbt.contains(NBT_KEY) && parentNbt.contains(NBT_KEY)) {
                int thisVariant = thisNbt.getInt(NBT_KEY);
                int parentVariant = parentNbt.getInt(NBT_KEY);

                if (thisVariant == parentVariant) {
                    // If both parents are the same variant, just pick that one
                    i = thisVariant;
                } else {
                    // Handle breeding
                    boolean hasBred = false;

                    if ((thisVariant == 2 && parentVariant == 1) || (thisVariant == 1 && parentVariant == 2)) { // German shepherd
                        if (p_148891_.getRandom().nextInt(10) < VariantSettings.getWolfBreedingChance() && !VariantBlacklist.isBlacklisted("wolf", "german_shepherd")) {
                            hasBred = true;
                            i = 3;
                        }
                    } else if ((thisVariant == 1 && parentVariant == 0) || (thisVariant == 0 && parentVariant == 1)) { // Golden retriever
                        if (p_148891_.getRandom().nextInt(10) < VariantSettings.getWolfBreedingChance() && !VariantBlacklist.isBlacklisted("wolf", "golden_retriever")) {
                            hasBred = true;
                            i = 4;
                        }
                    } else if ((thisVariant == 2 && parentVariant == 4) || (thisVariant == 4 && parentVariant == 2)) { // French bulldog
                        if (p_148891_.getRandom().nextInt(10) < VariantSettings.getWolfBreedingChance() && !VariantBlacklist.isBlacklisted("wolf", "french_bulldog")) {
                            hasBred = true;
                            i = 5;
                        }
                    }

                    // Otherwise, pick a random parent's variant
                    if (!hasBred) {
                        i = p_148891_.getRandom().nextBoolean() ? thisVariant : parentVariant;
                    }
                }
            }
        } else {
            // Give child random variant
            i = this.getRandomVariant(p_148891_.getRandom());
        }

        CompoundTag childNbt = new CompoundTag();
        child.saveWithoutId(childNbt);
        childNbt.putInt(NBT_KEY, i);
        child.readAdditionalSaveData(childNbt);

        ci.setReturnValue(child);
    }

    private int getVariantID(String variantName) {
        return switch (variantName) {
            case "jupiter" -> 1;
            case "husky" -> 2;
            case "german_shepherd" -> 3;
            case "golden_retriever" -> 4;
            case "french_bulldog" -> 5;
            default -> 0;
        };
    }

    private int getRandomVariant(RandomSource random) {
        return getVariantID(VariantWeights.getRandomVariant("wolf", random));
    }
}
