package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class SimpleLivingGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T, EntityRenderState> {
    private final IRenderLight<T> brightness;
    private final IBoneLight iBoneLight;
    private final IRenderer<T> renderer;
    private final IRendererWithModel renderWithModel;
    private final IOverlayOverride overlayOverride;
    private final boolean deathRotation;

    public SimpleLivingGeoRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, IRenderLight<T> brightness, IBoneLight iBoneLight, IRenderer<T> renderer, IRendererWithModel renderWithModel, IOverlayOverride overlayOverride, boolean deathRotation) {
        super(renderManager, model);
        this.brightness = brightness;
        this.iBoneLight = iBoneLight;
        this.renderer = renderer;
        this.renderWithModel = renderWithModel;
        this.overlayOverride = overlayOverride;
        this.deathRotation = deathRotation;
    }

    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector bufferSource, CameraRenderState camera) {
        // Stub - new render state system not yet implemented
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity, pos) : super.getBlockLightLevel(entity, pos);
    }
}
