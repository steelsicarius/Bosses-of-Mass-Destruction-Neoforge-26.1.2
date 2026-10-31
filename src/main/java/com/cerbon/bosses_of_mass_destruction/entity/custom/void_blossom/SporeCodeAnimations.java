package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.geckolib.animatable.GeoAnimatable;

public class SporeCodeAnimations implements ICodeAnimations<SporeBallProjectile> {

    public SporeCodeAnimations(){}

    @Override
    public void animate(SporeBallProjectile animatable, float partialTick, GeoModel<SporeBallProjectile> geoModel) {
        // Stub - GeckoLib 5.5.2 bone manipulation API changed
    }
}
