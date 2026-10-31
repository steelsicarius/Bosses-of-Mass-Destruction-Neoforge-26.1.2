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
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;

public class BlindnessS2CPacket {
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "blindness_s2c_packet");
    public static final StreamCodec<FriendlyByteBuf, BlindnessS2CPacket> STREAM_CODEC = StreamCodec.ofMember(BlindnessS2CPacket::write, BlindnessS2CPacket::new);

    public final int entityId;
    public final int[] playerIds;

    public BlindnessS2CPacket(int entityId, int[] playerIds) {
        this.entityId = entityId;
        this.playerIds = playerIds;
    }

    public BlindnessS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.playerIds = buf.readVarIntArray();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeVarIntArray(playerIds);
    }

    public static Consumer<PacketContext<BlindnessS2CPacket>> clientHandler = ctx -> {};

    public static void handle(PacketContext<BlindnessS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        clientHandler.accept(ctx);
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }
}