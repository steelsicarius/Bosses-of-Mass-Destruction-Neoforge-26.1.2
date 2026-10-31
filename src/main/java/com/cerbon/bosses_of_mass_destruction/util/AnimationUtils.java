package com.cerbon.bosses_of_mass_destruction.util;


import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;

public class AnimationUtils {
    public static <T extends GeoAnimatable> AnimationController.AnimationStateHandler<T> createIdlePredicate(String animationName) {
        return animationState -> {
            animationState.getController().setAnimation(
                    RawAnimation.begin().thenLoop(animationName)
            );
            return PlayState.CONTINUE;
        };
    }
}
