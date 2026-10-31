package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.packet.custom.SendVec3S2CPacket;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.StructureRepair;
import com.cerbon.bosses_of_mass_destruction.util.VecId;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BrimstoneNectarItem extends Item {
    private final List<StructureRepair> structureRepairs;

    public BrimstoneNectarItem(Properties properties, List<StructureRepair> structureRepairs) {
        super(properties);
        this.structureRepairs = structureRepairs;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag) {
        tooltipConsumer.accept(Component.translatable("item.bosses_of_mass_destruction.brimstone_nectar.tooltip").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipConsumer, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player user, @NotNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel){
            BlockPos usePos = user.blockPosition();
            List<StructureRepair> structuresToRepair = findStructuresToRepair(serverLevel, usePos);

            if (!structuresToRepair.isEmpty()){
                scheduleStructureRepair(serverLevel, structuresToRepair, usePos);
                playSound(serverLevel, user);
                user.getCooldowns().addCooldown(itemStack, 80);
                Dispatcher.sendToClientsLoadingPos(new SendVec3S2CPacket(user.position(), VecId.BrimstoneParticleEffect.ordinal()), serverLevel, user.position());

                if (!user.getAbilities().instabuild)
                    itemStack.shrink(1);

                user.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private List<StructureRepair> findStructuresToRepair(ServerLevel level, BlockPos usePos){
        return structureRepairs.stream().filter(structure -> {
            StructureStart structureStart = getStructureStart(level, usePos, structure);
            return structureStart.isValid() && structure.shouldRepairStructure(level, structureStart);
        }).toList();
    }

    private void scheduleStructureRepair(ServerLevel level, List<StructureRepair> structureToRepair, BlockPos usePos){
        CapabilityUtils.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> structureToRepair.forEach(structure -> structure.repairStructure(level, getStructureStart(level, usePos, structure))),
                        30
        ));
    }

    private StructureStart getStructureStart(ServerLevel level, BlockPos blockPos, StructureRepair it){
        return level.structureManager().getStructureAt(blockPos, level.structureManager().registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(it.associatedStructure()).value());
    }

    private void playSound(Level level, Player user){
        level.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                BMDSounds.BRIMSTONE.get(),
                SoundSource.NEUTRAL,
                1.0f,
                SoundUtils.randomPitch(level.getRandom())
        );
    }
}
