package net.iceice666.threejl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RedeemCodeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("redeem")
                        .requires(source -> source.hasPermissionLevel(0))
                        .then(
                                argument("redeem_code", string())
                                        .executes(context ->
                                                execute(context.getSource(), getString(context, "redeem_code"))
                                        )
                        ));
    }

    private static int execute(ServerCommandSource source, String redeemCode) throws CommandSyntaxException {

        PlayerEntity player = source.getPlayerOrThrow();


        return Command.SINGLE_SUCCESS;
    }


    class RedeemCode {
        @NotNull
        String code;
        String msg;
        Long expireTime;
        int maxUseCount = -1;

        List<ItemStack> rewards;


        public RedeemCode(String code) {
            this.code = code;
        }
    }


  class RedeemCodeSystem {
    HashMap<String, RedemCode> redeemCodeList = new HashMap<>();

    public RedeemCodeSystem(){}

    void add(RedeemCode rc){
redeemCodeList.put(rc.code, rc);
    }
  }
}
