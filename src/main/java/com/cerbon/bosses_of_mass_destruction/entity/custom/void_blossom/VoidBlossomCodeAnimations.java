package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;

public class VoidBlossomCodeAnimations implements ICodeAnimations<VoidBlossomEntity> {
    @Override
    public void animate(VoidBlossomEntity animatable, float partialTick, GeoModel<VoidBlossomEntity> geoModel) {
        float bodyYaw = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);

        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));
        model.getBone("Leaves").ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(bodyYaw)));
        model.getBone("Thorns").ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(bodyYaw)));
        model.getBone("Roots").ifPresent(geoBone -> geoBone.setRotY((float) Math.toRadians(bodyYaw)));
    }
}