package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class GauntletBlindnessIndicatorParticles implements IEntityEventHandler {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;

    // CERBONs API ClientParticleBuilder depends on SimpleParticle which extends
    // TextureSheetParticle (removed in NeoForge 26.1.2). Particle builders removed.

    public GauntletBlindnessIndicatorParticles(GauntletEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    public void handlePlayerEffects(List<Player> players){
        // Stubbed - particle effects pending API migration
    }

    private void spawnRotatingParticles(Entity player){
        // Stubbed - particle effects pending API migration
    }

    private Vec3 calculatePositions(Entity player, int i, int age, int startingRotation){
        return player.position().add(VecUtils.yAxis.scale(i * 0.1)).add(
                VecUtils.xAxis.yRot(
                        (float) Math.toRadians(age * 2 + startingRotation)
                )
        );
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == GauntletAttacks.blindnessAttack){
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                // Stubbed - particle effects pending API migration
                            },
                            0,
                            10,
                            () -> false
                    )
            );
        }

    }
}
