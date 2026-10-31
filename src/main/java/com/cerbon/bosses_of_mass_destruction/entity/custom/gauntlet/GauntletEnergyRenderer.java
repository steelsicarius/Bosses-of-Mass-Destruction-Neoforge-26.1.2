package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class GauntletEnergyRenderer implements IRendererWithModel, IRenderer<GauntletEntity> {
    private final GeoModel<GauntletEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final Identifier armorTexture = Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private GauntletEntity gauntletEntity;
    private RenderType type;

    public GauntletEnergyRenderer(GeoModel<GauntletEntity> geoModel, EntityRendererProvider.Context context) {
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * 0.01f;

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(entity, geoModel, context);

        gauntletEntity = entity;
        type = RenderTypes.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(BakedGeoModel model, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        VertexConsumer energyBuffer = buffer.getBuffer(type);
        if (gauntletEntity == null) return;
        if (type == null) return;
        float renderAlpha = gauntletEntity.energyShieldHandler.getRenderAlpha();
        if (renderAlpha == 0.0f) return;
        float lerpedAlpha = Mth.lerp(partialTicks, renderAlpha - 0.1f, renderAlpha);
        int colour = ARGB.color(
                (int) (lerpedAlpha * 255),
                (int) (0.8f * lerpedAlpha * 255),
                (int) (0.2f * lerpedAlpha * 255),
                (int) (0.2f * lerpedAlpha * 255)
        );

        // Stub - actuallyRender API changed in GeckoLib 5.5.2
        if (geoModelProvider == null) return;
    }

    private static class RenderHelper extends GeoEntityRenderer<GauntletEntity, EntityRenderState> {
        private final GauntletEntity gauntletEntity;

        public RenderHelper(GauntletEntity gauntletEntity, GeoModel<GauntletEntity> parentModel, EntityRendererProvider.Context context) {
            super(context, parentModel);
            this.gauntletEntity = gauntletEntity;
        }
    }
}
