package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PetalBladeParticleRenderer<T extends Entity> implements IRenderer<T> {
    // CERBONs API ClientParticleBuilder depends on SimpleParticle which extends
    // TextureSheetParticle (removed in NeoForge 26.1.2). Particle rendering
    // will need to be re-implemented using the new particle system.
    @Override
    public void render(
            T entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light
    ) {
        // Stubbed - particle rendering disabled pending API migration
    }
}
