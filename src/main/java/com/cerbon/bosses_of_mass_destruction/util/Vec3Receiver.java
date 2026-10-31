package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface Vec3Receiver {
    void clientHandler(Level level, Vec3 vec3);
}
