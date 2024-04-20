package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantSettings;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.networking.MMVPacketHandler;
import com.github.nyuppo.networking.S2CRespondVariantPacket;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
public abstract class PigVariantsMixin extends MobVariantsMixin {
    private MobVariant variant = Variants.getDefaultVariant(EntityType.PIG);
    private boolean isMuddy = false;
    private int muddyTimeLeft = -1;

    @Override
    protected void onAddAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        nbt.putBoolean(MoreMobVariants.MUDDY_NBT_KEY, isMuddy);
        nbt.putInt(MoreMobVariants.MUDDY_TIMEOUT_NBT_KEY, muddyTimeLeft);
    }

    @Override
    protected void onReadAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.getString(MoreMobVariants.NBT_KEY).isEmpty()) {
            if (nbt.getString(MoreMobVariants.NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.PIG, new ResourceLocation(nbt.getString(MoreMobVariants.NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.PIG, MoreMobVariants.id(nbt.getString(MoreMobVariants.NBT_KEY)));
            }
        } else {
            variant = Variants.getRandomVariant(EntityType.PIG, ((Pig)(Object)this).level.getRandom().nextLong(), ((Pig)(Object)this).level.getBiome(((Pig)(Object)this).blockPosition()), null, ((Pig)(Object)this).level.getMoonBrightness());
        }
        isMuddy = nbt.getBoolean(MoreMobVariants.MUDDY_NBT_KEY);
        muddyTimeLeft = nbt.getInt(MoreMobVariants.MUDDY_TIMEOUT_NBT_KEY);

        // Update all players in the event that this is from modifying entity data with a command
        // This should be fine since the packet is so small anyways
        MinecraftServer server = ((Entity)(Object)this).getServer();
        if (server != null) {
            MMVPacketHandler.sendToAllClients(new S2CRespondVariantPacket(
                    ((Entity)(Object)this).getId(),
                    variant.getIdentifier().toString(),
                    isMuddy,
                    muddyTimeLeft
            ));
        }
    }

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData, CompoundTag entityNbt, CallbackInfoReturnable<SpawnGroupData> cir) {
        variant = Variants.getRandomVariant(EntityType.PIG, world.getRandom().nextLong(), world.getBiome(((Pig)(Object)this).blockPosition()), null, world.getMoonBrightness());

        // 2% chance of pig starting muddy if in swamp
        if (world.getBiome(((Pig)(Object)this).blockPosition()).is(BiomeTags.HAS_RUINED_PORTAL_SWAMP) && world.getRandom().nextDouble() < 0.02) {
            isMuddy = true;
        }
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle muddy pigs
        if (VariantSettings.getEnableMuddyPigs()) {
            int muddyPigTimeout = VariantSettings.getMuddyPigTimeout();

            if (muddyTimeLeft == -1) {
                if (((Pig)(Object)this).level.getBlockState(((Pig)(Object)this).blockPosition()).is(MoreMobVariants.PIG_MUD_BLOCKS) || ((Pig)(Object)this).level.getBlockState(((Pig)(Object)this).blockPosition().below()).is(MoreMobVariants.PIG_MUD_BLOCKS)) {
                    isMuddy = true;
                    if (muddyPigTimeout > 0 ) {
                        muddyTimeLeft = 20 * muddyPigTimeout;
                    }
                } else if (((Pig)(Object)this).isInWaterOrRain()) {
                    isMuddy = false;
                    muddyTimeLeft = -1;
                }
            }

            if (muddyPigTimeout > 0 && muddyTimeLeft > 0) {
                muddyTimeLeft--;
                if (muddyTimeLeft == 0) {
                    isMuddy = false;
                    muddyTimeLeft = -1;
                }
            }
        }
    }
}
