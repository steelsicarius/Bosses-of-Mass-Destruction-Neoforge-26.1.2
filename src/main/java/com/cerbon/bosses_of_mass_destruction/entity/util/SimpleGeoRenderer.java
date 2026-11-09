package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IOverlayOverride;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * BOMD's GeckoLib renderer base for NON-living entities (projectiles: comet, spore ball).
 * <p>
 * 26.1.2 render states no longer hold the entity reference; GeckoLib data tickets are the
 * intended way to carry it from {@link #addRenderData} to the render passes.
 * <p>
 * This is the non-living counterpart of {@link SimpleLivingGeoRenderer}: it uses
 * {@link BMDGeoProjectileRenderState} (plain {@code EntityRenderState} + {@code GeoRenderState})
 * instead of a {@code LivingEntityRenderState} subclass, because GeckoLib 5.5.2's
 * {@code GeoEntityRenderer.extractRenderState} hard-casts the entity to {@code LivingEntity}
 * whenever the state is a {@code LivingEntityRenderState} — which crashed with
 * {@code ClassCastException} when a {@code comet} or {@code spore_ball} was rendered
 * (e.g. after {@code /summon bosses_of_mass_destruction:comet}).
 * <p>
 * The constructor signature mirrors {@link SimpleLivingGeoRenderer} so call sites only
 * need to switch the class name ({@code iBoneLight} and {@code renderWithModel} are
 * accepted for parity even though projectiles currently pass {@code null}).
 * <p>
 * The GeckoLib entity renderer implements the 26.1.2 submit pipeline itself
 * ({@code GeoEntityRenderer.submit} -> {@code performRenderPass} -> deferred
 * {@code submitCustomGeometry}); only hooks are overridden here.
 */
public class SimpleGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T, BMDGeoProjectileRenderState> {

    /** Same id/type as SimpleLivingGeoRenderer's ticket; DataTicket.create returns the shared cached instance. */
    private static final DataTicket<Entity> ENTITY_TICKET = DataTicket.create("bmd_entity", Entity.class);

    private final IRenderLight<T> brightness;
    private final IBoneLight iBoneLight;
    private final IRenderer<T> renderer;
    private final IRendererWithModel renderWithModel;
    private final IOverlayOverride overlayOverride;
    private final boolean deathRotation;

    public SimpleGeoRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, IRenderLight<T> brightness, IBoneLight iBoneLight, IRenderer<T> renderer, IRendererWithModel renderWithModel, IOverlayOverride overlayOverride, boolean deathRotation) {
        super(renderManager, model);
        this.brightness = brightness;
        this.iBoneLight = iBoneLight;
        this.renderer = renderer;
        this.renderWithModel = renderWithModel;
        this.overlayOverride = overlayOverride;
        this.deathRotation = deathRotation;
    }

    @Override
    public BMDGeoProjectileRenderState createRenderState(T entity, Void unused) {
        return new BMDGeoProjectileRenderState();
    }

    @Override
    public void addRenderData(T animatable, Void unused, BMDGeoProjectileRenderState renderState, float partialTick) {
        super.addRenderData(animatable, unused, renderState, partialTick);
        renderState.addGeckolibData(ENTITY_TICKET, animatable);
    }

    @Override
    public void postRenderPass(RenderPassInfo<BMDGeoProjectileRenderState> passInfo, SubmitNodeCollector collector) {
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
