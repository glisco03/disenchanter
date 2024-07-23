package com.glisco.disenchanter.client;

import com.glisco.disenchanter.Disenchanter;
import com.glisco.disenchanter.DisenchanterNetworking;
import com.glisco.disenchanter.DisenchanterScreenHandler;
import com.glisco.disenchanter.VisitableTextContent;
import com.glisco.disenchanter.catalyst.Catalyst;
import com.glisco.disenchanter.catalyst.CatalystRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class DisenchanterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(Disenchanter.DISENCHANTER_SCREEN_HANDLER, DisenchanterScreen::new);

        DisenchanterNetworking.initClient();

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (MinecraftClient.getInstance().player == null) return;
            if (!(MinecraftClient.getInstance().player.currentScreenHandler instanceof DisenchanterScreenHandler handler)) {
                return;
            }

            if (stack.isIn(Disenchanter.BLACKLIST)) {
                lines.add(1, Text.translatable("text.disenchanter.blacklisted").formatted(Formatting.DARK_GRAY));
            }

            final var catalyst = CatalystRegistry.getUnchecked(stack);
            if (catalyst == Catalyst.DEFAULT) return;

            final var catalystStack = handler.getSlot(2).getStack();
            if (!catalystStack.isEmpty() && stack == catalystStack) {
                int requiredCount = CatalystRegistry.getRequiredItemCount(catalyst);
                if (catalystStack.getCount() < requiredCount) {
                    lines.add(Text.translatable("text.disenchanter.extra_catalysts_required", requiredCount - catalystStack.getCount())
                            .formatted(Formatting.RED));
                }
            }

            if (catalyst != null) {
                MinecraftClient.getInstance().textRenderer.getTextHandler()
                        .wrapLines(
                                Text.translatable("disenchanter.catalyst." + stack.getItem().getRegistryEntry().registryKey().getValue().getPath()),
                                200,
                                Style.EMPTY.withColor(Formatting.DARK_GRAY)
                        )
                        .stream()
                        .map(VisitableTextContent::new)
                        .map(MutableText::of)
                        .forEach(lines::add);
            }
        });
    }

}
