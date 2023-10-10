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


    // It contains all created mails
    private HashMap<UUID, Mail> mailList = new HashMap<>();

    // It contains Players' mail that created
    private HashMap</*Player*/UUID, /*Mail*/UUID> createdList = new HashMap<>();

    // It contains Players' mail that be sent
    private HashMap</*Player*/UUID, /*Mail*/UUID> sentList = new HashMap<>();

    // It contains Players' mail that received
    private HashMap</*Player*/UUID, /*Mail*/UUID> receivedList = new HashMap<>();

    // It contains Players' mail that in trash can
    private HashMap</*Player*/UUID, /*Mail*/UUID> trashList = new HashMap<>();


    // This function defines your command
    /*
     * Commands
     * /mail
     * -> Show info
     * /mail help
     * -> Show help
     * /mail list <Choice created|sent|received|trashcan>
     * -> Show list with given category
     * /mail create [String title] [String body] [Boolean attachOffhand]
     * -> Create a new mail with title
     * /mail update <Choice {createdList}> <Choice title|body> <String newValue>
     * /mail update <Choice {createdList}> <Choice attachment> add
     * /mail update <Choice {createdList}> <Choice attachment> remove
     * -> Update a created mail field with new value
     * /mail send <Choice {createdList}> <UUID PlayerUuid>
     * -> Send a mail to a player
     * /mail unsend <Choice {sentList}>
     * -> Unsend a mail in sentList
     * /mail remove <Choice created|received> <Choice {listItems}>
     * -> Move a mail to trashcan
     * /mail revert <Choice {trashList}>
     * -> Move a mail in trashcan back to created/received list
     * /mail clear
     * -> Clear trashcan
     *  */

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    }


    private static int executeList(ServerCommandSource source) throws CommandSyntaxException {
        return Command.SINGLE_SUCCESS;
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

    private static int executeUnsend(ServerCommandSource source) throws CommandSyntaxException {
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
