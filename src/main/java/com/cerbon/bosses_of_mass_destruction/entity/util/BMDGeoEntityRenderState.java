package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.HashMap;
import java.util.Map;

/**
 * Render state for BOMD GeckoLib entities: combines the vanilla
 * {@link LivingEntityRenderState} with GeckoLib's {@link GeoRenderState} data map so the
 * entity reference can be carried from extraction to the render passes (26.1.2 render
 * states no longer hold the entity).
 */
public class BMDGeoEntityRenderState extends LivingEntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.dataMap;
    }
}
