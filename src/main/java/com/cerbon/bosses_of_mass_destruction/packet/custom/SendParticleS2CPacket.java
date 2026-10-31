package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.client.VoidLilyClient;
import com.cerbon.cerbons_api.api.network.data.PacketContext;

import java.util.function.Consumer;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.PacketUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.cerbon.cerbons_api.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class SendParticleS2CPacket {
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "send_particle_s2c_packet");
    public static final StreamCodec<FriendlyByteBuf, SendParticleS2CPacket> STREAM_CODEC = StreamCodec.ofMember(SendParticleS2CPacket::write, SendParticleS2CPacket::new);

    public final BlockPos pos;
    public final Vec3 dir;

    public SendParticleS2CPacket(BlockPos pos, Vec3 dir) {
        this.pos = pos;
        this.dir = dir;
    }

    public SendParticleS2CPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.dir = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        PacketUtils.writeVec3(buf, dir);
    }

    public static Consumer<PacketContext<SendParticleS2CPacket>> clientHandler = ctx -> {};

    public static void handle(PacketContext<SendParticleS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        clientHandler.accept(ctx);
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }
}