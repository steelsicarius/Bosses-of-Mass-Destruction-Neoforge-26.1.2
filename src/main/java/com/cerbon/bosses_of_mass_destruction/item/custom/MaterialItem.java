package com.cerbon.bosses_of_mass_destruction.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MaterialItem extends Item {

    public MaterialItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag) {
        tooltipConsumer.accept(Component.translatable("item.bosses_of_mass_destruction.crafting_material.tooltip").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipConsumer, tooltipFlag);
    }
}
