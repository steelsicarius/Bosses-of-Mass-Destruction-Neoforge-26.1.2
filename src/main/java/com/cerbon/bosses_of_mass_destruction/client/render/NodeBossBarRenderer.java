package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.List;

/**
 * Draws a boss bar with node markers for milestone health thresholds.
 * Replaces the old BossHealthOverlayMixin; called from the NeoForge
 * CustomizeGuiOverlayEvent.BossEventProgress handler.
 */
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

    /**
     * @return true if this renderer handled (drew) the boss bar and the vanilla bar should be cancelled.
     */
    public boolean renderBossBar(Identifier[] backgroundTexture, Identifier[] progressTexture, GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossEvent){
        Component name = bossEvent.getName();
        ComponentContents barContent = name.getContents();

        if (barContent instanceof TranslatableContents translatableContents && translatableContents.getKey().equals(entityTypeKey)){
            int colorLocation = bossEvent.getColor().ordinal();
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED, backgroundTexture[colorLocation], 182, 5, 0, 0, x, y, 182, 5
            );

            int i = Mth.lerpDiscrete(bossEvent.getProgress(), 0, 182);
            if (i > 0){
                int progressLocation = bossEvent.getColor().ordinal();
                guiGraphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED, progressTexture[progressLocation], 182, 5, 0, 0, x, y, i, 5
                );
            }

            renderBossNodes(bossEvent, guiGraphics, x, y);
            return true;
        }

        return false;
    }

    private void renderBossNodes(
            BossEvent bossEvent,
            GuiGraphicsExtractor guiGraphics,
            int x,
            int y
    ) {
        int steppedPercentage = (int) (192 * MathUtils.roundedStep(bossEvent.getProgress(), hpPercentages, true)) + 7;
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                noteTexture, x - 3, y - 1, 0f, 0f, steppedPercentage, 7,
                textureSize,
                textureSize,
                -1
        );

        int steppedPercentageReverse = 192 - steppedPercentage;
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                noteTexture,
                x - 3 + steppedPercentage,
                y - 1,
                (float) steppedPercentage,
                7f,
                steppedPercentageReverse,
                7,
                textureSize,
                textureSize,
                -1
        );
    }
}
