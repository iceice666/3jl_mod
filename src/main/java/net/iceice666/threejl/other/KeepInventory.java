package net.iceice666.threejl.other;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.Set;

public class KeepInventory {

    public static final ItemStack TOTEM_OF_KEEP_INVENTORY_ITEM = getTotemOfKeepInventoryItem();
    public static Set<String> whitelist = Set.of(
            "22bd9801-acd6-4e7e-ab14-9f53df1f42f7" // @jack0301
            , "75f6ec8c-6339-4a88-84d8-34afe4a38a1d" //@KSHSlime
    );

    private static ItemStack getTotemOfKeepInventoryItem() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("is_totem_of_keep_inventory", true);
        ItemStack itemStack = Items.TOTEM_OF_UNDYING.getDefaultStack();
        itemStack.setNbt(nbtCompound);

        return itemStack;
    }


}
