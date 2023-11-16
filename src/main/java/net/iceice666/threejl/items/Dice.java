package net.iceice666.threejl.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;

public class Dice {
    public static final String IS_DICE = "is_dice";

    private Dice() {
    }

    public static TypedActionResult<ItemStack> register(PlayerEntity player) {
        if (!player.isSpectator()
                && !player.getMainHandStack().isEmpty()

        ) {
            ItemStack itemStack = player.getMainHandStack();
            if (
                    itemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            itemStack.hasNbt() && itemStack.getNbt().getBoolean(IS_DICE)
            ) {

                int random = ((int) (Math.random() * 6)) + 1;

                player.sendMessage(Text.of("You rolled " + random + "!"), false);


                return TypedActionResult.success(itemStack);

            }


        }
        return TypedActionResult.pass(ItemStack.EMPTY);
    }
}
