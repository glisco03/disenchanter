package com.glisco.disenchanter.compat.rei;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CatalystDisplay implements Display {

    public static final MapCodec<CatalystDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
            EntryIngredient.codec().listOf().fieldOf("catalysts").forGetter(display -> display.catalysts)
        ).apply(instance, CatalystDisplay::new);
    });

    public static final DisplaySerializer<CatalystDisplay> SERIALIZER = DisplaySerializer.of(
        MAP_CODEC,
        EntryIngredient.streamCodec().collect(PacketCodecs.toList()).xmap(CatalystDisplay::new, catalystDisplay -> catalystDisplay.catalysts)
    );

    private final List<EntryIngredient> catalysts;

    public CatalystDisplay(Item catalystItem, int count) {
        this.catalysts = Collections.singletonList(EntryIngredients.of(catalystItem, count));
    }

    private CatalystDisplay(List<EntryIngredient> catalysts) {
        this.catalysts = catalysts;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return catalysts;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return catalysts;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DisenchanterReiPlugin.CATALYST;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
