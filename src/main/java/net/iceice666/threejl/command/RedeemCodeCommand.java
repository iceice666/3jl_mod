package net.iceice666.threejl.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.iceice666.threejl.Mod.LOGGER;
import static net.iceice666.threejl.Util.getCurrentUnixTimestamp;
import static net.iceice666.threejl.Util.givePlayerItem;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RedeemCodeCommand {
    private RedeemCodeCommand() {
    }

    public static void onServerStart() {

        RedeemCodeSystem.load(FabricLoader.getInstance().getConfigDir().resolve("redeemCodes.toml").toFile());
    }

    public static void onServerStop() {
        RedeemCodeSystem.save();
    }


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        dispatcher.register(
                literal("redeem")
                        .requires(source -> source.hasPermissionLevel(0))
                        .then(
                                argument("redeem_code", string())
                                        .executes(context ->
                                                executeRedeem(context.getSource(), getString(context, "redeem_code"))
                                        )
                        ));
    }

    private static int executeRedeem(ServerCommandSource source, String redeemCode) throws CommandSyntaxException {

        ServerPlayerEntity player = source.getPlayerOrThrow();

        List<ItemStack> rewards = RedeemCodeSystem.redeem(redeemCode);


        rewards.forEach(item -> givePlayerItem(player, item));


        return Command.SINGLE_SUCCESS;
    }


    static class RedeemCode {

        String code;
        String msg = "";
        Long expiredTime = (long) -1;
        int curUseCount = 0;
        int maxUseCount = -1;
        List<String> rewards;


        public List<ItemStack> getRewards() {
            ArrayList<ItemStack> results = new ArrayList<>();

            File temp = null;
            try {
                temp = Files.createTempFile("temp", ".nbt").toFile();
            } catch (IOException e) {
                LOGGER.error("Failed to load redeem codes!");
                e.printStackTrace((PrintStream) LOGGER);
            }

            File finalTemp = temp;
            rewards.forEach(itemNbtString -> {

                try (FileWriter fileWriter = new FileWriter(finalTemp);) {

                    fileWriter.write(itemNbtString);
                    fileWriter.flush();

                    NbtCompound nbtCompound = NbtIo.read(finalTemp);
                    results.add(ItemStack.fromNbt(nbtCompound));
                } catch (IOException e) {
                    e.printStackTrace((PrintStream) LOGGER);
                }

            });


            return results;
        }
    }


     static class RedeemCodeSystem {
        static HashMap<String, RedeemCode> redeemCodeList = new HashMap<>();

        static File savedDataFile;


        public static void load(File sdf) {
            savedDataFile = sdf;

            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, RedeemCode>>() {
            }.getType();
            try (FileReader reader = new FileReader(savedDataFile)) {
                redeemCodeList = gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace((PrintStream) LOGGER);
            }
        }

        public static List<ItemStack> redeem(String code) {
            if (!redeemCodeList.containsKey(code)) return Collections.emptyList();
            var rc = redeemCodeList.get(code);
            if (getCurrentUnixTimestamp() > rc.expiredTime) return Collections.emptyList();
            if (rc.maxUseCount > 0) ++rc.curUseCount;
            return rc.getRewards();
        }

        public static void save() {
            Gson gson = new Gson();
            String json = gson.toJson(redeemCodeList);
            try (FileWriter writer = new FileWriter(savedDataFile)) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace((PrintStream) LOGGER);
            }
        }

        public void add(RedeemCode rc) {
            redeemCodeList.put(rc.code, rc);
        }

        public RedeemCode remove(String key) {
            var rc = redeemCodeList.get(key);
            redeemCodeList.remove(key);
            return rc;
        }


    }
}
