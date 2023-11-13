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
       private String code;
       private String msg="";
       private Long expiredTime=-1;
       private int curUseCount=0;
       private int maxUseCount = -1;

      private  List<ItemStack> rewards;

        public String getCode() {return this.code;}
        
        public String getMsg() {return this.msg;}
        public void setMsg(String s ) {this.msg = s;}
        
        public Long getExpiredTime(){return this.expiredTime;}
        public void setExpiredTime(Long l){this.expiredTime=l;}
        
        public int getCurUseCount(){return this.curUseCount;}
        public void setCurUseCount(int l){this.curUseCount=i;}
        
        public int getMaxUseCount(){return this.maxUseCount;}
        public void setMaxUseCount(int l){this.maxUseCount=i;}


        public RedeemCode(String code) {
            this.code = code;
        }
    }


  class RedeemCodeSystem {
    HashMap<String, RedemCode> redeemCodeList = new HashMap<>();

    public RedeemCodeSystem(){}

    void add(RedeemCode rc){
        redeemCodeList.put(rc.getCode(), rc);
    }

    RedeemCode remove(String key){
        var rc = redeemCodeList.get(key);
        redeemCodeList.remove(key);
        return rc;
    }
    
    List<ItemStack> redeem(String code){
        if (!redeemCodeList.contains(code)) return null;
        var rc = redeemCodeList.get(code);
        if (getCurrentUnixTimeStamp() >rc.getExpiredTime() ) return null;
        if (rc.getMaxUseCount() >0) rc.setCurUseCount(rc.getCurUseCount+1);
        return rc.getRewards();
    }

      
  }
}
