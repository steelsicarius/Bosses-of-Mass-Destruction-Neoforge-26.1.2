package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.cerbons_api.api.network.data.PacketContext;

import java.util.function.Consumer;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class ChangeHitboxS2CPacket {
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "change_hitbox_s2c_packet");
    public static final StreamCodec<FriendlyByteBuf, ChangeHitboxS2CPacket> STREAM_CODEC = StreamCodec.ofMember(ChangeHitboxS2CPacket::write, ChangeHitboxS2CPacket::new);

    public final int entityId;
    public final boolean open;

    public ChangeHitboxS2CPacket(int entityId, boolean open) {
        this.entityId = entityId;
        this.open = open;
    }

    public ChangeHitboxS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.open = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(open);
    }

    public static Consumer<PacketContext<ChangeHitboxS2CPacket>> clientHandler = ctx -> {};

    public static void handle(PacketContext<ChangeHitboxS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        clientHandler.accept(ctx);
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }
}