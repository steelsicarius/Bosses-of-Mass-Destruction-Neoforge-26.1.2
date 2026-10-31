package com.cerbon.bosses_of_mass_destruction.client;

import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

/** Client-only particle handlers for the void lily block entity. */
public class VoidLilyClient {

    public static void spawnVoidLilyParticles(ClientLevel level, Vec3 pos, Vec3 dir){
        Vec3 streakPos = pos.add(new Vec3(0.5, 0.7, 0.5)).add(RandomUtils.randVec().scale(0.5));
        Vec3 right = dir.cross(VecUtils.yAxis).normalize();
        double sinCurve = RandomUtils.range(8.0, 11.0) * RandomUtils.randSign();
        double curveLength = RandomUtils.range(1.2, 1.6);
        int particleAmount = RandomUtils.range(7, 10);

        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 particlePos = streakPos.add(RandomUtils.randVec().scale(0.05));
                            PollenParticles.pollenParticles.continuousPosition(p -> {
                                float age = p.ageRatio;
                                Vec3 forward = dir.scale((double) age * curveLength);
                                Vec3 sinSide = right.scale(Math.sin(age * sinCurve) * 0.1);
                                return particlePos.add(forward).add(sinSide);
                            }).build(particlePos, Vec3.ZERO);
                        },
                        0,
                        particleAmount,
                        () -> false
                )
        );
    }

    private static class Particles {
         private static final ClientParticleBuilder pollenParticles = new ClientParticleBuilder(BMDParticles.POLLEN.get())
                .scale(age -> (float) (Math.sin(age * (float) Math.PI) * 0.04f))
                .age(30);
    }
    private static class PollenParticles {
        private static final ClientParticleBuilder pollenParticles = new ClientParticleBuilder(BMDParticles.POLLEN.get())
                .scale(age -> (float) (Math.sin(age * (float) Math.PI) * 0.04f))
                .age(30);
    }
}
