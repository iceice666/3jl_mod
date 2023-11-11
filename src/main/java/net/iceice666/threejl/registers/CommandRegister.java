package net.iceice666.threejl.registers;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.iceice666.threejl.command.GiveCustomCommand;
import net.iceice666.threejl.command.KillMeCommand;
import net.iceice666.threejl.command.RedeemCodeCommand;


public class CommandRegister {
    public CommandRegister() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                    KillMeCommand.register(dispatcher);
                    GiveCustomCommand.register(dispatcher);
            RedeemCodeCommand.register(dispatcher);

                }
        );

    }


}
