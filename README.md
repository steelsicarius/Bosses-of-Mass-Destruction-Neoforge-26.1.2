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

### Remaining Work (~200 compilation errors)
- Renderer classes need complete rewrite for Minecraft 26.1.2 render state system
  - `BlockEntityRenderer<T>` → `BlockEntityRenderer<T, R extends BlockEntityRenderState>`
  - `EntityRenderer<T>` → `EntityRenderer<T, R extends EntityRenderState>`
  - Old `render(PoseStack, ...)` methods replaced by `submit(RenderState, ...)`
- Block entity `loadAdditional`/`saveAdditional` method signatures changed
- Block `updateShape` method signature changed
- `ChunkPos` record constructor and field access changes
- Various other Minecraft 26.1.2 API changes

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