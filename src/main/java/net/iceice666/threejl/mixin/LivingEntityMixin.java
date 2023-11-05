package net.iceice666.threejl.mixin;


import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.iceice666.threejl.items.Gravestone.*;
import static net.iceice666.threejl.util.checkPlayerInventoryContainsNbtItem;
import static net.iceice666.threejl.util.damageItem;

@Mixin(ServerPlayerEntity.class)
public abstract class LivingEntityMixin {


    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void replaceWithGrave(ServerPlayerEntity player, DamageSource damageSource) {


        // gamerule keepInventory is false => drop
        if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            player.getInventory().dropAll();
            player.dropXp();
        }

        // Player in whitelist => skip
        if (
                whitelist.contains(player.getUuidAsString())
        ) {
            return;
        }
        PlayerInventory playerInventory = player.getInventory();

        // Check player's inventory
        int totemSlot = checkPlayerInventoryContainsNbtItem(playerInventory, IS_TOTEM_OF_KEEP_INVENTORY);

        // If this player doesn't have any totem => drop
        if (totemSlot == -1) {


            int gravestoneSlot = checkPlayerInventoryContainsNbtItem(playerInventory, IS_GRAVESTONE);

            // If this player doesn't have any gravestone => drop
            if (gravestoneSlot == -1) {
                dropAll(player);
                return;
            }


            // Player have gravestone => remove 1
            playerInventory.setStack(gravestoneSlot,
                    damageItem(playerInventory.getStack(gravestoneSlot), 1));

            // create a gravestone
            boolean r = createGravestone(player);

            // If failed => drop
            if (!r) {
                dropAll(player);
                return;
            }
        }


        // Player have totem => remove 1

        playerInventory.setStack(totemSlot,
                damageItem(playerInventory.getStack(totemSlot), 1));


    }
}
