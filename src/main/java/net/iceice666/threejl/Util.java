package net.iceice666.threejl;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

import java.util.Objects;

import static net.iceice666.threejl.registers.ItemRegister.Item.IS_DISPOSABLE;

public class Util {

    private Util() {
    }

    public static boolean doesContain(NbtCompound source, NbtCompound toCheck) {
        for (String key : toCheck.getKeys()) {
            if (!source.contains(key)) {
                return false; // Key is not present in the source
            }
            if (!Objects.equals(source.get(key), toCheck.get(key))) {
                return false; // Value is different for the key
            }
        }
        return true; // All keys and values are present in the source
    }


    public static boolean isHeldItemValid(PlayerEntity player, Hand hand, Item item, NbtCompound nbtCompound) {

        ItemStack itemStack;

        if (hand == Hand.MAIN_HAND) itemStack = player.getMainHandStack();
        else if (hand == Hand.OFF_HAND) itemStack = player.getOffHandStack();
        else return false;


        return itemStack.isOf(item) && doesContain(itemStack.getOrCreateNbt(), nbtCompound);

    }


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

    public static ItemStack damageItem(ItemStack item, int mount) {

        if (item.hasNbt() && item.getNbt().getBoolean("Unbreakable")) return item;

        item.setDamage(item.getDamage() + mount);

        if (item.getDamage() >= item.getMaxDamage() || (item.hasNbt() && item.getNbt().getBoolean(IS_DISPOSABLE))) {
            return ItemStack.EMPTY;
        }

        return item;

    }


    public static int[] checkPlayerInventoryContainsNbtItem(PlayerInventory playerInventory, String nbtString) {

        for (int i = 0; i < 3; ++i) {
            var list = playerInventory.combinedInventory.get(i);
            for (int j = 0; j < list.size(); ++j) {
                ItemStack itemStack = list.get(j);

                // If this totemSlot has an item, and it has nbt, and the AVOID_DROP key of it's nbt is not True => drop
                if (!itemStack.isEmpty() && itemStack.getNbt() != null && itemStack.getNbt().getBoolean(nbtString)) {
                    return new int[]{i, j};
                }
            }
        }

        return new int[]{-1};
    }
}
