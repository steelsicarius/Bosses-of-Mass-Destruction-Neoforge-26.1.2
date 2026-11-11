package com.cerbon.bosses_of_mass_destruction.neoforge.event;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.client.BMDBlockEntitiesClient;
import com.cerbon.bosses_of_mass_destruction.client.render.NodeBossBarRenderer;
import com.cerbon.bosses_of_mass_destruction.client.BMDS2CPacketHandlers;
import com.cerbon.bosses_of_mass_destruction.client.BMDEntitiesClient;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.BurstAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.PillarAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.WaveAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SpikeAction;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.SpikeWaveAction;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.general.particle.SimpleParticle;
import com.cerbon.cerbons_api.api.general.particle.SimpleParticleProvider;
import com.cerbon.cerbons_api.api.static_utilities.Geometries;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
            initBossBarRenderers();
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

        if (lichBarRenderer == null || voidBlossomBarRenderer == null)
            return;

        boolean handled = lichBarRenderer.renderBossBar(BAR_BACKGROUND_SPRITES, BAR_PROGRESS_SPRITES, graphics, x, y, bossEvent)
                || voidBlossomBarRenderer.renderBossBar(BAR_BACKGROUND_SPRITES, BAR_PROGRESS_SPRITES, graphics, x, y, bossEvent)
                || ObsidilithUtils.obsidilithBossBarRenderer.renderBossBar(BAR_BACKGROUND_SPRITES, BAR_PROGRESS_SPRITES, graphics, x, y, bossEvent);

        if (handled)
            event.setCanceled(true);
    }

    private static NodeBossBarRenderer lichBarRenderer;
    private static NodeBossBarRenderer voidBlossomBarRenderer;

    private static void initBossBarRenderers() {
        // must run after entity type registration (FMLClientSetup)
        lichBarRenderer = new NodeBossBarRenderer(
                BMDEntities.LICH.get().getDescriptionId(),
                LichUtils.hpPercentRageModes,
                Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/gui/lich_boss_bar_dividers.png"),
                LichUtils.textureSize
        );
        voidBlossomBarRenderer = new NodeBossBarRenderer(
                BMDEntities.VOID_BLOSSOM.get().getDescriptionId(),
                VoidBlossomEntity.hpMilestones,
                Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/gui/void_blossom_boss_bar_dividers.png"),
                LichUtils.textureSize
        );
    }

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
        // Ported from 1.21.1; CERBONs API 26.1.2 keeps the same SimpleParticle API
        event.registerSpriteSet(BMDParticles.DISAPPEARING_SWIRL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15,20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.SOUL_FLAME.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.COMET_BLUE);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.MAGIC_CIRCLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 40, Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_BURST.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.ORANGE, Vec3Colors.RUNIC_BROWN));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.ENCHANT.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(30, 50), Geometries::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_BURST_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, BurstAction.burstDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ORANGE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_WAVE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorVariation(0.25);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.RED, Vec3Colors.DARK_RED));
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, WaveAction.waveDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.RED);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.DOWNSPARKLE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, WaveAction.waveDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.COMET_BLUE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_SPIKE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.WHITE, Vec3Colors.COMET_BLUE));
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_RUNE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, 10, Geometries::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.WHITE, Vec3Colors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_SPAWN_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, PillarAction.pillarDelay, Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.scale(2.0f);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PILLAR_SPAWN_INDICATOR_2.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, PillarAction.pillarDelay, Geometries::buildBillBoardGeometry, false, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (float) ((Math.sin((double) f * Math.PI) + 1f) * 0.1f));
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.WHITE, Vec3Colors.ENDER_PURPLE));
                            particle.setColorVariation(0.2);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.OBSIDILITH_ANVIL_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(25, 27), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.setColorVariation(0.3);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.GAUNTLET_REVIVE_SPARKLES.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.LASER_RED);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.EYE_OPEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(60, 70), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.LINE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(20, 30), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.GROUND_ROD.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(8, 10), Geometries::buildFlatGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SpikeAction.indicatorDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.VOID_BLOSSOM_SPIKE_WAVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SpikeWaveAction.indicatorDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.ENDER_PURPLE);
                            particle.setColorVariation(0.2);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.PETAL.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, false, true)));

        event.registerSpriteSet(BMDParticles.POLLEN.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, false, true);
                            particle.setColorOverride(f -> new Vec3(1.0, 0.9, 0.4));
                            particle.setColorVariation(0.15);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> 0.05f * (1 - f * 0.25f));
                            int randomRot = RandomUtils.range(0, 360);
                            float angularMomentum = RandomUtils.randSign() * 4f;
                            particle.setRotationOverride(particle1 -> randomRot + particle1.getAge() * angularMomentum);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPORE.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(7, 15), Geometries::buildBillBoardGeometry, true, true);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.scale(4f);
                            particle.setColorOverride(age -> MathUtils.lerpVec(age, Vec3Colors.GREEN, Vec3Colors.DARK_GREEN));
                            particle.setColorVariation(0.25);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.SPORE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, SporeBallProjectile.explosionDelay + RandomUtils.range(-1, 2), Geometries::buildFlatGeometry, true, true);
                            particle.setColorOverride(f -> Vec3Colors.GREEN);
                            particle.setColorVariation(0.35);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 + f) * 0.25f);
                            return particle;
                        }));

        event.registerSpriteSet(BMDParticles.FLUFF.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, true)));

        event.registerSpriteSet(BMDParticles.EARTHDIVE_INDICATOR.get(),
                spriteSet -> new SimpleParticleProvider(spriteSet,
                        particleContext -> {
                            SimpleParticle particle = new SimpleParticle(particleContext, RandomUtils.range(15, 20), Geometries::buildBillBoardGeometry, true, false);
                            particle.setColorOverride(f -> Vec3Colors.RUNIC_BROWN);
                            particle.setColorVariation(0.25);
                            particle.setBrightnessOverride(f -> BMDParticles.FULL_BRIGHT);
                            particle.setScaleOverride(f -> (1 - (f * 0.25f)) * 0.35f);
                            return particle;
                        }));
    }
}
