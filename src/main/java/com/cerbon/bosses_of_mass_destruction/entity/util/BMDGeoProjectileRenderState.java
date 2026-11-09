package com.cerbon.bosses_of_mass_destruction.entity.util;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.HashMap;
import java.util.Map;

/**
 * Render state for BOMD GeckoLib NON-living entities (projectiles such as the comet and
 * the spore ball): combines the vanilla {@link EntityRenderState} with GeckoLib's
 * {@link GeoRenderState} data map.
 * <p>
 * GeckoLib 5.5.2's {@code GeoEntityRenderer.extractRenderState} casts the entity to
 * {@code LivingEntity} whenever the render state is a {@code LivingEntityRenderState};
 * for projectiles (which are not living entities) that cast throws
 * {@code ClassCastException} (crash when summoning {@code comet} / {@code spore_ball}).
 * Using a plain {@code EntityRenderState} subclass makes GeckoLib skip the living-entity
 * extraction branch entirely and instead populate the non-living data tickets
 * (ENTITY_POSE, ENTITY_YAW, ENTITY_BODY_YAW, ...), exactly like GeckoLib's own default
 * factory does for non-living animatables.
 * <p>
 * Like {@link BMDGeoEntityRenderState}, every {@link GeoRenderState} method is overridden
 * to route to a single local data map (GeckoLib's EntityRenderStateMixin injects its own
 * {@code geckolib$data} map at runtime; overriding keeps reads and writes consistent).
 */
public class BMDGeoProjectileRenderState extends EntityRenderState implements GeoRenderState {
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
