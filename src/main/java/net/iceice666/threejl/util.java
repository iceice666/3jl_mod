package net.iceice666.threejl;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class util {


    public static void givePlayerItem(ServerPlayerEntity player, ItemStack item) {
        ItemEntity itemEntity;
        boolean bl = player.getInventory().insertStack(item);

        if (!bl || !item.isEmpty()) {
            itemEntity = player.dropItem(item, false);
            if (itemEntity == null) return;
            itemEntity.resetPickupDelay();
            itemEntity.setOwner(player.getUuid());
            return;
        }

        item.setCount(1);
        itemEntity = player.dropItem(item, false);
        if (itemEntity != null) {
            itemEntity.setDespawnImmediately();
        }
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS,
                0.2f,
                ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
        player.currentScreenHandler.sendContentUpdates();
    }


    public static int checkPlayerInventoryContainsNbtItem(PlayerInventory playerInventory, String nbtString) {
        int slot = -1;
        for (List<ItemStack> list : playerInventory.combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);

                // If this totemSlot has an item, and it has nbt, and the AVOID_DROP key of it's nbt is not True => drop
                if (!itemStack.isEmpty() && itemStack.getNbt() != null && itemStack.getNbt().getBoolean(nbtString)) {
                    slot = i;
                    break;
                }
            }

            if (slot != -1) break;
        }

        return slot;
    }
}
