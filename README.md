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
- Remaining work:
  - Client-side testing (renderers were moved but not visually verified; no display in this environment)
  - Particle renderers stubbed out: `PetalBladeParticleRenderer`, `ObsidilithEffectHandler` effects,
    `GauntletEnergyRenderer`/`LaserParticleRenderer` paths pending new particle API usage
  - Vanilla mixins of the mod are temporarily disabled (`bosses_of_mass_destruction-common.mixins.json`
    is empty): `EnderEyeUseMixin`, `ExplosionMixin`, `IceBlockMixin`, `LivingEntityMixin`,
    `NaturalSpawnerMixin`, `BossHealthOverlayMixin` must be re-ported to 26.1.2 targets
  - `BMDItems.initClient()`: the "throwing" item model property needs the new 26.1.2 item model API
  - Food effects removed (FoodProperties.Builder.effect() no longer exists)

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