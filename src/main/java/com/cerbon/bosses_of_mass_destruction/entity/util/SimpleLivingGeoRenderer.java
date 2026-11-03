package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;


@SuppressWarnings("unchecked")
public class SimpleLivingGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T, BMDGeoEntityRenderState> {
    /**
     * 26.1.2 render states no longer hold the entity reference; GeckoLib data tickets are the
     * intended way to carry it from {@link #addRenderData} to the render passes.
     * createRenderState(T, Void) is overridden to return {@link BMDGeoEntityRenderState}
     * (GeckoLib's default factory hardcodes a plain LivingEntityRenderState, which does not
     * implement GeoRenderState at compile time).
     */
    private static final DataTicket<Entity> ENTITY_TICKET = DataTicket.create("bmd_entity", Entity.class);

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
    public BMDGeoEntityRenderState createRenderState(T entity, Void unused) {
        return new BMDGeoEntityRenderState();
    }

    @Override
    public void submit(BMDGeoEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector bufferSource, CameraRenderState camera) {
        // The GeckoLib entity renderer implements the 26.1.2 submit pipeline itself.
        super.submit(renderState, poseStack, bufferSource, camera);
    }

    @Override
    public void addRenderData(T animatable, Void unused, BMDGeoEntityRenderState renderState, float partialTick) {
        super.addRenderData(animatable, unused, renderState, partialTick);
        renderState.addGeckolibData(ENTITY_TICKET, animatable);
    }

    @Override
    public void postRenderPass(RenderPassInfo<BMDGeoEntityRenderState> passInfo, SubmitNodeCollector collector) {
        super.postRenderPass(passInfo, collector);

        // Extra per-frame rendering (particles, overlays) that used to run in the old render() hook
        final T entity = (T) passInfo.renderState().getGeckolibData(ENTITY_TICKET);
        if (entity == null) return;

        if (renderer != null)
            renderer.render(entity, 0f, passInfo.renderState().getPartialTick(), passInfo.poseStack(), collector, passInfo.renderState().getPackedLight());
    }

    @Override
    public int getPackedOverlay(T animatable, Void unused, float u, float partialTick) {
        return overlayOverride != null ? overlayOverride.getOverlay() : super.getPackedOverlay(animatable, unused, u, partialTick);
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return deathRotation ? 90f : 0f;
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity, pos) : super.getBlockLightLevel(entity, pos);
    }

}
