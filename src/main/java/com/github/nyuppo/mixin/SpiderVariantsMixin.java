package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.networking.MMVPacketHandler;
import com.github.nyuppo.networking.S2CRespondVariantPacket;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.class)
public abstract class SpiderVariantsMixin extends MobVariantsMixin {
    private MobVariant variant = Variants.getDefaultVariant(EntityType.SPIDER);

    @Override
    protected void onAddAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.getString(MoreMobVariants.NBT_KEY).isEmpty()) {
            if (nbt.getString(MoreMobVariants.NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.SPIDER, new ResourceLocation(nbt.getString(MoreMobVariants.NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.SPIDER, MoreMobVariants.id(nbt.getString(MoreMobVariants.NBT_KEY)));
            }
        } else {
            variant = Variants.getRandomVariant(EntityType.SPIDER, ((Spider)(Object)this).level.getRandom().nextLong(), ((Spider)(Object)this).level.getBiome(((Spider)(Object)this).blockPosition()), null, ((Spider)(Object)this).level.getMoonBrightness());
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
        variant = Variants.getRandomVariant(EntityType.SPIDER, world.getRandom().nextLong(), world.getBiome(((Spider)(Object)this).blockPosition()), null, world.getMoonBrightness());
    }
}
