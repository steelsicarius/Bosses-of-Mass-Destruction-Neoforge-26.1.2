package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.HashMap;
import java.util.Map;

/**
 * Render state for BOMD GeckoLib entities: combines the vanilla
 * {@link LivingEntityRenderState} with GeckoLib's {@link GeoRenderState} data map.
 * GeckoLib's own EntityRenderStateMixin does this for the vanilla states at runtime,
 * but the compile-time generics of GeoEntityRenderer require the R type argument to
 * implement it, and createRenderState must return it.
 */
public class BMDGeoEntityRenderState extends LivingEntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.dataMap;
    }
}
