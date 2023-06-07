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
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chicken.class)
public abstract class ChickenVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<Integer> VARIANT_ID =
            SynchedEntityData.defineId(Chicken.class, EntityDataSerializers.INT);
    private static final String NBT_KEY = "Variant";
    // 0 = default
    // 1 = amber
    // 2 = gold_crested
    // 3 = bronzed
    // 4 = skewbald
    // 5 = stormy
    // 6 = midnight
    // 7 = bone

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Chicken)(Object)this).getEntityData().define(VARIANT_ID, 0);
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putInt(NBT_KEY, ((Chicken)(Object)this).getEntityData().get(VARIANT_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Chicken)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getInt(NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        int i = this.getRandomVariant(p_21434_.getRandom());

        // If in nether, random chance of bone chicken
        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (p_21434_.getBiome(((Chicken)(Object)this).blockPosition()).is(BiomeTags.IS_NETHER) && p_21434_.getRandom().nextInt(6) == 0) {
                i = 7;
            }
        }

        ((Chicken)(Object)this).getEntityData().set(VARIANT_ID, i);
    }

    @Inject(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Chicken;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_, CallbackInfoReturnable<Chicken> ci) {
        Chicken child = (Chicken) EntityType.CHICKEN.create(p_148890_);

        int i = 0;
        if (p_148891_.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            CompoundTag thisNbt = new CompoundTag();
            ((Chicken)(Object)this).saveWithoutId(thisNbt);
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

        // If in nether, random chance of bone chicken
        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (p_148890_.getBiome(((Chicken)(Object)this).blockPosition()).is(BiomeTags.IS_NETHER) && ((Chicken)(Object)this).getRandom().nextInt(6) == 0) {
                i = 7;
            }
        }

        CompoundTag childNbt = new CompoundTag();
        child.saveWithoutId(childNbt);
        childNbt.putInt(NBT_KEY, i);
        child.readAdditionalSaveData(childNbt);

        ci.setReturnValue(child);
    }

    public int getVariantID(String variantName) {
        return switch(variantName) {
            case "amber" -> 1;
            case "gold_crested" -> 2;
            case "bronzed" -> 3;
            case "skewbald" -> 4;
            case "stormy" -> 5;
            case "midnight" -> 6;
            default -> 0;
        };
    }

    public int getRandomVariant(RandomSource random) {
        return getVariantID(VariantWeights.getRandomVariant("chicken", random));
    }
}
