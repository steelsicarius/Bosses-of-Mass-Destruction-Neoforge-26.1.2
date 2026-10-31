package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendParticleS2CPacket;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VoidLilyBlockEntity extends BlockEntity {
    private Vec3 structureDirection = null;

    public VoidLilyBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMDBlockEntities.VOID_LILY_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        if (tag.child("dirX").isPresent())
            structureDirection = new Vec3(tag.getDoubleOr("dirX", 0), tag.getDoubleOr("dirY", 0), tag.getDoubleOr("dirZ", 0));

        super.loadAdditional(tag);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput tag) {
        Vec3 dir = structureDirection;
        if (dir != null){
            tag.putDouble("dirX", dir.x);
            tag.putDouble("dirY", dir.y);
            tag.putDouble("dirZ", dir.z);
        }
        super.saveAdditional(tag);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VoidLilyBlockEntity entity){
        if (RandomUtils.range(0, 30) == 0 && level instanceof ServerLevel serverLevel) {
            Vec3 direction = entity.structureDirection;
            if (direction == null)
                setNearestStructureDirection(serverLevel, pos, entity);

            if (direction != null)
                Dispatcher.sendToClientsLoadingPos(new SendParticleS2CPacket(pos, direction), serverLevel, VecUtils.asVec3(pos));
        }
    }

    private static void setNearestStructureDirection(ServerLevel level, BlockPos pos, VoidLilyBlockEntity entity){
        BlockPos blockPos = level.findNearestMapStructure(BMDStructures.VOID_LILY_STRUCTURE_KEY, pos, 50, false);
        if (blockPos != null)
            entity.structureDirection = VecUtils.asVec3(new BlockPos(blockPos.getX(), -64, blockPos.getZ()).subtract(pos)).normalize();
        else
            entity.structureDirection = VecUtils.yAxis;
    }
}
