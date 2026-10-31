package com.cerbon.bosses_of_mass_destruction.item;

import net.minecraft.world.food.FoodProperties;

public class BMDFoods {
    public static final FoodProperties CRYSTAL_FRUIT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .alwaysEdible()
            .build();
}
