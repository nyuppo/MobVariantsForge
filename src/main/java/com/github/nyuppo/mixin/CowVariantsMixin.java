package com.github.nyuppo.mixin;

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
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class CowVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<Integer> VARIANT_ID =
            SynchedEntityData.defineId(Cow.class, EntityDataSerializers.INT);
    private static final String NBT_KEY = "Variant";
    // 0 = default
    // 1 = ashen
    // 2 = cookie
    // 3 = dairy
    // 4 = pinto
    // 5 = sunset
    // 6 = wooly
    // 7 = umbra

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Cow)(Object)this).getEntityData().define(VARIANT_ID, 0);
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putInt(NBT_KEY, ((Cow)(Object)this).getEntityData().get(VARIANT_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Cow)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getInt(NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        int i = this.getRandomVariant(p_21434_.getRandom());
        ((Cow)(Object)this).getEntityData().set(VARIANT_ID, i);
    }

    @Inject(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Cow;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_, CallbackInfoReturnable<Cow> ci) {
        Cow child = (Cow)EntityType.COW.create(p_148890_);

        int i = 0;
        if (p_148891_.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            CompoundTag thisNbt = new CompoundTag();
            ((Cow)(Object)this).saveWithoutId(thisNbt);
            CompoundTag parentNbt = new CompoundTag();
            p_148891_.saveWithoutId(parentNbt);

            if (thisNbt.contains(NBT_KEY) && parentNbt.contains(NBT_KEY)) {
                int thisVariant = thisNbt.getInt(NBT_KEY);
                int parentVariant = parentNbt.getInt(NBT_KEY);

                if (thisVariant == parentVariant) {
                    // If both parents are the same variant, just pick that one
                    i = thisVariant;
                } else {
                    // Otherwise, pick a random parent's variant
                    i = p_148891_.getRandom().nextBoolean() ? thisVariant : parentVariant;
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

    private int getRandomVariant(RandomSource random) {
        int i = random.nextInt(16);
        if (i == 0) {
            // Umbra
            return 7;
        } else if (i > 0 && i <= 2) {
            // Ashen
            return 1;
        } else if (i > 2 && i <= 4) {
            // Cookie
            return 2;
        } else if (i > 4 && i <= 6) {
            // Dairy
            return 3;
        } else if (i > 6 && i <= 8) {
            // Pinto
            return 4;
        } else if (i > 8 && i <= 10) {
            // Sunset
            return 5;
        } else if (i > 10 && i <= 12) {
            // Wooly
            return 6;
        }
        // Default
        return 0;
    }
}
