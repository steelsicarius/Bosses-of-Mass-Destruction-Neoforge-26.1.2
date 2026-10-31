package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.packet.custom.VoidBlossomReviveS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.BossBlockDecorator;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

public class VoidBlossomStructureRepair implements StructureRepair{

    @Override
    public ResourceKey<Structure> associatedStructure() {
        return BMDStructures.VOID_BLOSSOM_STRUCTURE_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos offset = getCenterSpawn(structureStart, level);
        Dispatcher.sendToClientsLoadingPos(new VoidBlossomReviveS2CPacket(VecUtils.asVec3(offset)), level, VecUtils.asVec3(offset));

        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> level.setBlockAndUpdate(offset, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get().defaultBlockState()),
                        60
                )
        );
    }

    @Override
    public boolean shouldRepairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos centerPos = getCenterSpawn(structureStart, level);
        return level.getEntities(BMDEntities.VOID_BLOSSOM.get(), voidBlossomEntity -> voidBlossomEntity.distanceToSqr(VecUtils.asVec3(centerPos)) < 100 * 100).isEmpty();
    }

    private BlockPos getCenterSpawn(StructureStart structureStart, ServerLevel level){
        BlockPos pos = structureStart.getBoundingBox().getCenter();
        return BossBlockDecorator.bossBlockOffset(pos, level.getMinY());
    }
}
