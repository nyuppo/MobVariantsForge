package com.github.nyuppo.networking;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MMVPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel INSTANCE;

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MoreMobVariants.MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                (protocolVersion -> true),
                (protocolVersion -> true)
        );

        INSTANCE.messageBuilder(C2SRequestVariantPacket.class, 0)
                .encoder(C2SRequestVariantPacket::encode)
                .decoder(C2SRequestVariantPacket::new)
                .consumerMainThread(C2SRequestVariantPacket::handle)
                .add();

        INSTANCE.messageBuilder(S2CRespondVariantPacket.class, 1)
                .encoder(S2CRespondVariantPacket::encode)
                .decoder(S2CRespondVariantPacket::new)
                .consumerMainThread(S2CRespondVariantPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void sendToClient(Object msg, ServerPlayer serverPlayer) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }
}
