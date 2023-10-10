package net.iceice666.Command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;


// This is the place you add your command here
// You may need some code template

/*

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.Command;

public final class MyCommand {

    // This function defines your command
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    }

    // This function processes the command
    private static int execute(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
    }

    // Your can have more than 1 function to process your command
}
*/

public class CommandRegister {
    public CommandRegister() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> KillMeCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MailCommand.register(dispatcher));
    }
}
