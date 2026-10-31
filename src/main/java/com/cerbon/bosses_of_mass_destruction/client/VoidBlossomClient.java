package com.cerbon.bosses_of_mass_destruction.client;

import com.cerbon.bosses_of_mass_destruction.block.custom.VoidBlossomBlock;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Client-only particle handlers for the void blossom block. */
public class VoidBlossomClient {

    public static void handleVoidBlossomHeal(ClientLevel level, Vec3 source, Vec3 dest){
        spawnHealParticle(dest, source, level);
        spawnChargeParticle(source, level);
    }

    
    private static void spawnChargeParticle(Vec3 source, ClientLevel level){
        Vec3 particlePos = source.add(VecUtils.yAxis.scale(0.25));
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> level.addParticle(ParticleTypes.HEART, particlePos.x() + RandomUtils.range(-0.2, 0.2), particlePos.y() + RandomUtils.range(-0.2, 0.2), particlePos.z() + RandomUtils.range(-0.2, 0.2), 0, 0, 0),
                        32,
                        32,
                        () -> false
                )
        );
    }

    
    private static void spawnHealParticle(Vec3 dest, Vec3 source, ClientLevel level){
        List<Vec3> particlePositions = new ArrayList<>();
        int numCirclePoints = VoidBlossomBlock.healAnimationDelay;
        List<Vec3> circlePoints = MathUtils.circlePoints(0.5, numCirclePoints, dest.subtract(source).normalize()).stream().toList();
        MathUtils.lineCallback(source, dest, 32, (pos, i) ->
                particlePositions.add(pos.add(circlePoints.get(i % numCirclePoints))));

        AtomicInteger i = new AtomicInteger();
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 pos1 = particlePositions.get(i.get());
                            Vec3 pos2 = particlePositions.get(i.get() + 1);
                            level.addParticle(ParticleTypes.HEART, pos1.x(), pos1.y(), pos1.z(), 0, 0, 0);
                            level.addParticle(ParticleTypes.HEART, pos2.x(), pos2.y(), pos2.z(), 0, 0, 0);
                            i.addAndGet(2);
                        },
                        0,
                        VoidBlossomBlock.healAnimationDelay,
                        () -> false
                )
        );
    }

    public static void handleVoidBlossomPlace(ClientLevel level, Vec3 pos){
        for (int i = 0; i <= 12; i++){
            Vec3 spawnPos = pos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
            Vec3 vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.3));
            level.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, spawnPos.x(), spawnPos.y(), spawnPos.z(), vel.x(), vel.y(), vel.z());
        }
    }
}
