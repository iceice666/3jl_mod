package net.iceice666.threejl.registers;
// This is the place you add your item here
// You may need some code template

/*

 */

// Just a reminder: This mod uses `carrot_on_a_stick` as a trigger item.
// Make sure have right texture(pack) for it.

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.iceice666.threejl.items.Dice;
import net.iceice666.threejl.items.artilleries.Missile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemRegister {
    public ItemRegister() {

        UseItemCallback.EVENT.register(Dice::register);
        UseItemCallback.EVENT.register(Missile::register);
    }

    public static class utils {
        public static boolean isPlayerInSurvival(PlayerEntity player) {
            return !player.isSpectator();
        }
    }


    public interface Item {
        static TypedActionResult<ItemStack> register(PlayerEntity player, World world, Hand hand) {
            return null;
        }
    }
}
