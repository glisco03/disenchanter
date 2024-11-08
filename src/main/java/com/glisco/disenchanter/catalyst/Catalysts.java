package com.glisco.disenchanter.catalyst;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Catalysts {

    public static void registerDefaults() {
        CatalystRegistry.registerFromConfig(Items.EMERALD, new Emerald());
        CatalystRegistry.registerFromConfig(Items.DIAMOND, new Diamond());
        CatalystRegistry.registerFromConfig(Items.ENDER_PEARL, new EnderPearl());
        CatalystRegistry.registerFromConfig(Items.HEART_OF_THE_SEA, new HeartOfTheSea());
        CatalystRegistry.registerFromConfig(Items.AMETHYST_SHARD, new AmethystShard());
        CatalystRegistry.registerFromConfig(Items.NETHER_STAR, new NetherStar());
        CatalystRegistry.registerFromConfig(Items.EXPERIENCE_BOTTLE, new ExperienceBottle());
    }

    public static class Emerald implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            final var resultStack = new ItemStack(Items.ENCHANTED_BOOK);
            final var levelMap = input.getEnchantments();
            final var enchantments = new ArrayList<>(levelMap.getEnchantments());

            for (int i = 0; i < 2; i++) {
                if (enchantments.isEmpty()) break;
                final var addition = enchantments.remove(random.nextInt(enchantments.size()));
                resultStack.addEnchantment(addition, levelMap.getLevel(addition));
            }

            return resultStack;
        }
    }

    public static class Diamond implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            final var resultStack = new ItemStack(Items.ENCHANTED_BOOK);
            final var levelMap = input.getEnchantments();
            final var enchantments = new ArrayList<>(levelMap.getEnchantments());

            final var enchantment = enchantments.removeFirst();
            resultStack.addEnchantment(enchantment, levelMap.getLevel(enchantment));

            for (int i = 0; i < 2; i++) {
                if (enchantments.isEmpty()) break;
                final var addition = enchantments.remove(random.nextInt(enchantments.size()));
                resultStack.addEnchantment(addition, levelMap.getLevel(addition));
            }

            return resultStack;
        }
    }

    public static class EnderPearl implements Catalyst {

        @Nullable
        private EnchantmentLevelEntry enchantmentCache = null;

        @Override
        public ItemStack transformInput(ItemStack input, Random random) {
            var levelMap = input.getEnchantments();
            var enchantments = new ArrayList<>(levelMap.getEnchantments());

            final var removedEnchantment = enchantments.remove(random.nextInt(enchantments.size()));
            enchantmentCache = new EnchantmentLevelEntry(removedEnchantment, levelMap.getLevel(removedEnchantment));

            EnchantmentHelper.apply(input, builder -> builder.remove(entry -> entry.matches(removedEnchantment)));

            int damage = input.getDamage() + 500;
            if (damage >= input.getMaxDamage()) return ItemStack.EMPTY;

            input.setDamage(damage);
            return input;
        }

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            if (enchantmentCache == null) throw new IllegalStateException();
            resultStack.addEnchantment(enchantmentCache.enchantment, enchantmentCache.level);

            enchantmentCache = null;
            return resultStack;
        }
    }

    public static class HeartOfTheSea implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = new HashMap<RegistryEntry<Enchantment>, Integer>(Map.ofEntries(input.getEnchantments().getEnchantmentEntries().toArray(Map.Entry[]::new)));
            levelMap.forEach((enchantment, integer) -> levelMap.replace(enchantment, Math.max(1, integer - 1)));

            levelMap.forEach(resultStack::addEnchantment);
            return resultStack;
        }
    }

    public static class AmethystShard implements Catalyst {

        @Override
        public ItemStack transformInput(ItemStack input, Random random) {
            input.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
            return input;
        }

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = input.getEnchantments();
            var enchantment = levelMap.getEnchantments().iterator().next();

            resultStack.addEnchantment(enchantment, levelMap.getLevel(enchantment));

            return resultStack;
        }
    }

    public static class NetherStar implements Catalyst {

        @Override
        public ItemStack transformInput(ItemStack input, Random random) {
            input.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
            return input;
        }

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = input.getEnchantments();
            levelMap.getEnchantmentEntries().forEach(entry -> {
                resultStack.addEnchantment(entry.getKey(), entry.getIntValue());
            });

            return resultStack;
        }
    }

    public static class ExperienceBottle implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = input.getEnchantments();
            int maxLevel = levelMap.getEnchantmentEntries().stream().max((o1, o2) -> {
                if (Objects.equals(o1.getIntValue(), o2.getIntValue())) return 0;
                return o1.getIntValue() > o2.getIntValue() ? 1 : -1;
            }).map(Map.Entry::getValue).orElse(-1);

            levelMap.getEnchantmentEntries().stream().filter(entry -> entry.getIntValue() == maxLevel)
                .forEach(entry -> resultStack.addEnchantment(entry.getKey(), entry.getIntValue()));

            return resultStack;
        }
    }

}
