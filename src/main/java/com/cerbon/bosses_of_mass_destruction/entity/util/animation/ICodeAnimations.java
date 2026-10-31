package com.cerbon.bosses_of_mass_destruction.entity.util.animation;

import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;
import com.geckolib.animatable.GeoAnimatable;

public interface ICodeAnimations<T extends GeoAnimatable> {
    void animate(T animatable, float partialTick, GeoModel<T> geoModel);
}