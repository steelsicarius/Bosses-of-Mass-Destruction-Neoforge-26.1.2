package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.geckolib.cache.model.BakedGeoModel;

public interface IRendererWithModel {
    void render(
            BakedGeoModel model,
            float partialTicks,
            PoseStack poseStack,
            SubmitNodeCollector buffer,
            int packedLightIn,
            int packedOverlayIn,
            float red,
            float green,
            float blue,
            float alpha
    );
}

