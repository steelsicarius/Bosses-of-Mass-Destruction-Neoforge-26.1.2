package com.cerbon.bosses_of_mass_destruction.client;

import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

/** Client-only revive particle effects (called from S2C packet handlers). */
public class ReviveParticlesClient {

    public static void handleVoidBlossomRevive(Vec3 pos, ClientLevel level) {
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> ParticleUtils.spawnRotatingParticles(
                                new ParticleUtils.RotatingParticles(
                                        pos.add(VecUtils.yAxis.scale(RandomUtils.range(1.0, 10.0))),
                                        Particles.spikeParticleFactory,
                                        1.0,
                                        2.0,
                                        3.0,
                                        4.0
                                )
                        ),
                        0,
                        50,
                        () -> false
                )
        );

        MathUtils.buildBlockCircle(2.3).forEach(vec3 ->
                new ClientParticleBuilder(BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get())
                        .age(60)
                        .build(pos.add(0.0, 0.1, 0.0).add(vec3), Vec3.ZERO));
    }

    private static class Particles {
        private static final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
                .color(Vec3Colors.VOID_PURPLE)
                .colorVariation(0.25)
                .brightness(BMDParticles.FULL_BRIGHT)
                .scale(f -> 0.5f * (1 - f * 0.25f))
                .age(20);
    }
}
