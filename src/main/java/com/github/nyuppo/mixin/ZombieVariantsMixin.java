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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<Integer> VARIANT_ID =
            SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
    private static final String NBT_KEY = "Variant";
    /*
     *   0 = steve (default)
     *   1 = alex
     *   2 = ari
     *   3 = efe
     *   4 = kai
     *   5 = makena
     *   6 = noor
     *   7 = sunny
     *   8 = zuri
     */

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Zombie)(Object)this).getEntityData().define(VARIANT_ID, 0);
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putInt(NBT_KEY, ((Zombie)(Object)this).getEntityData().get(VARIANT_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Zombie)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getInt(NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        int i = this.getRandomVariant(p_21434_.getRandom());
        ((Zombie)(Object)this).getEntityData().set(VARIANT_ID, i);
    }

    private int getRandomVariant(RandomSource random) {
        int i = random.nextInt(12);
        if (i == 11) {
            return 8;
        } else if (i == 10) {
            return 7;
        }
        else if (i == 9) {
            return 6;
        }
        else if (i == 8) {
            return 5;
        }
        else if (i == 7) {
            return 4;
        }
        else if (i == 6) {
            return 3;
        }
        else if (i == 5) {
            return 2;
        } else if (i == 3 || i == 4) {
            return 1;
        }
        return 0;
    }
}
