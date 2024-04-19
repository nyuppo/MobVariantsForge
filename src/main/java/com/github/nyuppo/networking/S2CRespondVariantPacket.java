package com.github.nyuppo.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class S2CRespondVariantPacket {
    private final int id;
    private final String variant;
    private byte responseType; // 0 = default, 1 = pig, 2 = sheep, 3 = tameable

    // Pigs
    private boolean isMuddy;
    private int muddyTimeout;

    // Sheep
    private String hornColour;

    // Tameables
    private boolean sitting;

    public S2CRespondVariantPacket(int id, String variant) {
        this.id = id;
        this.variant = variant;
        this.responseType = 0;

        this.isMuddy = false;
        this.muddyTimeout = 0;

        this.hornColour = "";

        this.sitting = false;
    }

    public S2CRespondVariantPacket(int id, String variant, boolean isMuddy, int muddyTimeout) {
        this.id = id;
        this.variant = variant;
        this.responseType = 1;

        this.isMuddy = isMuddy;
        this.muddyTimeout = muddyTimeout;

        this.hornColour = "";

        this.sitting = false;
    }

    public S2CRespondVariantPacket(int id, String variant, String hornColour) {
        this.id = id;
        this.variant = variant;
        this.responseType = 2;

        this.isMuddy = false;
        this.muddyTimeout = 0;

        this.hornColour = hornColour;

        this.sitting = false;
    }

    public S2CRespondVariantPacket(int id, boolean sitting, String variant) {
        this.id = id;
        this.variant = variant;
        this.responseType = 3;

        this.isMuddy = false;
        this.muddyTimeout = 0;

        this.hornColour = "";

        this.sitting = sitting;
    }

    public S2CRespondVariantPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readInt();
        this.variant = buffer.readUtf();
        this.responseType = buffer.readByte();

        // Reset before reading
        this.isMuddy = false;
        this.muddyTimeout = 0;
        this.hornColour = "";

        // Handle custom response types
        if (this.responseType == 1) {
            this.isMuddy = buffer.readBoolean();
            this.muddyTimeout = buffer.readInt();
        } else if (this.responseType == 2) {
            this.hornColour = buffer.readUtf();
        } else if (this.responseType == 3) {
            this.sitting = buffer.readBoolean();
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.id);
        buffer.writeUtf(this.variant);
        buffer.writeByte(this.responseType);

        if (this.responseType == 1) {
            buffer.writeBoolean(this.isMuddy);
            buffer.writeInt(this.muddyTimeout);
        } else if (this.responseType == 2) {
            buffer.writeUtf(this.hornColour);
        } else if (this.responseType == 3) {
            buffer.writeBoolean(this.sitting);
        }
    }

    public static void handle(S2CRespondVariantPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> S2CRespondVariantPacketHandler.handlePacket(msg, ctx));
        });
        ctx.get().setPacketHandled(true);
    }

    public int getId() {
        return this.id;
    }

    public String getVariant() {
        return this.variant;
    }

    public byte getResponseType() {
        return this.responseType;
    }

    public boolean isMuddy() {
        return this.isMuddy;
    }

    public int getMuddyTimeout() {
        return this.muddyTimeout;
    }

    public void setPigData(boolean isMuddy, int muddyTimeout) {
        this.responseType = 1;
        this.isMuddy = isMuddy;
        this.muddyTimeout = muddyTimeout;
    }

    public String getHornColour() {
        return this.hornColour;
    }

    public void setSheepData(String hornColour) {
        this.responseType = 2;
        this.hornColour = hornColour;
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }
}
