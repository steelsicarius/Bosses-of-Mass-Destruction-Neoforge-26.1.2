package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityAdapter;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityStats;
import com.cerbon.bosses_of_mass_destruction.packet.custom.HealS2CPacket;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VoidBlossomBlock extends Block {
    public static final int healAnimationDelay = 16;
    private static final int healDelay = 64;
    private final VoxelShape shape = box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public VoidBlossomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.scheduleTick(pos, this, healDelay);
        healNearbyEntities(level, pos);
    }

    private void healNearbyEntities(ServerLevel level, BlockPos pos){
        level.getEntitiesOfClass(VoidBlossomEntity.class, new AABB(pos).inflate(40.0, 20.0, 40.0))
                .forEach(voidBlossom -> {
                    CapabilityUtils.getLevelEventScheduler(level).addEvent(
                            new TimedEvent(
                                    () -> LichUtils.cappedHeal(new EntityAdapter(voidBlossom), new EntityStats(voidBlossom), VoidBlossomEntity.hpMilestones, 10f, voidBlossom::heal),
                                    healAnimationDelay
                            ));

                    Dispatcher.sendToClientsLoadingPos(new HealS2CPacket(VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)), voidBlossom.position().add(VecUtils.yAxis.scale(5.0))), level, voidBlossom.position());
                });
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull LevelReader level, @NotNull ScheduledTickAccess tickAccess, @NotNull BlockPos pos, @NotNull Direction direction, @NotNull BlockPos neighborPos, @NotNull BlockState neighborState, @NotNull RandomSource random) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)){
            return Blocks.AIR.defaultBlockState();
        }else
            return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return canSupportCenter(level, pos, Direction.UP) && !level.isWaterAt(pos);
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        for(int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for(int y = -1; y <= 1; y ++) {
                    if((x != 0 || z != 0)) {
                        BlockPos pos1 = pos.offset(x, y, z);
                        if(level.getBlockState(pos1).getBlock() == BMDBlocks.VINE_WALL.get()) {
                            level.scheduleTick(pos1, BMDBlocks.VINE_WALL.get(), (2 - y) * 20 + RandomUtils.range(0, 19));
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (level.isClientSide()) {
            for (int i = 0; i < 12; i++){
                Vec3 vel = VecUtils.yAxis.scale(RandomUtils.range(0.1, 0.2));
                Vec3 spawnPos = VecUtils.asVec3(pos).add(VecUtils.unit.scale(0.5)).add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(0.5));
                level.addParticle(net.minecraft.core.particles.ParticleTypes.COMPOSTER, spawnPos.x(), spawnPos.y(), spawnPos.z(), vel.x(), vel.y(), vel.z());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
