package net.iceice666.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public final class KillMeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("killme")
                        .requires(source -> source.hasPermissionLevel(0))
                        .executes(context -> execute(context.getSource())
                        ));
    }

    private static int execute(ServerCommandSource source) throws CommandSyntaxException {

        final PlayerEntity player = source.getPlayerOrThrow();

        player.kill();

        source.sendFeedback(() -> Text.literal("You have been killed!"), false);

        return 1;
    }
}
