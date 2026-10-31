package com.cerbon.bosses_of_mass_destruction.client.render;

import net.minecraft.resources.Identifier;

@FunctionalInterface
public interface ITextureProvider<T> {
    Identifier getTexture(T entity);
}

