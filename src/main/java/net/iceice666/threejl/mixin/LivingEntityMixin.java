package net.iceice666.threejl.mixin;


import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.iceice666.threejl.other.Gravestone.*;
import static net.iceice666.threejl.util.checkPlayerInventoryContainsNbtItem;

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

        // Check player's inventory
        int totemSlot = checkPlayerInventoryContainsNbtItem(player.getInventory(), IS_TOTEM_OF_KEEP_INVENTORY_NBT_KEY);

        // If this player doesn't have any totem => drop
        if (totemSlot == -1) {


            int gravestoneSlot = checkPlayerInventoryContainsNbtItem(player.getInventory(), IS_GRAVESTONE_NBT_KEY);

            // If this player doesn't have any gravestone => drop
            if (gravestoneSlot == -1) {
                dropAll(player);
                return;
            }


            // Player have gravestone => remove 1
            player.getInventory().getStack(gravestoneSlot)
                    .damage(1, Random.create(), player);

            // create a gravestone
            boolean r = createGravestone(player);

            // If failed => drop
            if (!r) {
                dropAll(player);
                return;
            }
        }


        // Player have totem => remove 1
        player.getInventory().getStack(totemSlot)
                .damage(1, Random.create(), player);


    }
}
