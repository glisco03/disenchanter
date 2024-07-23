package com.glisco.disenchanter;

import io.wispforest.owo.network.ClientAccess;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class DisenchanterNetworking {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Identifier.of(Disenchanter.MOD_ID, "main"));

    public static void init() {
        CHANNEL.registerClientboundDeferred(DisenchantEvent.class);
        CHANNEL.registerServerbound(DisenchantRequest.class, (message, access) -> {
            if (!(access.player().currentScreenHandler instanceof DisenchanterScreenHandler disenchanter)) return;
            disenchanter.onDisenchantRequest();
        });
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        //noinspection Convert2MethodRef
        CHANNEL.registerClientbound(DisenchantEvent.class, (message, access) -> executeDisenchantRequest(message, access));
    }

    @Environment(EnvType.CLIENT)
    private static void executeDisenchantRequest(DisenchantEvent message, ClientAccess access) {
        var client = access.runtime();
        var pos = message.pos;

        client.world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.BLOCKS, .5f, .8f + client.world.random.nextFloat() * .4f, false);
        for (int i = 0; i < 100; i++) {
            client.world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.685, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    public record DisenchantEvent(BlockPos pos) {}

    public record DisenchantRequest() {}
}
