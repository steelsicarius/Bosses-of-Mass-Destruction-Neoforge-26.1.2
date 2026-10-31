package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.block.custom.*;
import com.cerbon.bosses_of_mass_destruction.client.render.BMDBlockEntityRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BMDBlockEntities {
    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCKS_ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BMDConstants.MOD_ID);

    public static final RegistryEntry<BlockEntityType<MobWardBlockEntity>> MOB_WARD = BLOCKS_ENTITIES.register("mob_ward", () ->
            new BlockEntityType<>(MobWardBlockEntity::new, BMDBlocks.MOB_WARD.get()));

    public static final RegistryEntry<BlockEntityType<MonolithBlockEntity>> MONOLITH_BLOCK_ENTITY = BLOCKS_ENTITIES.register("monolith_block", () ->
            new BlockEntityType<>(MonolithBlockEntity::new, BMDBlocks.MONOLITH_BLOCK.get()));

    public static final RegistryEntry<BlockEntityType<LevitationBlockEntity>> LEVITATION_BLOCK_ENTITY = BLOCKS_ENTITIES.register("levitation_block", () ->
            new BlockEntityType<>(LevitationBlockEntity::new, BMDBlocks.LEVITATION_BLOCK.get()));

    public static final RegistryEntry<BlockEntityType<VoidBlossomSummonBlockEntity>> VOID_BLOSSOM_SUMMON_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_blossom_block", () ->
            new BlockEntityType<>(VoidBlossomSummonBlockEntity::new, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get()));

    public static final RegistryEntry<BlockEntityType<VoidLilyBlockEntity>> VOID_LILY_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_lily", () ->
            new BlockEntityType<>(VoidLilyBlockEntity::new, BMDBlocks.VOID_LILY_BLOCK.get()));

    
    public static void register() {
        BLOCKS_ENTITIES.register();
    }
}
