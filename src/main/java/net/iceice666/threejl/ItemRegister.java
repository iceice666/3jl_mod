package net.iceice666.threejl;
// This is the place you add your item here
// You may need some code template

/*

 */

// Just a reminder: This mod use `carrot_on_a_stick` as a trigger item.
// Make sure have right texture(pack) for it.

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.ActionResult;

public class ItemRegister {
    public ItemRegister() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            return null;
        });
    }


    public interface Item {
        public ActionResult register();
    }
}
