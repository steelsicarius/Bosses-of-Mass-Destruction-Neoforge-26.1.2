package com.cerbon.bosses_of_mass_destruction.entity;

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

public class BMDEntities {
    public static final BMDConfig mobConfig = AutoConfig.getConfigHolder(BMDConfig.class).getConfig();

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, BMDConstants.MOD_ID);

    public static final RegistryEntry<EntityType<LichEntity>> LICH = ENTITY_TYPES.register("lich",
            () -> EntityType.Builder.<LichEntity>of((entityType, level) -> new LichEntity(entityType, level, mobConfig.lichConfig), MobCategory.MONSTER)
                    .sized(1.8f, 3.0f)
                    .updateInterval(1)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "lich"))));

    public static final RegistryEntry<EntityType<MagicMissileProjectile>> MAGIC_MISSILE = ENTITY_TYPES.register("blue_fireball",
            () -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "blue_fireball"))));

    public static final RegistryEntry<EntityType<CometProjectile>> COMET = ENTITY_TYPES.register("comet",
            () -> EntityType.Builder.<CometProjectile>of(CometProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "comet"))));

    public static final RegistryEntry<EntityType<SoulStarEntity>> SOUL_STAR = ENTITY_TYPES.register("soul_star",
            () -> EntityType.Builder.<SoulStarEntity>of(SoulStarEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "soul_star"))));

    public static final RegistryEntry<EntityType<ChargedEnderPearlEntity>> CHARGED_ENDER_PEARL = ENTITY_TYPES.register("charged_ender_pearl",
            () -> EntityType.Builder.of(ChargedEnderPearlEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "charged_ender_pearl"))));

    public static final RegistryEntry<EntityType<ObsidilithEntity>> OBSIDILITH = ENTITY_TYPES.register("obsidilith",
            () -> EntityType.Builder.<ObsidilithEntity>of((entityType, level) -> new ObsidilithEntity(entityType, level, mobConfig.obsidilithConfig), MobCategory.MONSTER)
                    .sized(2.0f, 4.4f)
                    .fireImmune()
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "obsidilith"))));

    public static final RegistryEntry<EntityType<GauntletEntity>> GAUNTLET = ENTITY_TYPES.register("gauntlet",
            () -> EntityType.Builder.<GauntletEntity>of((entityType, level) -> new GauntletEntity(entityType, level, mobConfig.gauntletConfig) , MobCategory.MONSTER)
                    .sized(5.0f, 4.0f)
                    .fireImmune()
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "gauntlet"))));

    public static final RegistryEntry<EntityType<VoidBlossomEntity>> VOID_BLOSSOM = ENTITY_TYPES.register("void_blossom",
            () -> EntityType.Builder.<VoidBlossomEntity>of((entityType, level) -> new VoidBlossomEntity(entityType, level, mobConfig.voidBlossomConfig), MobCategory.MONSTER)
                    .sized(8.0f, 10.0f)
                    .fireImmune()
                    .clientTrackingRange(3)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom"))));

    public static final RegistryEntry<EntityType<SporeBallProjectile>> SPORE_BALL = ENTITY_TYPES.register("spore_ball",
            () -> EntityType.Builder.<SporeBallProjectile>of(SporeBallProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "spore_ball"))));

    public static final RegistryEntry<EntityType<PetalBladeProjectile>> PETAL_BLADE = ENTITY_TYPES.register("petal_blade",
            () -> EntityType.Builder.<PetalBladeProjectile>of(PetalBladeProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "petal_blade"))));

    public static final LichKillCounter killCounter = new LichKillCounter(mobConfig.lichConfig.summonMechanic);

    
    public static void register() {
        ENTITY_TYPES.register();
    }
}
