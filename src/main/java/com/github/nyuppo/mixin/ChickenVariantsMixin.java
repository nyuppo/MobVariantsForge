package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.networking.MMVPacketHandler;
import com.github.nyuppo.networking.S2CRespondVariantPacket;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chicken.class)
public abstract class ChickenVariantsMixin extends MobVariantsMixin {
    private MobVariant variant = Variants.getDefaultVariant(EntityType.CHICKEN);

    @Override
    protected void onAddAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.getString(MoreMobVariants.NBT_KEY).isEmpty()) {
            if (nbt.getString(MoreMobVariants.NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.CHICKEN, new ResourceLocation(nbt.getString(MoreMobVariants.NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.CHICKEN, MoreMobVariants.id(nbt.getString(MoreMobVariants.NBT_KEY)));
            }
        } else {
            variant = Variants.getRandomVariant(EntityType.CHICKEN, ((Chicken)(Object)this).level().getRandom().nextLong(), ((Chicken)(Object)this).level().getBiome(((Chicken)(Object)this).blockPosition()), null, ((Chicken)(Object)this).level().getMoonBrightness());
        }

        // Update all players in the event that this is from modifying entity data with a command
        // This should be fine since the packet is so small anyways
        MinecraftServer server = ((Entity)(Object)this).getServer();
        if (server != null) {
            MMVPacketHandler.sendToAllClients(new S2CRespondVariantPacket(
                    ((Entity)(Object)this).getId(),
                    variant.getIdentifier().toString()
            ));
        }
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData, CompoundTag entityNbt, CallbackInfoReturnable<SpawnGroupData> cir) {
        variant = Variants.getRandomVariant(EntityType.CHICKEN, world.getRandom().nextLong(), world.getBiome(((Chicken)(Object)this).blockPosition()), null, world.getMoonBrightness());
    }
}
