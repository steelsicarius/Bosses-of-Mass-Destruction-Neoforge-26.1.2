package com.cerbon.bosses_of_mass_destruction.client;

import com.cerbon.bosses_of_mass_destruction.animation.PauseAnimationTimer;
import com.cerbon.bosses_of_mass_destruction.client.render.*;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.*;
import com.cerbon.bosses_of_mass_destruction.entity.custom.lich.*;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithArmorRenderer;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithBoneLight;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.*;
import com.cerbon.bosses_of_mass_destruction.entity.util.SimpleLivingGeoRenderer;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.item.custom.SoulStarEntity;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.PetalBladeProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometCodeAnimations;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometProjectile;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.general.data.WeakHashPredicate;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import com.mojang.blaze3d.Blaze3D;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.GeoModel;

/** Client-only registration. Loaded only on the client (see BMDClientEventsNeo). */
public class BMDEntitiesClient {

    public static void init() {
        PauseAnimationTimer pauseSecondTimer = new PauseAnimationTimer(Blaze3D::getTime, () -> Minecraft.getInstance().isPaused());

        EntityRenderers.register(BMDEntities.LICH.get(), context -> {
            Identifier texture = Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/lich.png");
            return new SimpleLivingGeoRenderer<>(
                    context,
                    new GeoModel<>(
                            lichEntity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "lich"),
                            entity -> texture,
                            Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "lich.animation"),
                            new LichCodeAnimations()
                    ),
                    new BoundedLighting<>(5),
                    new LichBoneLight(),
                    new EternalNightRenderer(),
                    null,
                    null,
                    true
            );
        });

        EntityRenderers.register(BMDEntities.OBSIDILITH.get(), context -> {
            ObsidilithBoneLight runeColorHandler = new ObsidilithBoneLight();
            GeoModel<ObsidilithEntity> modelProvider = new GeoModel<>(
                    entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "obsidilith"),
                    entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/obsidilith.png"),
                    Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "obsidilith.animation"),
                    (animatable, data, geoModel) -> {}
            );
            ObsidilithArmorRenderer armorRenderer = new ObsidilithArmorRenderer(modelProvider, context);
            return new SimpleLivingGeoRenderer<>(
                    context,
                    modelProvider,
                    null,
                    runeColorHandler,
                    new CompositeRenderer<>(armorRenderer, runeColorHandler),
                    armorRenderer,
                    null,
                    false
            );
        });

        EntityRenderers.register(BMDEntities.COMET.get(), context ->
                new SimpleLivingGeoRenderer<>(
                        context,
                        new GeoModel<>(
                                geoAnimatable -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "comet"),
                                entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/comet.png"),
                                Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "comet.animation"),
                                new CometCodeAnimations()
                        ),
                        new FullRenderLight<>(),
                        null,
                        new ConditionalRenderer<>(
                                new WeakHashPredicate<>(() -> new FrameLimiter(60f, pauseSecondTimer)::canDoFrame),
                                new LerpedPosRenderer<>(vec3 -> ParticleFactories.cometTrail().build(vec3.add(RandomUtils.randVec().scale(0.5)), Vec3.ZERO))
                        ),
                        null,
                        null,
                        true
                ));

        EntityRenderers.register(BMDEntities.SOUL_STAR.get(), context ->
                new ThrownItemRenderer<>(context, 1.0f, true));

        EntityRenderers.register(BMDEntities.CHARGED_ENDER_PEARL.get(), ThrownItemRenderer::new);

        Identifier missileTexture = Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/blue_magic_missile.png");
        RenderType magicMissileRenderType = RenderTypes.entityCutout(missileTexture);
        EntityRenderers.register(BMDEntities.MAGIC_MISSILE.get(), context ->
                new SimpleEntityRenderer<>(context,
                        new CompositeRenderer<>(
                                new BillboardRenderer<>(context.getEntityRenderDispatcher(), magicMissileRenderType, f -> 0.5f),
                                new ConditionalRenderer<>(
                                        new WeakHashPredicate<>(() -> new FrameLimiter(20f, pauseSecondTimer)::canDoFrame),
                                        new LerpedPosRenderer<>(vec3 -> ParticleFactories.soulFlame().build(vec3.add(RandomUtils.randVec().scale(0.25)), Vec3.ZERO)))),
                        entity -> missileTexture,
                        new FullRenderLight<>()
                ));

        EntityRenderers.register(BMDEntities.GAUNTLET.get(), context -> {
            GeoModel<GauntletEntity> modelProvider = new GeoModel<>(
                    entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "gauntlet"),
                    new GauntletTextureProvider(),
                    Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "gauntlet.animation"),
                    new GauntletCodeAnimations()
            );
            GauntletEnergyRenderer energyRenderer = new GauntletEnergyRenderer(modelProvider, context);
            GauntletOverlay overlayOverride = new GauntletOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    modelProvider,
                    null,
                    null,
                    new CompositeRenderer<>(
                            new GauntletLaserRenderer(),
                            new ConditionalRenderer<>(
                                    new WeakHashPredicate<>(() -> new FrameLimiter(20f, pauseSecondTimer)::canDoFrame),
                                    new LaserParticleRenderer()
                            ),
                            energyRenderer,
                            overlayOverride
                    ),
                    energyRenderer,
                    overlayOverride,
                    false
            );
        });

        EntityRenderers.register(BMDEntities.VOID_BLOSSOM.get(), context -> {
            Identifier texture = Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/void_blossom.png");
            GeoModel<VoidBlossomEntity> modelProvider = new GeoModel<>(
                    entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom"),
                    entity -> texture,
                    Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom.animation"),
                    new VoidBlossomCodeAnimations()
            );
            VoidBlossomBoneLight boneLight = new VoidBlossomBoneLight();
            NoRedOnDeathOverlay overlay = new NoRedOnDeathOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    modelProvider,
                    null,
                    boneLight,
                    new CompositeRenderer<>(new VoidBlossomSpikeRenderer(), boneLight, overlay),
                    null,
                    overlay,
                    false
            );
        });

        EntityRenderers.register(BMDEntities.SPORE_BALL.get(), context -> {
            SporeBallOverlay explosionFlasher = new SporeBallOverlay();
            return new SimpleLivingGeoRenderer<>(
                    context,
                    new GeoModel<>(
                            geoAnimatable -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "comet"),
                            entity -> Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/spore.png"),
                            Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "comet.animation"),
                            new SporeCodeAnimations()),
                    new FullRenderLight<>(),
                    null,
                    new CompositeRenderer<>(
                            new ConditionalRenderer<>(
                                    new WeakHashPredicate<>(() -> new FrameLimiter(60f, pauseSecondTimer)::canDoFrame),
                                    new LerpedPosRenderer<>(vec3 -> {
                                        ClientParticleBuilder projectileParticles = new ClientParticleBuilder(BMDParticles.OBSIDILITH_BURST.get())
                                                .color(Vec3Colors.GREEN)
                                                .colorVariation(0.4)
                                                .scale(0.5f)
                                                .brightness(BMDParticles.FULL_BRIGHT);
                                        projectileParticles.build(vec3.add(RandomUtils.randVec().scale(0.25)), VecUtils.yAxis.scale(0.1));
                                    })),
                            explosionFlasher,
                            new SporeBallSizeRenderer()),
                    null,
                    explosionFlasher,
                    true);
        });

        Identifier petalTexture = Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/petal_blade.png");
        RenderType petalBladeRenderType = RenderTypes.entityCutout(petalTexture);
        EntityRenderers.register(BMDEntities.PETAL_BLADE.get(), context ->
                new SimpleEntityRenderer<>(context,
                        new CompositeRenderer<>(
                                new PetalBladeRenderer(context.getEntityRenderDispatcher(), petalBladeRenderType),
                                new ConditionalRenderer<>(
                                        new WeakHashPredicate<>(() -> new FrameLimiter(30f, pauseSecondTimer)::canDoFrame),
                                        new PetalBladeParticleRenderer<>()
                                )),
                        entity -> petalTexture,
                        new FullRenderLight<>()));
    }
}
