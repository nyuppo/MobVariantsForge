package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
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
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
public abstract class PigVariantsMixin extends MobVariantsMixin {
    private static final EntityDataAccessor<String> VARIANT_ID =
            SynchedEntityData.defineId(Pig.class, EntityDataSerializers.STRING);
    private static final String NBT_KEY = "Variant";

    private static final EntityDataAccessor<Boolean> MUDDY_ID = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.BOOLEAN);
    private static final String MUDDY_NBT_KEY = "IsMuddy";

    @Override
    protected void onDefineSynchedData(CallbackInfo ci) {
        ((Pig)(Object)this).getEntityData().define(VARIANT_ID, "default");
        ((Pig)(Object)this).getEntityData().define(MUDDY_ID, false);
    }

    @Override
    protected void onAddAdditionalSaveData(CompoundTag p_21484_, CallbackInfo ci) {
        p_21484_.putString(NBT_KEY, ((Pig)(Object)this).getEntityData().get(VARIANT_ID));
        p_21484_.putBoolean(MUDDY_NBT_KEY, ((Pig)(Object)this).getEntityData().get(MUDDY_ID));
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag p_21450_, CallbackInfo ci) {
        ((Pig)(Object)this).getEntityData().set(VARIANT_ID, p_21450_.getString(NBT_KEY));
        ((Pig)(Object)this).getEntityData().set(MUDDY_ID, p_21450_.getBoolean(MUDDY_NBT_KEY));
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_, CallbackInfoReturnable<SpawnGroupData> cir) {
        String variant = this.getRandomVariant(p_21434_.getRandom());
        ((Pig)(Object)this).getEntityData().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle muddy pigs
        if (VariantSettings.getEnableMuddyPigs()) {
            if (((Pig)(Object)this).level().getBlockState(((Pig)(Object)this).blockPosition()).is(MoreMobVariants.PIG_MUD_BLOCKS) || ((Pig)(Object)this).level().getBlockState(((Pig)(Object)this).blockPosition().below()).is(MoreMobVariants.PIG_MUD_BLOCKS)) {
                ((Pig)(Object)this).getEntityData().set(MUDDY_ID, true);
            } else if (((Pig)(Object)this).isInWaterOrRain()) {
                ((Pig)(Object)this).getEntityData().set(MUDDY_ID, false);
            }
        }

        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((Pig)(Object)this).getEntityData().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((Pig)(Object)this).level().getRandom());
            ((Pig)(Object)this).getEntityData().set(VARIANT_ID, variant);
        }
    }

    @Inject(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Pig;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_, CallbackInfoReturnable<Pig> ci) {
        Pig child = (Pig)EntityType.PIG.create(p_148890_);

        String variant = "default";
        if (p_148891_.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            CompoundTag thisNbt = new CompoundTag();
            ((Pig)(Object)this).saveWithoutId(thisNbt);
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

        CompoundTag childNbt = new CompoundTag();
        child.saveWithoutId(childNbt);
        childNbt.putString(NBT_KEY, variant);
        child.readAdditionalSaveData(childNbt);

        ci.setReturnValue(child);
    }

    private String getRandomVariant(RandomSource random) {
        return VariantWeights.getRandomVariant("pig", random);
    }
}
