package com.github.nyuppo.mixin;

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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<String> VARIANT_ID =
            SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Zombie)(Object)this).getEntityData().define(VARIANT_ID, "default");
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putString(NBT_KEY, ((Zombie)(Object)this).getEntityData().get(VARIANT_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Zombie)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getString(NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        String variant = this.getRandomVariant(p_21434_.getRandom());
        ((Zombie)(Object)this).getEntityData().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((Zombie)(Object)this).getEntityData().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((Zombie)(Object)this).level().getRandom());
            ((Zombie)(Object)this).getEntityData().set(VARIANT_ID, variant);
        }
    }

    private String getRandomVariant(RandomSource random) {
        return VariantWeights.getRandomVariant("zombie", random);
    }
}
