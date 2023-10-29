package net.iceice666.threejl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.iceice666.threejl.registers.CommandRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.iceice666.threejl.other.KeepInventory.TOTEM_OF_KEEP_INVENTORY_ITEM;
import static net.minecraft.server.command.CommandManager.literal;

public class CheckCommand implements CommandRegister.Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        dispatcher.register(literal("check")
                .requires(source -> source.hasPermissionLevel(1))
                .executes(context -> execute(context.getSource()))

                .then(literal("totem_of_keep_inventory")
                        .executes(context -> executeCheckTotemOfKeepInventory(context.getSource()))
                )


        );
    }


    private static int execute(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();

        ItemStack mainHandStack = player.getInventory().getMainHandStack();

        player.sendMessage(Text.translatable(mainHandStack.getTranslationKey()));
        if (mainHandStack.getNbt() != null) {
            player.sendMessage(Text.of(mainHandStack.getNbt().toString()));


        }


        return 1;
    }


    private static int executeCheckTotemOfKeepInventory(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ItemStack mainHandStack = player.getInventory().getMainHandStack();
        player.sendMessage(
                Text.of(
                        String.valueOf(
                                ItemStack.areEqual(
                                        mainHandStack, TOTEM_OF_KEEP_INVENTORY_ITEM
                                ))));

        return 1;
    }
}
