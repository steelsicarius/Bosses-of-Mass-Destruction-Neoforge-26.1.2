package com.cerbon.bosses_of_mass_destruction.neoforge.event;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.client.BMDBlockEntitiesClient;
import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.client.BMDS2CPacketHandlers;
import com.cerbon.bosses_of_mass_destruction.client.BMDEntitiesClient;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = BMDConstants.MOD_ID, value = Dist.CLIENT)
public class BMDClientEventsNeo {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BMDS2CPacketHandlers.assign();
            BMDItems.initClient();
            BMDEntitiesClient.init();
            BMDBlockEntitiesClient.init();
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> AutoConfigClient.getConfigScreen(BMDConfig.class, screen).get());
        });
    }

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        // Replaces the old BossHealthOverlayMixin (drawBar/GuiGraphics removed in 26.1.2)
        LerpingBossEvent bossEvent = event.getBossEvent();
        int x = event.getX();
        int y = event.getY();
        GuiGraphicsExtractor graphics = event.getGuiGraphics();

        boolean handled = lichBarRenderer.renderBossBar(BAR_BACKGROUND_SPRITES, BAR_PROGRESS_SPRITES, graphics, x, y, bossEvent)
                || voidBlossomBarRenderer.renderBossBar(BAR_BACKGROUND_SPRITES, BAR_PROGRESS_SPRITES, graphics, x, y, bossEvent)
                || ObsidilithUtils.obsidilithBossBarRenderer.renderBossBar(BAR_BACKGROUND_SPRITES, BAR_PROGRESS_SPRITES, graphics, x, y, bossEvent);

        if (handled)
            event.setCanceled(true);
    }

    private static final NodeBossBarRenderer lichBarRenderer = new NodeBossBarRenderer(
            BMDEntities.LICH.get().getDescriptionId(),
            LichUtils.hpPercentRageModes,
            Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/gui/lich_boss_bar_dividers.png"),
            LichUtils.textureSize
    );

    private static final NodeBossBarRenderer voidBlossomBarRenderer = new NodeBossBarRenderer(
            BMDEntities.VOID_BLOSSOM.get().getDescriptionId(),
            VoidBlossomEntity.hpMilestones,
            Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/gui/void_blossom_boss_bar_dividers.png"),
            LichUtils.textureSize
    );

    // Same sprites used by the vanilla BossHealthOverlay (private in 26.1.2)
    private static final Identifier[] BAR_BACKGROUND_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("boss_bar/pink_background"),
            Identifier.withDefaultNamespace("boss_bar/blue_background"),
            Identifier.withDefaultNamespace("boss_bar/red_background"),
            Identifier.withDefaultNamespace("boss_bar/green_background"),
            Identifier.withDefaultNamespace("boss_bar/yellow_background"),
            Identifier.withDefaultNamespace("boss_bar/purple_background"),
            Identifier.withDefaultNamespace("boss_bar/white_background")
    };
    private static final Identifier[] BAR_PROGRESS_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("boss_bar/pink_progress"),
            Identifier.withDefaultNamespace("boss_bar/blue_progress"),
            Identifier.withDefaultNamespace("boss_bar/red_progress"),
            Identifier.withDefaultNamespace("boss_bar/green_progress"),
            Identifier.withDefaultNamespace("boss_bar/yellow_progress"),
            Identifier.withDefaultNamespace("boss_bar/purple_progress"),
            Identifier.withDefaultNamespace("boss_bar/white_progress")
    };

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        // CERBONs API SimpleParticle extends TextureSheetParticle which was removed in NeoForge 26.1.2
        // Particle providers will need to be re-implemented using the new particle system
        // TODO: Re-implement custom particle providers using vanilla Minecraft particle API
    }
}
