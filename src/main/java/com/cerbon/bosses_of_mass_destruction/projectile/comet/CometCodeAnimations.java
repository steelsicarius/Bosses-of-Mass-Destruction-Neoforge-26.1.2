package com.cerbon.bosses_of_mass_destruction.projectile.comet;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import com.geckolib.animatable.GeoAnimatable;

public class CometCodeAnimations implements ICodeAnimations<CometProjectile> {
    @Override
    public void animate(
            CometProjectile animatable,
            float partialTick,
            GeoModel<CometProjectile> geoModel
    ) {
        // Stub - GeckoLib 5.5.2 bone manipulation API changed
    }
}
