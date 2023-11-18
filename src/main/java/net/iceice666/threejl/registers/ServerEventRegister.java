package net.iceice666.threejl.registers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.iceice666.threejl.command.RedeemCodeCommand;

public class ServerEventRegister {

    public ServerEventRegister() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> RedeemCodeCommand.onServerStart()
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(
                server -> RedeemCodeCommand.onServerStop()
        );
    }
}
