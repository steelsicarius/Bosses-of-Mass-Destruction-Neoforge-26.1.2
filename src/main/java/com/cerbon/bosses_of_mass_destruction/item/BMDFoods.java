package com.cerbon.bosses_of_mass_destruction.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

public class BMDFoods {
    public static final FoodProperties CRYSTAL_FRUIT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .alwaysEdible()
            .build();

    /**
     * 26.1.2: food effects no longer live on {@link FoodProperties} (Builder.effect() was
     * removed); they are part of the {@code minecraft:consumable} data component, passed via
     * {@code Item.Properties#food(FoodProperties, Consumable)}. Same effects as the original:
     * Regeneration II (15s), Instant Health, Resistance I (30s), all at 100% probability.
     */
    public static final Consumable CRYSTAL_FRUIT_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                    new MobEffectInstance(MobEffects.REGENERATION, 300, 1),
                    new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1),
                    new MobEffectInstance(MobEffects.RESISTANCE, 600, 0)
            )))
            .build();
}
