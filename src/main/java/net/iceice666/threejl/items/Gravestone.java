package net.iceice666.threejl.items;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class Gravestone {

    public static final String IS_ITEM_AVOID_DROP = "is_item_avoid_drop";
    public static final String IS_TOTEM_OF_KEEP_INVENTORY = "is_totem_of_keep_inventory";
    public static final String IS_GRAVESTONE = "is_gravestone";

    // Private constructor to prevent instantiation
    private Gravestone() {
    }

    // Method to create a gravestone block with the player's inventory items upon death.
    public static boolean createGravestone(ServerPlayerEntity player) {

        // Retrieve player's inventory and position information.
        PlayerInventory playerInventory = player.getInventory();
        BlockPos blockPos = BlockPos.ofFloored(player.getPos());
        RegistryKey<World> dimension = player.getWorld().getRegistryKey();

        // Get the world object for the dimension the player is in.
        World world = player.getWorld();
        ServerWorld serverWorld = Objects.requireNonNull(world.getServer()).getWorld(dimension);

        // If the server world is null, return false indicating the gravestone couldn't be created.
        if (serverWorld == null) return false;

        // Find a suitable position for the gravestone by checking for air blocks.
        BlockPos gravestonePos = findSuitablePositionForGravestone(serverWorld, blockPos);

        // Prepare a chest block state with a northward facing direction.
        BlockState chestState = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH);

        // Initialize two chest entities for the gravestone.
        ChestBlockEntity chestEntity1 = new ChestBlockEntity(gravestonePos, chestState);
        ChestBlockEntity chestEntity2 = new ChestBlockEntity(gravestonePos.east(), chestState);

        // Initialize inventories for both chests.
        DefaultedList<ItemStack> chestInventory1 = DefaultedList.ofSize(27, ItemStack.EMPTY);
        DefaultedList<ItemStack> chestInventory2 = DefaultedList.ofSize(27, ItemStack.EMPTY);

        //  Distribute the player's items into the two chests.
        distributeItemsToChests(playerInventory, chestInventory1, chestInventory2);

        // Place the chests in the world and set their inventories if they have items.
        placeChestIfNotEmpty(serverWorld, gravestonePos, chestEntity1, chestInventory1);
        placeChestIfNotEmpty(serverWorld, gravestonePos.east(), chestEntity2, chestInventory2);

        // Notify the player of the gravestone's location.
        Text gravestoneMessage = Text.of("Your gravestone is generated at " + gravestonePos.toShortString());
        player.sendMessage(gravestoneMessage);

        return true;
    }

    // Method to find a suitable position for the gravestone
    private static BlockPos findSuitablePositionForGravestone(ServerWorld serverWorld, BlockPos blockPos) {
        while (true) {
            Block block = serverWorld.getBlockState(blockPos).getBlock();
            if (serverWorld.canSetBlock(blockPos) && block.equals(Blocks.AIR)) break;
            blockPos = blockPos.getY() > 320 ? blockPos.down() : blockPos.up();
        }
        return blockPos; // Return the found position
    }

    // Method to distribute items to chests using Java Streams
    private static void distributeItemsToChests(PlayerInventory playerInventory, DefaultedList<ItemStack> chestInventory1, DefaultedList<ItemStack> chestInventory2) {
        int chestInventory1Counter = 0;
        int chestInventory2Counter = 0;

        for (List<ItemStack> list : playerInventory.combinedInventory) {

            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);

                if (shouldAvoidDrop(itemStack)) continue;

                if (chestInventory1Counter < 27) {
                    chestInventory1.set(chestInventory1Counter, itemStack);
                    ++chestInventory1Counter;
                } else {
                    chestInventory2.set(chestInventory2Counter, itemStack);
                    ++chestInventory2Counter;
                }

                list.set(i, ItemStack.EMPTY);
            }
        }
    }

    //  Method to place a chest if it contains items
    private static void placeChestIfNotEmpty(ServerWorld serverWorld, BlockPos pos, ChestBlockEntity chestEntity, DefaultedList<ItemStack> chestInventory) {
        if (!chestInventory.isEmpty()) {
            chestEntity.setInvStackList(chestInventory);
            serverWorld.setBlockState(pos, chestEntity.getCachedState());
            serverWorld.addBlockEntity(chestEntity);
        }
    }

    // Helper method to check for NBT tag to avoid be dropping
    private static boolean shouldAvoidDrop(ItemStack itemStack) {
        // Empty items should not be dropping
        return itemStack.isEmpty()
                // Items which have IS_ITEM_AVOID_DROP nbt tag true should not be dropping
                || (itemStack.hasNbt() && (itemStack.getNbt().getBoolean(IS_ITEM_AVOID_DROP)
                // Gravestones should not be dropping
                || itemStack.getNbt().getBoolean(IS_GRAVESTONE)));
    }

    // Method to drop all items from the player's inventory.
    public static void dropAll(ServerPlayerEntity player) {
        // Cause the player to drop experience orbs.
        player.dropXp();

        // Drop all items that are not marked to avoid dropping.
        for (List<ItemStack> list : player.getInventory().combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);
                // Check NBT tags to determine if the item should be dropped.
                if (!(
                        itemStack.isEmpty()
                                || (
                                itemStack.hasNbt()
                                        && itemStack.getNbt().getBoolean(IS_ITEM_AVOID_DROP)
                        )
                )) {
                    player.dropItem(itemStack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
