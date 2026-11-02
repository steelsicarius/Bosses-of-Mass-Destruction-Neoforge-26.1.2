package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleEntityRenderer<T extends Entity> extends EntityRenderer<T, SimpleEntityRenderer.State<T>> {
    private final IRenderer<T> renderer;
    private final ITextureProvider<T> textureProvider;
    private final IRenderLight<T> brightness;

    public SimpleEntityRenderer(EntityRendererProvider.Context renderManager, IRenderer<T> renderer, ITextureProvider<T> textureProvider, IRenderLight<T> brightness) {
        super(renderManager);
        this.renderer = renderer;
        this.textureProvider = textureProvider;
        this.brightness = brightness;
    }

    @Override
    public State<T> createRenderState() {
        return new State<>();
    }

    @Override
    public void extractRenderState(T entity, State<T> state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
        state.partialTicks = partialTick;
    }

    @Override
    public void submit(State<T> state, PoseStack poseStack, SubmitNodeCollector bufferSource, CameraRenderState camera) {
        if (renderer != null && state.entity != null)
            renderer.render(state.entity, 0f, state.partialTicks, poseStack, bufferSource, state.lightCoords);
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity, pos) : super.getBlockLightLevel(entity, pos);
    }

    public static class State<T extends Entity> extends EntityRenderState {
        public T entity;
        public float partialTicks;
    }
}
