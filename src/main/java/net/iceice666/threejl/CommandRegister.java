package net.iceice666.threejl;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.iceice666.threejl.command.KillMeCommand;
import net.minecraft.server.command.ServerCommandSource;


public class CommandRegister {
    public CommandRegister() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                KillMeCommand.register(dispatcher)
        );

    }


    public interface Command {

        public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        }


    }
}