package com.cerbon.bosses_of_mass_destruction.client;

import com.cerbon.bosses_of_mass_destruction.packet.custom.BlindnessS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ChangeHitboxS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ChargedEnderPearlS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.HealS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ObsidilithReviveS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.PlaceS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendDeltaMovementS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendParticleS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendVec3S2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SpikeS2CPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.VoidBlossomReviveS2CPacket;
import com.cerbon.bosses_of_mass_destruction.client.VoidBlossomClient;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEffectHandler;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.util.VecId;
import com.cerbon.cerbons_api.api.network.data.PacketContext;
import com.cerbon.cerbons_api.api.network.data.Side;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.phys.Vec3;

/** Client-side handlers for BOMD S2C packets. Assigned on client setup. */
public class BMDS2CPacketHandlers {

    public static void assign() {
        BlindnessS2CPacket.clientHandler = BMDS2CPacketHandlers::handleBlindness;
        ChangeHitboxS2CPacket.clientHandler = BMDS2CPacketHandlers::handleChangeHitbox;
        ChargedEnderPearlS2CPacket.clientHandler = BMDS2CPacketHandlers::handleChargedEnderPearl;
        HealS2CPacket.clientHandler = BMDS2CPacketHandlers::handleHeal;
        ObsidilithReviveS2CPacket.clientHandler = BMDS2CPacketHandlers::handleObsidilithRevive;
        PlaceS2CPacket.clientHandler = BMDS2CPacketHandlers::handlePlace;
        SendDeltaMovementS2CPacket.clientHandler = BMDS2CPacketHandlers::handleSendDeltaMovement;
        SendParticleS2CPacket.clientHandler = BMDS2CPacketHandlers::handleSendParticle;
        SendVec3S2CPacket.clientHandler = BMDS2CPacketHandlers::handleSendVec3;
        SpikeS2CPacket.clientHandler = BMDS2CPacketHandlers::handleSpike;
        VoidBlossomReviveS2CPacket.clientHandler = BMDS2CPacketHandlers::handleVoidBlossomRevive;
    }

    private static void handleBlindness(PacketContext<BlindnessS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        BlindnessS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> {
            Entity entity = level.getEntity(packet.entityId);
            List<Player> players = Arrays.stream(packet.playerIds)
                    .mapToObj(level::getEntity)
                    .filter(id -> id instanceof Player)
                    .map(id -> (Player) id)
                    .toList();

            if (entity instanceof GauntletEntity gauntletEntity)
                gauntletEntity.clientBlindnessHandler.handlePlayerEffects(players);
        });
    }

    private static void handleChangeHitbox(PacketContext<ChangeHitboxS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ChangeHitboxS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> {
            Entity entity = level.getEntity(packet.entityId);

            if (entity instanceof GauntletEntity gauntletEntity) {
                if (packet.open) gauntletEntity.hitboxHelper.setOpenHandHitbox();
                else gauntletEntity.hitboxHelper.setClosedFistHitbox();
            }
        });
    }

    private static void handleChargedEnderPearl(PacketContext<ChargedEnderPearlS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ChargedEnderPearlS2CPacket packet = ctx.message();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> ChargedEnderPearlEntity.handlePearlImpact(packet.pos));
    }

    private static void handleHeal(PacketContext<HealS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        HealS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> VoidBlossomClient.handleVoidBlossomHeal(level, packet.source, packet.dest));
    }

    private static void handleObsidilithRevive(PacketContext<ObsidilithReviveS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        ObsidilithReviveS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> ObsidilithEffectHandler.spawnPillarParticles(packet.pos, CapabilityUtils.getLevelEventScheduler(level)));
    }

    private static void handlePlace(PacketContext<PlaceS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        PlaceS2CPacket packet = ctx.message();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> VoidBlossomClient.handleVoidBlossomPlace(client.level, packet.pos));
    }

    private static void handleSendDeltaMovement(PacketContext<SendDeltaMovementS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendDeltaMovementS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        LocalPlayer localPlayer = client.player;
        if (localPlayer == null) return;

        client.execute(() -> localPlayer.setDeltaMovement(packet.deltaMovement));
    }

    private static void handleSendParticle(PacketContext<SendParticleS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendParticleS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> VoidLilyClient.spawnVoidLilyParticles(level, VecUtils.asVec3(packet.pos), packet.dir));
    }

    private static void handleSendVec3(PacketContext<SendVec3S2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SendVec3S2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        VecId vecId = VecId.fromInt(packet.id);

        if (level == null) return;
        if (vecId == null) return;

        client.execute(() -> BrimstoneNectarEffectsClient.handlePacket(vecId, level, packet.pos));
    }

    private static void handleSpike(PacketContext<SpikeS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        SpikeS2CPacket packet = ctx.message();
        Minecraft client = Minecraft.getInstance();

        client.execute(() -> {
            ClientLevel clientLevel = client.level;
            if (clientLevel == null) return;
            Entity entity = clientLevel.getEntity(packet.id);

            if (entity instanceof VoidBlossomEntity voidBlossomEntity)
                packet.spikePositions.forEach(voidBlossomEntity.clientSpikeHandler::addSpike);
        });
    }

    private static void handleVoidBlossomRevive(PacketContext<VoidBlossomReviveS2CPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) return;

        VoidBlossomReviveS2CPacket packet = ctx.message();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;

        client.execute(() -> ReviveParticlesClient.handleVoidBlossomRevive(packet.pos, level));
    }
}
