package net.iceice666.threejl.other;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static net.iceice666.threejl.util.givePlayerItem;

public class Gravestone {

    public static final String AVOID_DROP_NBT_KEY = "is_item_avoid_drop";
    public static final String IS_TOTEM_OF_KEEP_INVENTORY_NBT_KEY = "is_totem_of_keep_inventory";
    public static final String IS_GRAVESTONE_NBT_KEY = "is_gravestone";
    public static Set<String> whitelist = Set.of(
            "22bd9801-acd6-4e7e-ab14-9f53df1f42f7" // @jack0301
            , "75f6ec8c-6339-4a88-84d8-34afe4a38a1d" //@KSHSlime
    );


    public static boolean createGravestone(ServerPlayerEntity player) {


        // Get basic info
        PlayerInventory playerInventory = player.getInventory();

        BlockPos blockPos = BlockPos.ofFloored(player.getPos());
        RegistryKey<World> dimension = player.getWorld().getRegistryKey();

        World world = player.getWorld();
        ServerWorld serverWorld = Objects.requireNonNull(world.getServer()).getWorld(dimension);


        if (serverWorld == null) return false;

        while (true) {
            Block block = serverWorld.getBlockState(blockPos).getBlock();
            if (serverWorld.canSetBlock(blockPos) && block.equals(Blocks.AIR)) break;
            blockPos = blockPos.up();
        }


        BlockState chestState = Blocks.CHEST.getDefaultState();
        chestState.with(ChestBlock.FACING, Direction.NORTH);

        ChestBlockEntity chestEntity1 = new ChestBlockEntity(blockPos, chestState);
        ChestBlockEntity chestEntity2 = new ChestBlockEntity(blockPos.east(), chestState);

        DefaultedList<ItemStack> chestInventory1 = DefaultedList.ofSize(27, ItemStack.EMPTY);
        DefaultedList<ItemStack> chestInventory2 = DefaultedList.ofSize(27, ItemStack.EMPTY);

        int chestInventory1Counter = 0;
        int chestInventory2Counter = 0;


        for (List<ItemStack> list : playerInventory.combinedInventory) {

            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);

                if (itemStack.isEmpty() || (itemStack.hasNbt() && itemStack.getNbt().getBoolean(AVOID_DROP_NBT_KEY)))
                    continue;

                if (chestInventory1Counter <= 27) {
                    chestInventory1.set(chestInventory1Counter, itemStack);
                    ++chestInventory1Counter;
                } else {
                    chestInventory2.set(chestInventory2Counter, itemStack);
                    ++chestInventory2Counter;
                }

                list.set(i, ItemStack.EMPTY);
            }
        }


        chestEntity1.setInvStackList(chestInventory1);
        chestEntity2.setInvStackList(chestInventory2);

        serverWorld.setBlockState(blockPos, chestEntity1.getCachedState());
        serverWorld.setBlockState(blockPos.east(), chestEntity2.getCachedState());


        CompassItem compass = (CompassItem) Items.COMPASS;
        compass.writeNbt(serverWorld.getRegistryKey(), blockPos, compass.getDefaultStack().getOrCreateNbt());
        givePlayerItem(player, compass.getDefaultStack());

        return true;

    }


    public static void dropAll(ServerPlayerEntity player) {
        player.dropXp();


        for (List<ItemStack> list : player.getInventory().combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);

                // If this totemSlot has an item, and it doesn't have nbt,
                // or it has nbt, and the AVOID_DROP key of it's nbt is not True
                // => drop
                if (
                        !itemStack.isEmpty()
                                && (
                                itemStack.getNbt() == null
                                        || !itemStack.getNbt().getBoolean(AVOID_DROP_NBT_KEY)
                        )
                ) {
                    player.dropItem(itemStack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }
    }


}