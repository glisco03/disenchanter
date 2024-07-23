package com.glisco.disenchanter.catalyst;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface Catalyst {

    Catalyst DEFAULT = (input, random) -> {
        final var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

        var targetEnchantment = input.getEnchantments().getEnchantments().iterator().next();
        var level = input.getEnchantments().getLevel(targetEnchantment);

        resultStack.addEnchantment(targetEnchantment, level);

        return resultStack;
    };

    default ItemStack transformInput(ItemStack input, Random random) {
        return ItemStack.EMPTY;
    }

    ItemStack generateOutput(ItemStack input, Random random);
}
