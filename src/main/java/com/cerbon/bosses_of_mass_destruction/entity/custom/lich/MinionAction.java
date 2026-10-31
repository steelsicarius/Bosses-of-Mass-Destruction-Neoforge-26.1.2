package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.spawn.*;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.RegistryUtils;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class MinionAction implements IActionWithCooldown {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    public static final int minionSummonCooldown = 80;
    public static final int minionSummonParticleDelay = 10;
    public static final int minionSummonDelay = 40;
    public static final int minionRuneToMinionSpawnDelay = 40;
    public static final String summonId = "minecraft:phantom";

    public static final EntityType<?> summonEntityType = RegistryUtils.getEntityTypeByKey(summonId);

    public MinionAction(LichEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        Entity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return minionSummonCooldown;
        performMinionSummon((ServerPlayer) target);
        return minionSummonCooldown;
    }

    private void performMinionSummon(ServerPlayer target) {
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> beginSummonSingleMob(target),
                        minionSummonDelay,
                        1,
                        shouldCancel
                )
        );
    }

    void beginSummonSingleMob(ServerPlayer target){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", summonId);
        ServerLevel serverLevel = (ServerLevel) target.level();
        SimpleMobSpawner mobSpawner = new SimpleMobSpawner(serverLevel);
        CompoundTagEntityProvider entityProvider = new CompoundTagEntityProvider(compoundTag, serverLevel);
        MobEntitySpawnPredicate spawnPredicate = new MobEntitySpawnPredicate(target.level());
        IMobSpawner summonCircleBeforeSpawn = (pos, summon) -> {
            ParticleUtils.spawnParticle(serverLevel, BMDParticles.MAGIC_CIRCLE.get(), pos, Vec3.ZERO, 0, 0.0);
            SoundUtils.playSound(serverLevel, pos, BMDSounds.MINION_RUNE.get(), SoundSource.HOSTILE, 1.0f, 64.0, null);
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                mobSpawner.spawn(pos, summon);
                                entity.playSound(BMDSounds.MINION_SUMMON.get(), 0.7f, 1.0f);
                            },
                            minionRuneToMinionSpawnDelay,
                            1,
                            shouldCancel
                    )
            );
        };

        new MobPlacementLogic(
                new RangedSpawnPosition(target.position(), 4.0, 8.0),
                entityProvider,
                spawnPredicate,
                summonCircleBeforeSpawn
        ).tryPlacement(30);
    }
}
