package com.github.nyuppo.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public class MobVariantsMixin {
    @Inject(
            method = "defineSynchedData",
            at = @At("RETURN")
    )
    protected void onDefineSynchedData(CallbackInfo ci) {

    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("RETURN")
    )
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {

    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("RETURN")
    )
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {

    }

    @Inject(
            method = "finalizeSpawn",
            at = @At("RETURN")
    )
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {

    }
}
