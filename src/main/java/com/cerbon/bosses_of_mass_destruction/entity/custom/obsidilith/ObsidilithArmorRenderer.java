package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.client.render.IRendererWithModel;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

import java.util.Random;

public class ObsidilithArmorRenderer implements IRendererWithModel, IRenderer<ObsidilithEntity> {
    private final GeoModel<ObsidilithEntity> geoModel;
    private final EntityRendererProvider.Context context;

    private final Identifier armorTexture = Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/obsidilith_armor.png");
    private RenderHelper geoModelProvider;
    private ObsidilithEntity obsidilithEntity;
    private RenderType type;

    public ObsidilithArmorRenderer(GeoModel<ObsidilithEntity> geoModel, EntityRendererProvider.Context context){
        this.geoModel = geoModel;
        this.context = context;
    }

    @Override
    public void render(ObsidilithEntity entity, float yaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector buffer, int light) {
        float renderAge = entity.tickCount + partialTicks;
        float textureOffset = renderAge * new Random().nextFloat();

        if (geoModelProvider == null)
            geoModelProvider = new RenderHelper(entity, geoModel, context);

        this.obsidilithEntity = entity;
        type = RenderTypes.energySwirl(armorTexture, textureOffset, textureOffset);
    }

    @Override
    public void render(BakedGeoModel model, float partialTicks, PoseStack poseStack, SubmitNodeCollector buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        // TODO: energy swirl layer pending a GeckoLib 5.5.2 GeoRenderLayer port
    }

    private Vec3 getColor() {
        Vec3 color;
        switch (obsidilithEntity.currentAttack){
            case ObsidilithUtils.burstAttackStatus -> color = Vec3Colors.ORANGE;
            case ObsidilithUtils.waveAttackStatus -> color = Vec3Colors.RED;
            case ObsidilithUtils.spikeAttackStatus -> color = Vec3Colors.COMET_BLUE;
            case ObsidilithUtils.anvilAttackStatus -> color = Vec3Colors.ENDER_PURPLE;
            case ObsidilithUtils.pillarDefenseStatus -> color = Vec3Colors.WHITE;
            default -> color = Vec3Colors.WHITE;
        }
        return color;
    }

    private static class RenderHelper extends GeoEntityRenderer<ObsidilithEntity, EntityRenderState> {
        private final ObsidilithEntity entity;

        public RenderHelper(ObsidilithEntity entity, GeoModel<ObsidilithEntity> parentModel, EntityRendererProvider.Context context) {
            super(context, parentModel);
            this.entity = entity;
        }
    }
}
