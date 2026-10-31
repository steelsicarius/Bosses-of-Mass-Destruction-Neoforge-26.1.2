package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.ITextureProvider;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.resources.Identifier;

public class GauntletTextureProvider implements ITextureProvider<GauntletEntity> {

    @Override
    public Identifier getTexture(GauntletEntity entity) {
        return entity.hurtTime > 0
                ? Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/gauntlet_hurt.png")
                : Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/gauntlet.png");
    }
}
