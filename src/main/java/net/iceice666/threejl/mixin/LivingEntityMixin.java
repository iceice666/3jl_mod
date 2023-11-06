package net.iceice666.threejl.mixin;


import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

import static net.iceice666.threejl.items.Gravestone.*;
import static net.iceice666.threejl.util.checkPlayerInventoryContainsNbtItem;
import static net.iceice666.threejl.util.damageItem;

@Mixin(ServerPlayerEntity.class)
public abstract class LivingEntityMixin {
    // A set of UUIDs that are allowed to create gravestones.
    @Unique
    private static final Set<String> whitelist = Set.of(
            // UUID of player @jack0301
            "22bd9801-acd6-4e7e-ab14-9f53df1f42f7",
            // UUID of player @KSHSlime
            "75f6ec8c-6339-4a88-84d8-34afe4a38a1d",
            // UUID of player @coffeecat2006
            "61353ed0-f03c-40a4-9363-e3257b2dee34"
    );

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
        int[] totemSlot = checkPlayerInventoryContainsNbtItem(playerInventory, IS_TOTEM_OF_KEEP_INVENTORY);

        // If this player doesn't have any totem => drop
        if (totemSlot[0] == -1) {


            int[] gravestoneSlot = checkPlayerInventoryContainsNbtItem(playerInventory, IS_GRAVESTONE);

            // If this player doesn't have any gravestone => drop
            if (gravestoneSlot[0] == -1) {
                dropAll(player);
                return;
            }


            // Player have gravestone => remove 1
            playerInventory.combinedInventory
                    .get(gravestoneSlot[0])
                    .set(
                            gravestoneSlot[1]
                            , damageItem(
                                    playerInventory.combinedInventory
                                            .get(gravestoneSlot[0])
                                            .get(gravestoneSlot[1]),
                                    1)
                    );

            // create a gravestone
            boolean r = createGravestone(player);

            // If failed => drop
            if (!r) {
                dropAll(player);
                return;
            }
        }


        // Player have totem => remove 1

        playerInventory.combinedInventory
                .get(totemSlot[0])
                .set(
                        totemSlot[1]
                        , damageItem(
                                playerInventory.combinedInventory
                                        .get(totemSlot[0])
                                        .get(totemSlot[1]),
                                1)
                );


    }
}
