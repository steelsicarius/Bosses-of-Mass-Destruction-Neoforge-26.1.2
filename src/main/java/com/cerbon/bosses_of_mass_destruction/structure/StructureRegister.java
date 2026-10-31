package com.cerbon.bosses_of_mass_destruction.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureRegister {
    private final ResourceKey<Structure> configuredStructureKey;

    public StructureRegister(Identifier structureResourceLocation) {
        this.configuredStructureKey = createConfigureStructureKey(structureResourceLocation);
    }

    private ResourceKey<Structure> createConfigureStructureKey(Identifier resourceLocation) {
        return ResourceKey.create(Registries.STRUCTURE, resourceLocation);
    }

    public ResourceKey<Structure> getConfiguredStructureKey() {
        return this.configuredStructureKey;
    }
}

