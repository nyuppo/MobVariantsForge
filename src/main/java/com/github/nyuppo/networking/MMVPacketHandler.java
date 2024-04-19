package com.github.nyuppo.networking;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class MMVPacketHandler {
    private static final int PROTOCOL_VERSION = 1;
    private static SimpleChannel INSTANCE;

    public static void registerPackets() {
        INSTANCE = ChannelBuilder
                .named(MoreMobVariants.id("main"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .acceptedVersions((s, v) -> true)
                .simpleChannel();

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
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(msg, PacketDistributor.ALL.noArg());
    }

    public static void sendToClient(Object msg, ServerPlayer serverPlayer) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(serverPlayer));
    }
}
