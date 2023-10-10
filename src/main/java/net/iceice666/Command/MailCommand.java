package net.iceice666.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class MailCommand {

    // It contains all created mail
    private HashMap<UUID, Mail> MailList = new HashMap<>();

    // This function defines your command
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    }

    // This function create a new mail
    private static int executeCreate(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
    }

    // This function delete a new mail
    private static int executeDelete(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
    }

    // This function will read the mail and prints to receiver
    private static int executeRead(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
    }

    // This function will send the mail
    private static int executeSend(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRevert(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
    }

    // This function will revert a mail that be sent

    public class Mail {
        private final UUID uuid = UUID.randomUUID();
        public String title;
        public String content;
        public ArrayList<Item> attachment = new ArrayList<>();

        public UUID getUuid() {
            return uuid;
        }

        public void changeTitle(String new_title) {
            title = new_title;
        }

        public void changeContent(String new_content) {
            content = new_content;
        }

    }

}
