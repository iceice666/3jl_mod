package net.iceice666.threejl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TestModCommand {

    private TestModCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("testmod")
                        .executes(context -> execute(context.getSource())
                        ));
    }

    private static int execute(ServerCommandSource source) throws CommandSyntaxException {

        final PlayerEntity player = source.getPlayerOrThrow();
        var t = new NbtCompound();
        player.getMainHandStack().writeNbt(t);


        source.sendFeedback(() -> Text.literal(t.toString()), false);

        return Command.SINGLE_SUCCESS;
    }
}
