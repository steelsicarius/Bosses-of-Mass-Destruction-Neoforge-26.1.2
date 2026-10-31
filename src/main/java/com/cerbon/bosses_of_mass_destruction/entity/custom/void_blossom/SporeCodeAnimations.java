package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import net.minecraft.util.Mth;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;

public class SporeCodeAnimations implements ICodeAnimations<SporeBallProjectile> {

    public SporeCodeAnimations(){}

    @Override
    public void animate(SporeBallProjectile animatable, float partialTick, GeoModel<SporeBallProjectile> geoModel) {
        float pitch = animatable.impacted ? animatable.getXRot() : Mth.rotLerp(partialTick, animatable.getXRot() - 5, animatable.getXRot());

        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));
        model.getBone("root1").ifPresent(it -> it.setRotX((float) Math.toRadians(pitch)));
    }
}