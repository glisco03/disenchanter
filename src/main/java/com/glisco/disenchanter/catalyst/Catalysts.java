package com.glisco.disenchanter.catalyst;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
            final var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            final var enchantments = new ArrayList<>(levelMap.keySet());

            for (int i = 0; i < 2; i++) {
                if (enchantments.isEmpty()) break;
                final var addition = enchantments.remove(random.nextInt(enchantments.size()));
                EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(addition, levelMap.get(addition)));
            }

            return resultStack;
        }
    }

    public static class Diamond implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            final var resultStack = new ItemStack(Items.ENCHANTED_BOOK);
            final var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            final var enchantments = new ArrayList<>(levelMap.keySet());

            final var enchantment = enchantments.remove(0);
            EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(enchantment, levelMap.get(enchantment)));

            for (int i = 0; i < 2; i++) {
                if (enchantments.isEmpty()) break;
                final var adddition = enchantments.remove(random.nextInt(enchantments.size()));
                EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(adddition, levelMap.get(adddition)));
            }

            return resultStack;
        }
    }

    public static class EnderPearl implements Catalyst {

        @Nullable
        private EnchantmentLevelEntry enchantmentCache = null;

        @Override
        public ItemStack transformInput(ItemStack input, Random random) {
            var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            var enchantments = new ArrayList<>(levelMap.keySet());

            final var removedEnchantment = enchantments.remove(random.nextInt(enchantments.size()));
            enchantmentCache = new EnchantmentLevelEntry(removedEnchantment, levelMap.get(removedEnchantment));

            levelMap.remove(removedEnchantment);
            EnchantmentHelper.set(levelMap, input);

            int damage = input.getDamage() + 500;
            if (damage >= input.getMaxDamage()) return ItemStack.EMPTY;

            input.setDamage(damage);
            return input;
        }

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            if (enchantmentCache == null) throw new IllegalStateException();
            EnchantedBookItem.addEnchantment(resultStack, enchantmentCache);

            enchantmentCache = null;
            return resultStack;
        }
    }

    public static class HeartOfTheSea implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            levelMap.forEach((enchantment, integer) -> levelMap.replace(enchantment, Math.max(1, integer - 1)));

            levelMap.forEach((enchantment, integer) -> EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(enchantment, integer)));
            return resultStack;
        }
    }

    public static class AmethystShard implements Catalyst {

        @Override
        public ItemStack transformInput(ItemStack input, Random random) {
            EnchantmentHelper.set(new HashMap<>(), input);
            return input;
        }

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            var enchantment = levelMap.keySet().iterator().next();

            EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(enchantment, levelMap.get(enchantment)));

            return resultStack;
        }
    }

    public static class NetherStar implements Catalyst {

        @Override
        public ItemStack transformInput(ItemStack input, Random random) {
            EnchantmentHelper.set(new HashMap<>(), input);
            return input;
        }

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            levelMap.forEach((enchantment, integer) -> EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(enchantment, integer)));

            return resultStack;
        }
    }

    public static class ExperienceBottle implements Catalyst {

        @Override
        public ItemStack generateOutput(ItemStack input, Random random) {
            var resultStack = new ItemStack(Items.ENCHANTED_BOOK);

            var levelMap = EnchantmentHelper.fromNbt(input.getEnchantments());
            int maxLevel = levelMap.entrySet().stream().max((o1, o2) -> {
                if (Objects.equals(o1.getValue(), o2.getValue())) return 0;
                return o1.getValue() > o2.getValue() ? 1 : -1;
            }).map(Map.Entry::getValue).orElse(-1);

            levelMap.entrySet().stream().filter(entry -> entry.getValue() == maxLevel)
                    .forEach(entry -> EnchantedBookItem.addEnchantment(resultStack, new EnchantmentLevelEntry(entry.getKey(), entry.getValue())));

            return resultStack;
        }
    }

}
