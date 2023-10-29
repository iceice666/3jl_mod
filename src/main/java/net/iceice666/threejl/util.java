package net.iceice666.threejl;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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
}
