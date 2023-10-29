package net.iceice666.threejl.command;

import com.mojang.brigadier.CommandDispatcher;
import net.iceice666.threejl.registers.CommandRegister;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.iceice666.threejl.other.KeepInventory.TOTEM_OF_KEEP_INVENTORY_ITEM;
import static net.iceice666.threejl.util.givePlayerItem;
import static net.minecraft.server.command.CommandManager.literal;

public class GiveCustomCommand implements CommandRegister.Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("givecustom")
                .requires(source -> source.hasPermissionLevel(1))
                .then(literal("totem_of_keep_inventory")
                        .executes(context -> {

                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                            givePlayerItem(player, TOTEM_OF_KEEP_INVENTORY_ITEM);

                            return 1;
                        })
                )
        );


    }


}
