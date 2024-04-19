package com.github.nyuppo.networking;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SRequestVariantPacket {
    private final UUID uuid;

    public C2SRequestVariantPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public C2SRequestVariantPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
    }

    public static void handle(C2SRequestVariantPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender(); // client that sent packet
            if (sender != null) {
                MinecraftServer server = sender.getServer();
                if (server != null) {
                    Entity entity = server.overworld().getEntity(msg.uuid);

                    // If we couldn't find the mob in the overworld, start checking all other worlds
                    if (entity == null) {
                        for (ServerLevel level : server.getAllLevels()) {
                            Entity entity2 = level.getEntity(msg.uuid);
                            if (entity2 != null) {
                                entity = entity2;
                                break;
                            }
                        }
                    }

                    if (entity != null) {
                        CompoundTag nbt = new CompoundTag();
                        entity.saveWithoutId(nbt);

                        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
                            S2CRespondVariantPacket packet = new S2CRespondVariantPacket(entity.getId(), nbt.getString(MoreMobVariants.NBT_KEY));

                            if (entity instanceof Pig) {
                                packet.setPigData(nbt.getBoolean(MoreMobVariants.MUDDY_NBT_KEY), nbt.getInt(MoreMobVariants.MUDDY_TIMEOUT_NBT_KEY));
                            } else if (entity instanceof Sheep) {
                                packet.setSheepData(nbt.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY));
                            }

                            MMVPacketHandler.sendToClient(packet, sender);
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
