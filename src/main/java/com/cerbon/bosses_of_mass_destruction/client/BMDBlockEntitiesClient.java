package com.cerbon.bosses_of_mass_destruction.client;

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

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;

/** Client-only registration. Loaded only on the client (see BMDClientEventsNeo). */
public class BMDBlockEntitiesClient {

    public static void init() {
        BlockEntityRenderers.register(BMDBlockEntities.LEVITATION_BLOCK_ENTITY.get(), context ->
                new BMDBlockEntityRenderer<LevitationBlockEntity>(
                        context,
                        new GeoModel<LevitationBlockEntity>(
                                entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "geckolib/models/levitation_block.geo.json"),
                                entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/block/levitation_block.png"),
                                Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "geckolib/animations/levitation_block.animation.json"),
                                (animatable, data, geoModel) -> {}
                        ),
                        (bone, packedLight) -> IBoneLight.fullbright
                )
        );
    }
}
