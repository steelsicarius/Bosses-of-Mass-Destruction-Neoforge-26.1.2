package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.entity.util.animation.ICodeAnimations;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.constant.dataticket.DataTicket;

import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class GeoModel<T extends GeoAnimatable> extends com.geckolib.model.GeoModel<T> {
    /** Carries the animatable instance from {@link #addAdditionalStateData} to the resource getters. */
    public static final DataTicket<GeoAnimatable> ANIMATABLE = DataTicket.create("bmd_animatable", GeoAnimatable.class);

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
    public void addAdditionalStateData(T animatable, Object object, GeoRenderState state) {
        super.addAdditionalStateData(animatable, object, state);
        state.addGeckolibData(ANIMATABLE, animatable);
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        GeoAnimatable animatable = state.getGeckolibData(ANIMATABLE);
        return animatable != null ? modelLocation.apply((T) animatable) : animationLocation;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        GeoAnimatable animatable = state.getGeckolibData(ANIMATABLE);
        return animatable != null ? textureProvider.getTexture((T) animatable) : animationLocation;
    }

    @Override
    public Identifier getAnimationResource(T t) {
        return animationLocation;
    }

    @Override
    public BakedGeoModel getBakedModel(Identifier modelLocation) {
        return super.getBakedModel(modelLocation);
    }

    public BakedGeoModel getAnimationBakedModel() {
        return getBakedModel(animationLocation);
    }
}
