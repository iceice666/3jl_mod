package net.iceice666.threejl.items;

import net.iceice666.threejl.ItemRegister;
import net.minecraft.util.ActionResult;

public class Dice implements ItemRegister.Item {
    @Override
    public ActionResult register() {
        return ActionResult.PASS;
    }
}
