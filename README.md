# Bosses of Mass Destruction

[![License](https://img.shields.io/badge/license-GNU%20LGPL--3.0-blue.svg)](LICENSE)
[![NeoForge](https://img.shields.io/badge/minecraft-26.1.2-2D3748.svg)](https://neoforged.net/)

## Installation

1. Install NeoForge Mod Loader 26.1.2
2. Download the latest mod JAR file from the releases
3. Place the mod file in your `mods` folder

## Porting Status

This mod has been ported from NeoForge 1.21 (Forge-era code) to NeoForge 26.1.2 (MC 26.1.2, Java 25).

### Completed
- Build system migrated to NeoForge MDK (net.neoforged.moddev 2.0.148)
- All Forge-specific packages removed and replaced with NeoForge equivalents
- Import/class name changes for Minecraft 26.1.2 API:
  - `net.minecraft.util.ResourceLocation` → `net.minecraft.resources.Identifier`
  - `net.minecraft.util.FastColor` → `net.minecraft.util.ARGB`
  - `net.minecraft.client.renderer.RenderType` → `net.minecraft.client.renderer.rendertype.RenderType`
  - `net.minecraft.world.item.UseAnim` → `net.minecraft.world.item.ItemUseAnimation`
  - `Level.isClientSide` field → `isClientSide()` method
  - `EntityType.Builder.build(String)` → `build(ResourceKey)`
  - `BlockEntityType.Builder.build(null)` → `build(ResourceKey)`
  - `SavedData.Factory` → `SavedDataType`
  - `Item.use()` returns `InteractionResult` (not `InteractionResultHolder`)
  - `EventBusSubscriber.bus` parameter removed
  - GeckoLib 5.5.2 API changes (PlayState, AnimationStateHandler, GeoCube package, etc.)
- CERBONs API ported to 26.1.2 first (see `../CERBONs-API-master-26.1.2`), jar
  `libs/cerbons_api-neoforge-26.1.2-1.2.0.jar` replaces the 1.21 binary
- Dependencies updated to MC 26.1.2 builds in `libs/`:
  - `cloth-config-26.1.154.jar`, `geckolib-neoforge-26.1.2-5.5.2.jar`
- Block/Item registration uses `Properties.setId(ResourceKey)` (mandatory in 26.1.2)
- Client-only code isolated from common classes (server `DistCleaner` crashes otherwise):
  - `client/BMDEntitiesClient`, `client/BMDBlockEntitiesClient` (renderers)
  - `client/BMDS2CPacketHandlers` (injected `clientHandler` consumers on packets)
  - `client/VoidBlossomClient`, `client/VoidLilyClient`, `client/ReviveParticlesClient`,
    `client/BrimstoneNectarEffectsClient` (particle handlers)
- Recipe JSONs updated to 26.1.2 ingredient format (plain item strings)
- Dedicated server verified: boots with all 4 mods (cerbons_api, cloth_config,
  geckolib, bosses_of_mass_destruction) and reaches "Done" on port 25599

### Compilation
- `./gradlew build` produces `build/libs/BOMD-1.3.3.jar` (BUILD SUCCESSFUL)
- `./gradlew runServer` boots a dedicated server (configure `run/server.properties`
  with `server-port=25599`, `server-ip=0.0.0.0` and `run/eula.txt=eula=true`)
- `./gradlew runClient` boots the client on ARM dev machines with
  `LD_LIBRARY_PATH=<dir with libstdc++ >= GLIBCXX_3.4.30>` (LWJGL openal native
  needs a newer libstdc++ than the system one; a compatible copy lives in
  `/tmp/stdcxx/extracted/lib`) plus `xvfb-run` on headless machines. Verified:
  mods load, resource reload completes and the NeoForge client network
  validation passes.
- Automated headless render test (opt-in, off by default): `BMDRenderTestNeo`
  spawns frozen copies of the four bosses in front of the player ~3s after login
  (integrated or dedicated server) and the client saves framebuffer screenshots
  to `<gameDir>/screenshots/bmd_dbg_N.png` (4 shots, ~7s apart). Run it with:
  `xvfb-run -a -s "-screen 0 1280x800x24" env LD_LIBRARY_PATH=/tmp/stdcxx/extracted/lib \
     JAVA_HOME=/usr/java/jdk-25-oracle-aarch64 ./gradlew runClient \
     -PquickplayWorld=testworld -PclientGameDir=run-client-test -PbmdRenderTest=true`
  Requires `testworld` under `<gameDir>/saves/`; enabled by the
  `-Dbmd.renderTest=true` system property (wired through `-PbmdRenderTest=true`).
- Vanilla mixins of the mod re-ported: EnderEyeUse, Explosion (new
  `ServerLevel#explode` signature), IceBlock, NaturalSpawner; the shield-piercing
  behaviour now uses `LivingShieldBlockEvent` instead of a mixin (26.1.2 made
  shield blocking data-driven), and the custom boss bars use
  `CustomizeGuiOverlayEvent.BossEventProgress` instead of `BossHealthOverlayMixin`
  (`drawBar`/`GuiGraphics` were removed; rendering goes through `GuiGraphicsExtractor`).
- Item model definitions (`assets/bosses_of_mass_destruction/items/*.json`, mandatory
  since 1.21.4) generated for every item; `earthdive_spear` uses the vanilla
  `minecraft:using_item` condition, which matches the old `throwing` predicate logic.
- All four GeckoLib boss models verified rendering in-game (Lich, Obsidilith, Gauntlet, Void
  Blossom) with the automated headless render test (2026-11-08); the earlier "bosses invisible"
  reports predate the 2026-11-03 render-pipeline and asset-ID fixes. The full GeckoLib chain
  was also traced statically (extract -> addRenderData -> submit -> performRenderPass ->
  deferred submitCustomGeometry -> CustomFeatureRenderer draw) and runs correctly.
- All particle effects restored (ObsidilithEffectHandler, PetalBladeParticleRenderer,
  GauntletBlindnessIndicatorParticles, ChargedEnderPearl particles) using the ported
  ClientParticleBuilder over the new SingleQuadParticle pipeline
- CERBONs API oriented-hitbox debug mixin re-implemented with Gizmos
  (EntityHitboxDebugRendererMixin, in ../CERBONs-API-master-26.1.2)
- Singleplayer-world crash fixed in CERBONs API: MultiplayerGameModeMixin.interactHook now
  matches the 26.1.2
  `MultiPlayerGameMode.interact(Player, Entity, EntityHitResult, InteractionHand)` signature
- Food effects restored via the 26.1.2 consumable data component (FoodProperties.Builder.effect()
  no longer exists): crystal fruit applies Regeneration II (15s), Instant Health and
  Resistance I (30s) on eat, via `Consumables.defaultFood().onConsume(ApplyStatusEffectsConsumeEffect)`
  passed to `Item.Properties.food(FoodProperties, Consumable)`
- Custom particle providers registered (2026-11-10): the 29 custom particles now have client
  providers again (ported from 1.21.1 into `registerParticleProviders`, same
  `SimpleParticle`/`ClientParticleBuilder` API via the ported CERBONs API jar). Previously the
  empty provider registration made the client silently drop every spawned particle (sounds
  played, nothing was visible).
- Custom boss bars fixed and verified in-game (2026-11-11): rendering now goes through
  `CustomizeGuiOverlayEvent.BossEventProgress` in `BMDClientEventsNeo` (`drawBar`/`GuiGraphics`
  were removed in 26.1.2; the vanilla chain is `BossHealthOverlay.extractRenderState` ->
  `ClientHooks.onCustomizeBossEventProgress`). Lich, Obsidilith and Void Blossom show their
  custom textured bars with node dividers; the temporary `[BMD-BOSSBAR]` diagnostics were
  removed after the fix was confirmed.
- GeckoLib 5.5.2 projectile-renderer crash fixed (2026-11-11): BOMD projectiles are
  `Entity` but not `LivingEntity` (correct design), while GeckoLib 5.5.2's
  `GeoEntityRenderer.extractRenderState` unconditionally reads living-only state and
  crashed on them. Fix on the BOMD side, without patching GeckoLib:
  `BMDGeoEntityRenderState` extends `LivingEntityRenderState` and implements
  `GeoRenderState` (all data-map methods overridden to a single consistent map),
  `SimpleLivingGeoRenderer<T extends Entity & GeoAnimatable>` overrides
  `createRenderState`/`addRenderData`/`postRenderPass` to carry the entity via a
  GeckoLib `DataTicket`, and non-modelled projectiles use vanilla renderers
  (`ThrownItemRenderer` for soul star and charged ender pearl, `SimpleEntityRenderer`
  billboard pipeline for the magic missile). Comet, soul star, magic missiles and
  spore balls spawn/attack without crashing and render with their trails/particles.
- Particle visual parity with 1.21.1 confirmed in-game (2026-11-11): the ported CERBONs
  API `SimpleParticle` (now a `SingleQuadParticle`) applies the mod's scale overrides with
  the same multiplicative `Particle.scale(float)` semantics as 1.21.1, so ability
  particles (obsidilith burst/wave, spore, comet trail, soul flame, pollen, etc.) render
  at the original size; mod-side `ParticleFactories` and the 29 provider definitions are
  byte-for-byte identical to the 1.21.1 sources.
- Dev-environment note (FML 11.0.15+): the patched MC jar in `build/moddev/artifacts/` needs the
  manifest attribute `Minecraft-Dists: client server` (lowercase), otherwise FML aborts with
  "NeoForge dev environment Minecraft jar does not have a Minecraft-Dists attribute". The
  attribute was added to the pre-generated artifact and `build.gradle` skips
  `createMinecraftArtifacts` (`onlyIf { false }`) so the low-memory dev box does not re-run the
  ~40 min NFRT pipeline. Remove that block when bumping `neo_version`.

- Remaining work (post-port polish, none blocking):
  - Manual play-testing of boss fights (AI, attacks, loot): the automated harness verifies
    rendering, not combat behaviour
  - Long multiplayer sessions to confirm stability under real network conditions

### Note on Parchment Mappings

By default, the MDK is configured to use the official mapping names from Mojang for methods and fields in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

## Additional Resources
- Community Documentation: https://docs.neoforged.net/
- NeoForged Discord: https://discord.neoforged.net/
- Original Mod: https://github.com/miyo6032/bosses-of-mass-destruction

## License

This project uses code from the original [Bosses of Mass Destruction](https://github.com/miyo6032/bosses-of-mass-destruction) mod.

Copyright (C) 2027 miyo6032

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.