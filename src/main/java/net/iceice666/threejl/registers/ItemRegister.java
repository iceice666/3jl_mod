package net.iceice666.threejl.registers;
// This is the place you add your item here
// You may need some code template

/*

 */

// Just a reminder: This mod uses `carrot_on_a_stick` as a trigger item.
// Make sure have right texture(pack) for it.

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.iceice666.threejl.items.Dice;
import net.iceice666.threejl.items.JesusPunch;
import net.iceice666.threejl.items.Toolbox;
import net.iceice666.threejl.items.artilleries.Missile;
import net.iceice666.threejl.items.artilleries.RocketLauncher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemRegister {


    public ItemRegister() {

        UseItemCallback.EVENT.register(
                (PlayerEntity player, World world, Hand hand)
                        -> Dice.register(player)
        );
        UseItemCallback.EVENT.register(
                (PlayerEntity player, World world, Hand hand) ->
                        Missile.register((ServerPlayerEntity) player, world, hand)
        );
        UseItemCallback.EVENT.register(
                RocketLauncher::register
        );
        UseItemCallback.EVENT.register(
                (PlayerEntity player, World world, Hand hand) ->
                        Toolbox.register((ServerPlayerEntity) player, hand)
        );


        UseEntityCallback.EVENT.register(
                (player, world, hand, entity, hitResult)
                        -> JesusPunch.register(player, entity, hitResult)
        );
    }


    public static class Item {
        public static final String IS_DISPOSABLE = "is_disposable";

        private Item() {
        }
    }
}
