package com.cerbon.bosses_of_mass_destruction.projectile.comet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.util.Mth;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;

public class CometCodeAnimations implements ICodeAnimations<CometProjectile> {
    @Override
    public void animate(
            CometProjectile animatable,
            float partialTick,
            GeoModel<CometProjectile> geoModel
    ) {
        float pitch = Mth.rotLerp(partialTick, animatable.getXRot() - 5, animatable.getXRot());
        BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(animatable));
        model.getBone("root1").ifPresent(geoBone -> geoBone.setRotX((float) Math.toRadians(pitch)));
    }
}