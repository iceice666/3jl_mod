package net.iceice666.threejl.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

import java.util.Iterator;

import static net.iceice666.threejl.Util.givePlayerItem;
import static net.iceice666.threejl.Util.isHeldItemValid;

public class Toolbox {

    // NBT key for identifying toolbox items
    public static final String IS_TOOLBOX_TOOL = "is_toolbox_tool";
    // Constant for indicating a failed action
    static final TypedActionResult<ItemStack> FAILED = TypedActionResult.pass(ItemStack.EMPTY);

    // Private constructor to prevent instantiation
    private Toolbox() {
    }

    // Method to get the toolbox item from the player's inventory
    static ItemStack getToolbox(PlayerEntity player) {
        for (ItemStack itemStack : player.getInventory().main) {
            if (
                    "toolbox".equalsIgnoreCase(itemStack.getName().getString()) &&
                            (
                                    itemStack.isOf(Items.SHULKER_BOX)
                                            || itemStack.isOf(Items.WHITE_SHULKER_BOX)
                                            || itemStack.isOf(Items.ORANGE_SHULKER_BOX)
                                            || itemStack.isOf(Items.MAGENTA_SHULKER_BOX)
                                            || itemStack.isOf(Items.LIGHT_BLUE_SHULKER_BOX)
                                            || itemStack.isOf(Items.YELLOW_SHULKER_BOX)
                                            || itemStack.isOf(Items.LIME_SHULKER_BOX)
                                            || itemStack.isOf(Items.PINK_SHULKER_BOX)
                                            || itemStack.isOf(Items.GRAY_SHULKER_BOX)
                                            || itemStack.isOf(Items.LIGHT_GRAY_SHULKER_BOX)
                                            || itemStack.isOf(Items.CYAN_SHULKER_BOX)
                                            || itemStack.isOf(Items.PURPLE_SHULKER_BOX)
                                            || itemStack.isOf(Items.BLUE_SHULKER_BOX)
                                            || itemStack.isOf(Items.BROWN_SHULKER_BOX)
                                            || itemStack.isOf(Items.GREEN_SHULKER_BOX)
                                            || itemStack.isOf(Items.RED_SHULKER_BOX)
                                            || itemStack.isOf(Items.BLACK_SHULKER_BOX)
                            )
            )
                return itemStack;
        }
        return Items.AIR.getDefaultStack();
    }

    // Method to handle the registration of toolbox actions
    public static TypedActionResult<ItemStack> register(ServerPlayerEntity player, Hand hand) {
        // Check if the action is performed with the main hand
        if (hand != Hand.MAIN_HAND)
            return FAILED;

        // Create NBT compound for toolbox identification
        var nbt = new NbtCompound();
        nbt.putBoolean(IS_TOOLBOX_TOOL, true);

        // Check if the held item is valid for toolbox action
        if (!isHeldItemValid(player, hand, Items.AIR, nbt))
            return FAILED;

        // Get the toolbox item from the player's inventory
        ItemStack shulkerboxItem = getToolbox(player);
        if (shulkerboxItem.isOf(Items.AIR))
            return FAILED;

        // Get the inventory of the toolbox item
        NbtList boxInventory = shulkerboxItem.getOrCreateSubNbt("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE);

        // Create NBT compound for the selected item
        NbtCompound mainhandItemNbt = new NbtCompound();
        player.getMainHandStack().writeNbt(mainhandItemNbt);
        int mainhandItemSlot = mainhandItemNbt.getCompound("tag").getInt("toolbox_slot");
        mainhandItemNbt.getCompound("tag").remove("toolbox_slot");

        int swappedItemIndex = player.isSneaking() ? mainhandItemSlot - 1 : mainhandItemSlot + 1;
        swappedItemIndex = (swappedItemIndex + boxInventory.size()) % boxInventory.size();

        // Get the swapped item from the toolbox
        NbtCompound swappedItemNbt = (NbtCompound) boxInventory.remove(swappedItemIndex);
        // Put the item back to the toolbox
        boxInventory.add(swappedItemIndex, mainhandItemNbt);


        // Re-order toolbox items and handle overflow
        Iterator<NbtElement> it = boxInventory.iterator();
        int i = -1;
        while (it.hasNext()) {
            NbtCompound itemNbt = (NbtCompound) it.next();
            var item = ItemStack.fromNbt(itemNbt);

            if (item.isOf(Items.AIR)) {
                it.remove();
                continue;
            }

            i++;

            if (i >= 27) { // Overflow!
                givePlayerItem(player, item);
                it.remove();
                player.sendMessage(Text.of("Something go wrongly cause your toolbox overflow!"));
                continue;
            }

            if (i == swappedItemIndex)
                continue;

            itemNbt.putInt("Slot", i);
        }

        // Convert NBT to ItemStack for the swapped item
        ItemStack convertedItem = ItemStack.fromNbt(swappedItemNbt);
        convertedItem.getOrCreateNbt().putBoolean(IS_TOOLBOX_TOOL, true);
        convertedItem.getNbt().putInt("toolbox_slot", swappedItemIndex);
        player.getInventory().setStack(player.getInventory().selectedSlot, convertedItem);

        // Update shulkerbox with the modified inventory
        shulkerboxItem.getOrCreateSubNbt("BlockEntityTag").put("Items", boxInventory);

        return TypedActionResult.pass(convertedItem);
    }
}
