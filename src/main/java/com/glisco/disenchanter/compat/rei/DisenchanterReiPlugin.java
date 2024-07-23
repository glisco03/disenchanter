package com.glisco.disenchanter.compat.rei;

import com.glisco.disenchanter.Disenchanter;
import com.glisco.disenchanter.catalyst.CatalystRegistry;
import com.glisco.disenchanter.client.DisenchanterScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.util.Identifier;

public class DisenchanterReiPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<CatalystDisplay> CATALYST = CategoryIdentifier.of(Identifier.of(Disenchanter.MOD_ID, "catalyst"));

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        CatalystRegistry.forEach((item, catalystEntry) -> registry.add(new CatalystDisplay(item, catalystEntry.amount())));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> {
            return new Rectangle(screen.getBaseX() + 61, screen.getBaseY() + 25, 14, 33);
        }, DisenchanterScreen.class, CATALYST);

        registry.registerClickArea(screen -> {
            return new Rectangle(screen.getBaseX() + 101, screen.getBaseY() + 25, 14, 33);
        }, DisenchanterScreen.class, CATALYST);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CatalystCategory());
        registry.addWorkstations(CATALYST, EntryStacks.of(Disenchanter.DISENCHANTER_BLOCK));
    }
}
