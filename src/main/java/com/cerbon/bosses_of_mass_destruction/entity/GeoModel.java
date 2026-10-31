package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import net.minecraft.resources.Identifier;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.cache.model.BakedGeoModel;

import java.util.function.Function;

public class GeoModel<T extends GeoAnimatable> extends com.geckolib.model.GeoModel<T> {
    private final Function<T, Identifier> modelLocation;
    private final ITextureProvider<T> textureProvider;
    private final Identifier animationLocation;
    private final ICodeAnimations<T> codeAnimations;

    public GeoModel(Function<T, Identifier> modelLocation, ITextureProvider<T> textureProvider, Identifier animationLocation, ICodeAnimations<T> codeAnimations){
        this.modelLocation = modelLocation;
        this.textureProvider = textureProvider;
        this.animationLocation = animationLocation;
        this.codeAnimations = codeAnimations;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return animationLocation;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return animationLocation;
    }

    @Override
    public Identifier getAnimationResource(T t) {
        return animationLocation;
    }

    @Override
    public BakedGeoModel getBakedModel(Identifier modelLocation) {
        return super.getBakedModel(modelLocation);
    }
}
