package net.iceice666.threejl.mixin;


import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.iceice666.threejl.other.KeepInventory.TOTEM_OF_KEEP_INVENTORY_ITEM;
import static net.iceice666.threejl.other.KeepInventory.whitelist;

@Mixin(ServerPlayerEntity.class)
public abstract class LivingEntityMixin {


    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void replaceWithGrave(ServerPlayerEntity player, DamageSource damageSource) {

        if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) player.drop(damageSource);

        if (
                whitelist.contains(player.getUuidAsString())
        ) {

        } else if (player.getInventory().contains(TOTEM_OF_KEEP_INVENTORY_ITEM)) {


            player.getInventory().removeStack(player.getInventory().indexOf(TOTEM_OF_KEEP_INVENTORY_ITEM));


//            player.sendMessage(Text.of("U saved ur items bcuz of totem of keep inventory!"), false);

        } else {

//            player.sendMessage(Text.of("U lost ur items!"), false);

            player.getInventory().dropAll();

        }
    }
}
