package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantWeights;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chicken.class)
public abstract class ChickenVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<String> VARIANT_ID =
            SynchedEntityData.defineId(Chicken.class, EntityDataSerializers.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Chicken)(Object)this).getEntityData().define(VARIANT_ID, "default");
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putString(NBT_KEY, ((Chicken)(Object)this).getEntityData().get(VARIANT_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Chicken)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getString(NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        String variant = this.getRandomVariant(p_21434_.getRandom());

        // If in nether, random chance of bone chicken
        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (p_21434_.getBiome(((Chicken)(Object)this).blockPosition()).is(BiomeTags.IS_NETHER) && p_21434_.getRandom().nextInt(6) == 0) {
                variant = "bone";
            }
        }

        ((Chicken)(Object)this).getEntityData().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((Chicken)(Object)this).getEntityData().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((Chicken)(Object)this).level().getRandom());
            ((Chicken)(Object)this).getEntityData().set(VARIANT_ID, variant);
        }
    }

    @Inject(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Chicken;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_, CallbackInfoReturnable<Chicken> ci) {
        Chicken child = (Chicken) EntityType.CHICKEN.create(p_148890_);

        String variant = "default";
        if (p_148891_.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            CompoundTag thisNbt = new CompoundTag();
            ((Chicken)(Object)this).saveWithoutId(thisNbt);
            CompoundTag parentNbt = new CompoundTag();
            p_148891_.saveWithoutId(parentNbt);

            if (thisNbt.contains(NBT_KEY) && parentNbt.contains(NBT_KEY)) {
                String thisVariant = thisNbt.getString(NBT_KEY);
                String parentVariant = parentNbt.getString(NBT_KEY);

                if (thisVariant.equals(parentVariant)) {
                    // If both parents are the same variant, just pick that one
                    variant = thisVariant;
                } else {
                    // Otherwise, pick a random parent's variant
                    variant = p_148891_.getRandom().nextBoolean() ? thisVariant : parentVariant;
                }
            }
        } else {
            // Give child random variant
            variant = this.getRandomVariant(p_148891_.getRandom());
        }

        // If in nether, random chance of bone chicken
        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (p_148890_.getBiome(((Chicken)(Object)this).blockPosition()).is(BiomeTags.IS_NETHER) && ((Chicken)(Object)this).getRandom().nextInt(6) == 0) {
                variant = "bone";
            }
        }

        CompoundTag childNbt = new CompoundTag();
        child.saveWithoutId(childNbt);
        childNbt.putString(NBT_KEY, variant);
        child.readAdditionalSaveData(childNbt);

        ci.setReturnValue(child);
    }

    public String getRandomVariant(RandomSource random) {
        return VariantWeights.getRandomVariant("chicken", random);
    }
}
