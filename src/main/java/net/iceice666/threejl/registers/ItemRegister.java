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
import net.iceice666.threejl.items.artilleries.RocketLauncher;
import net.minecraft.entity.player.PlayerEntity;

public class ItemRegister {


    public ItemRegister() {

        UseItemCallback.EVENT.register(Dice::register);
        UseItemCallback.EVENT.register(Missile::register);
        UseItemCallback.EVENT.register(RocketLauncher::register);
    }

    public static class Utils {
        private Utils() {
        }
        public static boolean isPlayerInSurvival(PlayerEntity player) {
            return !player.isSpectator();
        }
    }


    public static class Item {
        private Item() {
        }

        public static final String IS_DISPOSABLE = "is_disposable";
    }
}
