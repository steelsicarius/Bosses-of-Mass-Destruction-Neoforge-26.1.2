package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.HashMap;
import java.util.Map;

/**
 * Render state for BOMD GeckoLib entities: combines the vanilla
 * {@link LivingEntityRenderState} with GeckoLib's {@link GeoRenderState} data map.
 * <p>
 * GeckoLib's EntityRenderStateMixin also injects these methods into the vanilla state at
 * runtime backed by its own {@code geckolib$data} field, so this class overrides every
 * GeoRenderState method to keep reads and writes on a single consistent map (mixing the
 * injected field with an overridden getDataMap would silently lose all data).
 */
public class BMDGeoEntityRenderState extends LivingEntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();

    @Override
    public <D> void addGeckolibData(DataTicket<D> ticket, D data) {
        this.dataMap.put(ticket, data);
    }

    @Override
    public boolean hasGeckolibData(DataTicket<?> ticket) {
        return this.dataMap.containsKey(ticket);
    }

    @Override
    public <D> D getGeckolibData(DataTicket<D> ticket) {
        return (D) this.dataMap.get(ticket);
    }

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.dataMap;
    }

    @Override
    public int getPackedLight() {
        return this.lightCoords;
    }

    @Override
    public double getAnimatableAge() {
        return this.ageInTicks;
    }
}
