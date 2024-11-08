package com.glisco.disenchanter.client;

import com.glisco.disenchanter.Disenchanter;
import com.glisco.disenchanter.DisenchanterNetworking;
import com.glisco.disenchanter.DisenchanterScreenHandler;
import com.glisco.disenchanter.catalyst.Catalyst;
import com.glisco.disenchanter.catalyst.CatalystRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DisenchanterScreen extends HandledScreen<DisenchanterScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of("disenchanter", "textures/gui/container/disenchanter.png");

    private ButtonWidget disenchantButton = null;
    private boolean validCatalyst = true;

    public DisenchanterScreen(DisenchanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.backgroundHeight = 190;
        this.playerInventoryTitleY += 24;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        this.disenchantButton.active = this.handler.getSlot(0).hasStack() && this.handler.getSlot(1).hasStack()
                && this.handler.getSlot(0).getStack().hasEnchantments() && !this.handler.getSlot(3).hasStack()
                && this.validCatalyst;
    }

    @Override
    protected void handledScreenTick() {
        this.validCatalyst = (Disenchanter.getConfig().allowDisenchantingWithoutCatalyst && !this.handler.getSlot(2).hasStack()) || CatalystRegistry.get(this.handler.getSlot(2).getStack()) != Catalyst.DEFAULT;
    }

    public int getBaseX() {
        return this.x;
    }

    public int getBaseY() {
        return this.y;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

        this.disenchantButton = new ButtonWidget.Builder(Text.translatable("disenchanter.gui.button"), button ->
                DisenchanterNetworking.CHANNEL.clientHandle().send(new DisenchanterNetworking.DisenchantRequest())
        ).position(this.x + 46, this.y + 74).size(84, 20).build();
        this.disenchantButton.active = false;
        this.addDrawableChild(disenchantButton);
    }
}
