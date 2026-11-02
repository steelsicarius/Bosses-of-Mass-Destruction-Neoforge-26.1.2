package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.joml.Vector4f;
import com.geckolib.cache.model.GeoBone;

public class VoidBlossomBoneLight implements IBoneLight, IRenderer<VoidBlossomEntity> {
    private VoidBlossomEntity entity;
    private Float partialTicks;

    @Override
    public int getLightForBone(GeoBone bone, int packedLight) {
        if (bone.name().contains("Spike") || bone.name().contains("Thorn") || bone.name().contains("FlowerCenter"))
            return 15728880;
        else
            return packedLight;
    }

    @Override
    public Vector4f getColorForBone(GeoBone bone, Vector4f rgbaColor) {
        if (entity == null)
            return rgbaColor;

        float partialTicks1 = this.partialTicks != null ? this.partialTicks : 0f;
        Vector4f newColor = new Vector4f(rgbaColor.x, rgbaColor.y, rgbaColor.z, rgbaColor.w);

        if (entity.isDeadOrDying()){
            float interceptedTime = MathUtils.ratioLerp(entity.deathTime, 0.5f, LightBlockRemover.deathMaxAge, partialTicks1);
            newColor.mul(1f - interceptedTime * 0.5f);
        }

        return newColor;
    }

    @Override
    public void render(VoidBlossomEntity entity, float yaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector buffer, int light) {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }
}
