package com.github.nyuppo.networking;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CRespondVariantPacketHandler {
    public static void handlePacket(S2CRespondVariantPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(msg.getId());
            if (entity != null) {
                CompoundTag nbt = new CompoundTag();
                entity.saveWithoutId(nbt);

                nbt.putString(MoreMobVariants.NBT_KEY, msg.getVariant());

                if (entity instanceof Pig && msg.getResponseType() == 1) {
                    nbt.putBoolean(MoreMobVariants.MUDDY_NBT_KEY, msg.isMuddy());
                    nbt.putInt(MoreMobVariants.MUDDY_TIMEOUT_NBT_KEY, msg.getMuddyTimeout());
                } else if (entity instanceof Sheep && msg.getResponseType() == 2) {
                    nbt.putString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY, msg.getHornColour());
                } else if (entity instanceof TamableAnimal && msg.getResponseType() == 3) {
                    nbt.putBoolean("Sitting", msg.isSitting());
                }

                entity.load(nbt);
            }
        }
    }
}
