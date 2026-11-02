package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class EternalNightRenderer implements IRenderer<LichEntity> {

    @Override
    public void render(LichEntity entity, float yaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector buffer, int light) {
        // Stub - time manipulation API changed in 26.1.2
    }
}
