package com.glisco.disenchanter;

import com.glisco.disenchanter.catalyst.Catalyst;
import com.glisco.disenchanter.catalyst.CatalystRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class DisenchanterScreenHandler extends ScreenHandler {

    private final ScreenHandlerContext context;
    private final Inventory inventory;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public DisenchanterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public DisenchanterScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Disenchanter.DISENCHANTER_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(4);
        this.context = context;
        this.addSlot(new Slot(inventory, 0, 17, 76) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.hasEnchantments() && !stack.isIn(Disenchanter.BLACKLIST);
            }
        });
        this.addSlot(new Slot(inventory, 1, 143, 76) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof BookItem;
            }
        });
        this.addSlot(new Slot(inventory, 2, 80, 49) {
            public boolean canInsert(ItemStack stack) {
                return CatalystRegistry.isCatalyst(stack.getItem());
            }
        });
        this.addSlot(new Slot(inventory, 3, 80, 22) {
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        //This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        int m;
        int l;
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 108 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 166));
        }

    }

    public void onDisenchantRequest() {
        var contOpt = context.get((world, blockPos) -> world);
        if (contOpt.isEmpty()) return;

        final var world = contOpt.get();
        final var catalystStack = inventory.getStack(2);
        if (!Disenchanter.getConfig().allowDisenchantingWithoutCatalyst && catalystStack.isEmpty()) return;

        final var catalyst = CatalystRegistry.get(catalystStack);
        var processedInput = catalyst.transformInput(inventory.getStack(0).copy(), world.random);

        inventory.setStack(3, catalyst.generateOutput(inventory.getStack(0).copy(), world.random));
        inventory.setStack(0, processedInput);

        decrement(inventory, 1);
        if (catalyst != Catalyst.DEFAULT) decrement(inventory, 2, CatalystRegistry.getRequiredItemCount(catalyst));

        this.sendContentUpdates();

        //noinspection OptionalGetWithoutIsPresent
        var pos = context.get((w, blockPos) -> blockPos).get();

        var buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        var players = PlayerLookup.tracking((ServerWorld) world, pos);
        players.forEach(player -> ServerPlayNetworking.send(player, new Identifier(Disenchanter.MOD_ID, "disenchant_event"), buf));
    }

    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, Disenchanter.DISENCHANTER_BLOCK);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, this.inventory));
    }

    private static void decrement(Inventory inv, int idx) {
        decrement(inv, idx, 1);
    }

    private static void decrement(Inventory inv, int idx, int by) {
        inv.getStack(idx).decrement(by);
        if (inv.getStack(idx).isEmpty()) inv.setStack(idx, ItemStack.EMPTY);
    }
}