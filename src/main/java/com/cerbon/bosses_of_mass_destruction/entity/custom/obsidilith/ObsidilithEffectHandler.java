package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ObsidilithEffectHandler {
    private final LivingEntity entity;
    private final EventScheduler eventScheduler;

    // CERBONs API ClientParticleBuilder depends on SimpleParticle which extends
    // TextureSheetParticle (removed in NeoForge 26.1.2). Particle builders removed.

    public ObsidilithEffectHandler(LivingEntity entity, EventScheduler eventScheduler){
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    public void handleStatus(byte status){
        switch (status){
            case ObsidilithUtils.burstAttackStatus -> burstEffect();
            case ObsidilithUtils.waveAttackStatus -> waveEffect();
            case ObsidilithUtils.spikeAttackStatus -> spikeEffect();
            case ObsidilithUtils.anvilAttackStatus -> anvilEffect();
            case ObsidilithUtils.deathStatus -> deathEffect();
        }
    }

    private void burstEffect(){
        // Stubbed - particle effects pending API migration
        Vec3 entityPos = MobUtils.eyePos(entity);
        for (int i = 0; i <= 5; i++){
            Vec3 pos = entityPos.add(RandomUtils.randVec().normalize().scale(3.0));
            entity.level().addParticle(ParticleTypes.ENCHANT, pos.x(), pos.y(), pos.z(), 0, 0, 0);
        }
    }

    private void waveEffect(){
        // Stubbed - particle effects pending API migration
        Vec3 entityPos = entity.position();
        for (int i = 0; i <= 5; i++){
            Vec3 pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(3.0));
            entity.level().addParticle(new net.minecraft.core.particles.DustParticleOptions(0xFF0000, 1.0f), pos.x(), pos.y(), pos.z(), 0, 0, 0);
        }
    }

    private void spikeEffect(){
        // Stubbed - particle effects pending API migration
        Vec3 entityPos = entity.position();
        for (int i = 0; i <= 5; i++){
            Vec3 pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(3.0));
            entity.level().addParticle(new net.minecraft.core.particles.DustParticleOptions(0x0080FF, 1.0f), pos.x(), pos.y(), pos.z(), 0, 0, 0);
        }
    }

    private void anvilEffect(){
        // Stubbed - particle effects pending API migration
        Vec3 entityPos = MobUtils.eyePos(entity);
        for (int i = 0; i <= 5; i++){
            Vec3 pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().multiply(3.0, 3.0, 3.0));
            entity.level().addParticle(new net.minecraft.core.particles.DustParticleOptions(0x800080, 1.0f), pos.x(), pos.y(), pos.z(), 0, 0, 0);
        }
    }

    private void deathEffect(){
        Vec3 entityPos = VecUtils.asVec3(entity.blockPosition()).add(0.5, 0.5, 0.5);
        spawnPillarParticles(entityPos, entity.level(), eventScheduler);
    }

    public static void spawnPillarParticles(Vec3 entityPos, net.minecraft.world.level.Level level, EventScheduler eventScheduler){
        for (int i = 0; i <= ObsidilithUtils.deathPillarHeight; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> MathUtils.circleCallback(3.0, 30, VecUtils.yAxis, vec3 ->
                                    level.addParticle(new net.minecraft.core.particles.DustParticleOptions(0xFF00FF, 1.0f), entityPos.x() + vec3.x(), entityPos.y() + i1, entityPos.z() + vec3.z(), 0, 0, 0)),
                            i * ObsidilithUtils.ticksBetweenPillarLayer
                    )
            );
        }
    }
}
