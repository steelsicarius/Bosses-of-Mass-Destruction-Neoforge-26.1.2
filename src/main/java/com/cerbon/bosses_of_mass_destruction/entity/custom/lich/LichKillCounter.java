package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.config.mob.LichConfig;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.cerbons_api.api.static_utilities.ParticleUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LichKillCounter {
    private final LichConfig.SummonMechanic config;
    private final List<Holder<EntityType<?>>> countedEntities;

    public LichKillCounter(LichConfig.SummonMechanic config) {
        this.config = config;
        this.countedEntities = config.entitiesThatCountToSummonCounter != null
                ? config.entitiesThatCountToSummonCounter.stream()
                .map(string -> BuiltInRegistries.ENTITY_TYPE.get(Identifier.tryParse(string)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
                : java.util.Collections.<Holder<EntityType<?>>>emptyList();
    }

    public void afterKilledOtherEntity(Entity entity, LivingEntity killedEntity) {
        if (entity instanceof ServerPlayer && countedEntities.contains(killedEntity.getType())) {
            int entitiesKilled = getUndeadKilled((ServerPlayer) entity);

            if (entitiesKilled > 0 && entitiesKilled % config.numEntitiesKilledToDropSoulStar == 0) {
                ServerLevel serverLevel = (ServerLevel) ((ServerPlayer) entity).level();
                ParticleUtils.spawnParticle(serverLevel, BMDParticles.SOUL_FLAME.get(), killedEntity.position().add(VecUtils.yAxis), VecUtils.unit, 15, 0.0);
                killedEntity.spawnAtLocation(serverLevel, new ItemStack(BMDItems.SOUL_STAR.get()));
            }
        }
    }

    private int getUndeadKilled(ServerPlayer entity) {
        return countedEntities.stream()
                .mapToInt(entityType -> entity.getStats().getValue(Stats.ENTITY_KILLED.get(entityType.value())))
                .sum();
    }
}
