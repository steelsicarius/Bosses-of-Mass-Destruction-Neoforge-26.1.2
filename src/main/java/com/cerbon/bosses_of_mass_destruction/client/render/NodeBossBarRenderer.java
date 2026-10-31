package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class NodeBossBarRenderer {
    private final String entityTypeKey;
    private final List<Float> hpPercentages;
    private final Identifier noteTexture;
    private final int textureSize;

    public NodeBossBarRenderer(String entityTypeKey, List<Float> hpPercentages, Identifier noteTexture, int textureSize) {
        this.entityTypeKey = entityTypeKey;
        this.hpPercentages = hpPercentages;
        this.noteTexture = noteTexture;
        this.textureSize = textureSize;
    }

    public void renderBossBar(Identifier[] backgroundTexture, Identifier[] progressTexture, Object guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo callbackInfo){
        // Stub implementation - GuiGraphics API changed in 26.1.2
        // Full implementation would need to use GuiGraphicsExtractor or equivalent
    }

    private void renderBossNodes(
            BossEvent bossEvent,
            Object guiGraphics,
            int x,
            int y
    ) {
        // Stub implementation
    }
}
