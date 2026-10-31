package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleEntityRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
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
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector bufferSource, CameraRenderState camera) {
        // Stub - new render state system not yet implemented
    }

    @Override
    public void render(
            @NotNull T entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight
    ) {
        renderer.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull T entity) {
        return textureProvider.getTexture(entity);
    }

    @Override
    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return brightness != null ? brightness.getBlockLight(entity, pos) : super.getBlockLightLevel(entity, pos);
    }
}
