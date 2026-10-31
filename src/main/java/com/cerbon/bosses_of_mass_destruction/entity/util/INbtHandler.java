package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface INbtHandler {
    ValueOutput toTag(ValueOutput tag);
    void fromTag(ValueInput tag);
}
