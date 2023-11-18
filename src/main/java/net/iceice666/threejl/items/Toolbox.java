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

    public static final String IS_TOOLBOX_TOOL = "is_toolbox_tool";
    static final TypedActionResult<ItemStack> FAILED = TypedActionResult.pass(ItemStack.EMPTY);

    private Toolbox() {
    }

    static ItemStack getToolbox(PlayerEntity player) {

        for (ItemStack itemStack : player.getInventory().main) {
            if (
                    "toolbox".equalsIgnoreCase(itemStack.getName().getString())
                            && itemStack.isOf(Items.SHULKER_BOX)
            ) return itemStack;
        }

        return Items.AIR.getDefaultStack();
    }

    public static TypedActionResult<ItemStack> register(ServerPlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) return FAILED;

        var nbt = new NbtCompound();
        nbt.putBoolean(IS_TOOLBOX_TOOL, true);

        if (!isHeldItemValid(player, hand, Items.AIR, nbt)) return FAILED;

        ItemStack shulkerboxItem = getToolbox(player);
        if (shulkerboxItem.isOf(Items.AIR)) return FAILED;

        NbtList boxInventory = (NbtList) shulkerboxItem.getNbt().getCompound("BlockEntityTag").get("Items");

        if (boxInventory == null)
            boxInventory = new NbtList();

        // Get selected item nbt
        NbtCompound mainhandItemNbt = new NbtCompound();
        player.getMainHandStack().writeNbt(mainhandItemNbt);
        // remove toolbox_slot nbt
        mainhandItemNbt.getCompound("tag").remove("toolbox_slot");
        // Get selected item slot of toolbox
        int mainhandItemSlot = mainhandItemNbt.getCompound("tag").getInt("toolbox_slot");
        // Put item back to toolbox
        boxInventory.add(mainhandItemSlot, mainhandItemNbt);


        // Get swapped item nbt
        NbtCompound swappedItemNbt;
        int swappedItemIndex;

        if (player.isSneaking())
            swappedItemIndex = mainhandItemSlot - 1;
        else
            swappedItemIndex = mainhandItemSlot + 1;

        if (swappedItemIndex >= boxInventory.size())
            swappedItemIndex = 0;
        else if (swappedItemIndex < 0)
            swappedItemIndex = boxInventory.size() - 1;

        swappedItemNbt = (NbtCompound) boxInventory.remove(swappedItemIndex);


        Iterator<NbtElement> it = boxInventory.iterator();
        // Re-order toolbox items
        for (int i = 0; it.hasNext(); i++) {
            NbtCompound itemNbt = (NbtCompound) it.next();

            if (i >= 27) { // Overflow!
                givePlayerItem(player, ItemStack.fromNbt(itemNbt));
                it.remove();
                player.sendMessage(Text.of("Please ensure at least 1 empty slot in your toolbox!"));
                continue;
            }

            itemNbt.putInt("Slot", i);

        }


        // Convert nbt to ItemStack
        ItemStack convertedItem = ItemStack.fromNbt(swappedItemNbt);
        // Add IS_TOOLBOX_TOOL nbt
        convertedItem.getOrCreateNbt().putBoolean(IS_TOOLBOX_TOOL, true);
        // Add slot info
        convertedItem.getNbt().putInt("toolbox_slot", swappedItemIndex);
        // Add to selected item slot
        player.getInventory().setStack(player.getInventory().selectedSlot, convertedItem);

        // Update shulkerbox
        shulkerboxItem.getNbt().getCompound("BlockEntityTag").put("Items", boxInventory);

        return TypedActionResult.pass(convertedItem);
    }
}
