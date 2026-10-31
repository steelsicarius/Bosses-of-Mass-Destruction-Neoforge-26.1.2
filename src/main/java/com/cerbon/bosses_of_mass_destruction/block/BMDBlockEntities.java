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
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BMDBlockEntities {
    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCKS_ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BMDConstants.MOD_ID);

    public static final RegistryEntry<BlockEntityType<MobWardBlockEntity>> MOB_WARD = BLOCKS_ENTITIES.register("mob_ward", () ->
            BlockEntityType.Builder.of(MobWardBlockEntity::new, BMDBlocks.MOB_WARD.get()).build(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "mob_ward"))));

    public static final RegistryEntry<BlockEntityType<MonolithBlockEntity>> MONOLITH_BLOCK_ENTITY = BLOCKS_ENTITIES.register("monolith_block", () ->
            BlockEntityType.Builder.of(MonolithBlockEntity::new, BMDBlocks.MONOLITH_BLOCK.get()).build(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "monolith_block"))));

    public static final RegistryEntry<BlockEntityType<LevitationBlockEntity>> LEVITATION_BLOCK_ENTITY = BLOCKS_ENTITIES.register("levitation_block", () ->
            BlockEntityType.Builder.of(LevitationBlockEntity::new, BMDBlocks.LEVITATION_BLOCK.get()).build(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "levitation_block"))));

    public static final RegistryEntry<BlockEntityType<VoidBlossomSummonBlockEntity>> VOID_BLOSSOM_SUMMON_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_blossom_block", () ->
            BlockEntityType.Builder.of(VoidBlossomSummonBlockEntity::new, BMDBlocks.VOID_BLOSSOM_SUMMON_BLOCK.get()).build(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom_block"))));

    public static final RegistryEntry<BlockEntityType<VoidLilyBlockEntity>> VOID_LILY_BLOCK_ENTITY = BLOCKS_ENTITIES.register("void_lily", () ->
            BlockEntityType.Builder.of(VoidLilyBlockEntity::new, BMDBlocks.VOID_LILY_BLOCK.get()).build(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_lily"))));

    
    public static void initClient() {
        BlockEntityRenderers.register(LEVITATION_BLOCK_ENTITY.get(), context ->
                new BMDBlockEntityRenderer<>(
                        new GeoModel<>(
                                entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "geo/levitation_block.geo.json"),
                                entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/block/levitation_block.png"),
                                Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "animations/levitation_block.animation.json"),
                                (animatable, data, geoModel) -> {},
                                RenderTypes::entityCutout
                        ),
                        (bone, packedLight) -> IBoneLight.fullbright
                )
        );
    }

    public static void register() {
        BLOCKS_ENTITIES.register();
    }
}
