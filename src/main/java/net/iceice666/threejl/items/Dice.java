package net.iceice666.threejl.items;

import net.iceice666.threejl.ItemRegister;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static net.iceice666.threejl.ItemRegister.isPlayerInSurvival;

public class Dice implements ItemRegister.Item {


    public static TypedActionResult<ItemStack> register(PlayerEntity player, World world, Hand hand) {
        if (isPlayerInSurvival(player)
                && !player.getMainHandStack().isEmpty()

        ) {
            ItemStack itemStack = player.getMainHandStack();
            if (itemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK)) {

                NbtCompound itemNbt = itemStack.getNbt();
                if (itemNbt != null) {
                    int n = itemNbt.getInt("CustomModelData");

                    if (n == 2) {

                        int random = ((int) (Math.random() * 6)) + 1;

                        player.sendMessage(Text.of("You rolled " + random + "!"), false);
                    }

                    return TypedActionResult.success(ItemStack.EMPTY);
                }
            }


        }
        return TypedActionResult.pass(ItemStack.EMPTY);
    }
}
