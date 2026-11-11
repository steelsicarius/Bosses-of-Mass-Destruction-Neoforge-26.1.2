package com.cerbon.bosses_of_mass_destruction.neoforge.event;

// Automated in-game render test (opt-in dev tool, off by default).
// Activated only with -Dbmd.renderTest=true.
// Server side: spawns frozen bosses in front of the player after login.
// Client side: takes periodic framebuffer screenshots.

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = BMDConstants.MOD_ID)
public final class BMDRenderTestNeo {
    private static final boolean ENABLED = System.getProperty("bmd.renderTest") != null;
    private static final Logger LOG = LogUtils.getLogger();

    // ---------------- SERVER ----------------
    private static int serverCountdown = -1;
    private static ServerPlayer pendingPlayer;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ENABLED || !(event.getEntity() instanceof ServerPlayer player)) return;
        serverCountdown = 60; // spawn ~3s after login
        pendingPlayer = player;
    }

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        if (!ENABLED || serverCountdown < 0) return;
        if (--serverCountdown == 0) {
            ServerPlayer player = pendingPlayer;
            pendingPlayer = null;
            serverCountdown = -1;
            if (player != null && player.isAlive()) spawnBosses(player);
        }
    }

    private static void spawnBosses(ServerPlayer player) {
        ServerLevel level = player.level();
        Vec3 pos = player.position();
        Vec3 fwd = Vec3.directionFromRotation(0.0F, player.getYRot());
        Vec3 side = new Vec3(-fwd.z, 0, fwd.x);
        Vec3 base = pos.add(fwd.scale(6.0));
        spawn(level, BMDEntities.LICH.get(), base.add(side.scale(-4.5)).add(0, 3, 0), 180.0F);
        spawn(level, BMDEntities.OBSIDILITH.get(), base.add(side.scale(-1.5)).add(0, 1, 0), 180.0F);
        spawn(level, BMDEntities.GAUNTLET.get(), base.add(side.scale(1.5)).add(0, 1, 0), 180.0F);
        spawn(level, BMDEntities.VOID_BLOSSOM.get(), base.add(side.scale(4.5)).add(0, 1, 0), 180.0F);
    }

    private static void spawn(ServerLevel level, EntityType<? extends Mob> type, Vec3 pos, float yRot) {
        Mob mob = type.create(level, EntitySpawnReason.COMMAND);
        if (mob == null) {
            LOG.error("[BMD-TEST] failed to create {}", BuiltInRegistries.ENTITY_TYPE.getKey(type));
            return;
        }
        mob.snapTo(pos.x, pos.y, pos.z, yRot, 0.0F);
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setPersistenceRequired();
        level.addFreshEntity(mob);
        LOG.info("[BMD-TEST] spawned {} at {}", BuiltInRegistries.ENTITY_TYPE.getKey(type), pos);
    }

    // ---------------- CLIENT ----------------
    private static int ticksUntilShot = -1;
    private static int shotIndex = 0;

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!ENABLED) return;
        ticksUntilShot = 140;
        LOG.info("[BMD-TEST] logged in, screenshots scheduled");
    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        if (!ENABLED || ticksUntilShot < 0) return;
        if (--ticksUntilShot != 0) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        shotIndex++;
        LOG.info("[BMD-TEST] screenshot #{} pos={}", shotIndex, mc.player.position());
        Screenshot.grab(mc.gameDirectory, "bmd_dbg_" + shotIndex, mc.getMainRenderTarget(), 1,
                component -> LOG.info("[BMD-TEST] {}", component.getString()));
        if (shotIndex < 4) ticksUntilShot = 100;
    }

    private BMDRenderTestNeo() {}
}
