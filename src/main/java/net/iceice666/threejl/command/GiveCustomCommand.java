package net.iceice666.threejl.command;

import com.mojang.brigadier.CommandDispatcher;
import net.iceice666.threejl.registers.CommandRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.iceice666.threejl.other.Gravestone.IS_GRAVESTONE_NBT_KEY;
import static net.iceice666.threejl.other.Gravestone.IS_TOTEM_OF_KEEP_INVENTORY_NBT_KEY;
import static net.iceice666.threejl.util.givePlayerItem;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GiveCustomCommand implements CommandRegister.Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("givecustom")
                .requires(source -> source.hasPermissionLevel(1))
                .then(literal("totem_of_keep_inventory")
                        .executes(context -> {

                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            givePlayerItem(player, getCustomItem(IS_TOTEM_OF_KEEP_INVENTORY_NBT_KEY,
                                    1)
                            );

                            return 1;
                        })

                        .then(
                                argument("mount", integer(1, Items.CARROT_ON_A_STICK.getMaxDamage()))
                                        .executes(context -> {

                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                                            givePlayerItem(player, getCustomItem(IS_TOTEM_OF_KEEP_INVENTORY_NBT_KEY,
                                                    getInteger(context, "mount"))
                                            );

                                            return 1;
                                        })
                        )
                )

                .then(literal("gravestone")
                        .executes(context -> {

                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            givePlayerItem(player, getCustomItem(IS_GRAVESTONE_NBT_KEY,
                                    1)
                            );

                            return 1;
                        })

                        .then(
                                argument("mount", integer(1, Items.CARROT_ON_A_STICK.getMaxDamage()))
                                        .executes(context -> {

                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                                            givePlayerItem(player, getCustomItem(IS_GRAVESTONE_NBT_KEY,
                                                    getInteger(context, "mount"))
                                            );

                                            return 1;
                                        })
                        )
                )
        );


    }


    public static ItemStack getCustomItem(String nbtKey, int mount) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean(nbtKey, true);
        nbtCompound.putInt("RepairCost", 114514);


        ItemStack itemStack = Items.CARROT_ON_A_STICK.getDefaultStack();
        itemStack.setCount(1);
        itemStack.setNbt(nbtCompound);
        itemStack.setDamage(itemStack.getMaxDamage() - mount);


        return itemStack;
    }


}
