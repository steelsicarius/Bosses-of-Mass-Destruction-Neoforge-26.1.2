package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.block.custom.MonolithBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerLevel.class)
public class ExplosionMixin {

    // The first float argument of ServerLevel#explode is the explosion radius.
    @ModifyVariable(at = @At("HEAD"), method = "explode", argsOnly = true)
    private float bmd_explosionPower(float radius, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float r, boolean fire, Level.ExplosionInteraction explosionInteraction){
        Level level = (Level) (Object) this;
        if (!level.isClientSide())
            return MonolithBlock.getExplosionPower((ServerLevel) level, BlockPos.containing(x, y, z), radius);

        return radius;
    }
}
