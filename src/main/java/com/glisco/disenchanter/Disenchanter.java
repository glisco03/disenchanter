package com.glisco.disenchanter;

import com.glisco.disenchanter.catalyst.Catalysts;
import com.glisco.disenchanter.compat.config.DisenchanterConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class Disenchanter implements ModInitializer {

    public static final String MOD_ID = "disenchanter";
    public static final Identifier DISENCHANTER_HANDLER_ID = Identifier.of(MOD_ID, "disenchanter");

    public static final Block DISENCHANTER_BLOCK = new DisenchanterBlock(AbstractBlock.Settings.create().hardness(5f).requiresTool().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "disenchanter"))));
    public static final ScreenHandlerType<DisenchanterScreenHandler> DISENCHANTER_SCREEN_HANDLER;

    public static final TagKey<Item> BLACKLIST = TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "blacklist"));

    private static DisenchanterConfig CONFIG;

    static {
        DISENCHANTER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, DISENCHANTER_HANDLER_ID, new ScreenHandlerType<>(DisenchanterScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES));
    }

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "disenchanter"), DISENCHANTER_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "disenchanter"), new BlockItem(DISENCHANTER_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "disenchanter")))));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.addAfter(Items.ENCHANTING_TABLE, DISENCHANTER_BLOCK));

        AutoConfig.register(DisenchanterConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DisenchanterConfig.class).get();

        Catalysts.registerDefaults();

        DisenchanterNetworking.init();
    }

    public static DisenchanterConfig getConfig() {
        return CONFIG;
    }
}
