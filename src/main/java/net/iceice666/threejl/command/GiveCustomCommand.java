package net.iceice666.threejl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.iceice666.threejl.Util.givePlayerItem;
import static net.iceice666.threejl.items.Dice.IS_DICE;
import static net.iceice666.threejl.items.artilleries.Missile.IS_MISSILE;
import static net.iceice666.threejl.items.artilleries.RocketLauncher.IS_ROCKET_LAUNCHER;
import static net.iceice666.threejl.items.gravestones.Gravestone.IS_GRAVESTONE;
import static net.iceice666.threejl.items.gravestones.Gravestone.IS_TOTEM_OF_KEEP_INVENTORY;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GiveCustomCommand {
    private GiveCustomCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("givecustom")
                .requires(source -> source.hasPermissionLevel(1))
                .then(generateCommandNode(IS_GRAVESTONE))
                .then(generateCommandNode(IS_TOTEM_OF_KEEP_INVENTORY))
                .then(generateCommandNode(IS_MISSILE))
                .then(generateCommandNode(IS_ROCKET_LAUNCHER))
                .then(generateCommandNode(IS_DICE))

        );


    }

    public static LiteralArgumentBuilder<ServerCommandSource> generateCommandNode(String cmd) {


        String finalCmd = cmd.startsWith("is_") ? cmd.substring(3) : cmd;

        return literal(finalCmd)
                .executes(context -> {

                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    givePlayerItem(player, getCustomItem(cmd,
                            1)
                    );

                    return 1;
                })

                .then(
                        argument("mount", integer(1, Items.CARROT_ON_A_STICK.getMaxDamage()))
                                .executes(context -> {

                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                                    givePlayerItem(player, getCustomItem(cmd,
                                            getInteger(context, "mount"))
                                    );

                                    return 1;
                                })
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
