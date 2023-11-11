package net.iceice666.threejl.items.artilleries;

import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

import static net.iceice666.threejl.Util.damageItem;
import static net.iceice666.threejl.items.artilleries.Artillery.getTntEntity;
import static net.iceice666.threejl.registers.ItemRegister.Utils.isPlayerInSurvival;


public class Missile {

    // A constant key to check if the item has a specific NBT tag that marks it as a missile.
    public static final String IS_MISSILE = "is_missile";
    // A static Vec3d to hold the position of the target where the missile will strike.
    static HashMap<UUID, Vec3d> targetPos = new HashMap<>();

    private Missile() {
    }

    // This method handles the custom behavior when the item is used.
    public static TypedActionResult<ItemStack> register(PlayerEntity player, World world, Hand hand) {

        // Check if the player is not in survival mode, if so, nothing happens.
        if (!isPlayerInSurvival(player)) return TypedActionResult.pass(ItemStack.EMPTY);

        // Check if the off-hand is used.
        if (hand == Hand.OFF_HAND) {

            // Search target by getting the item in the off-hand.
            ItemStack offhandItemStack = player.getOffHandStack();

            // Get player UUID
            UUID playerUuid = player.getUuid();

            // Check if the item is a carrot on a stick with the missile NBT tag.
            if (
                    !(offhandItemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            offhandItemStack.hasNbt() && offhandItemStack.getNbt().getBoolean(IS_MISSILE))
            ) return TypedActionResult.pass(ItemStack.EMPTY);

            // Get target block
            var targetBlockPos = getTargetBlock(player);

            // Save to map
            targetPos.put(playerUuid, targetBlockPos);

            if (targetBlockPos == null) return TypedActionResult.pass(ItemStack.EMPTY);

            // Send a message to the player with the coordinates of the target.
            player.sendMessage(Text.of("Target found at" + targetBlockPos));

            return TypedActionResult.success(offhandItemStack);

        } else if (hand == Hand.MAIN_HAND) {
            // If the main hand is used, then it's time to fire the missile!

            // Get the item in the main hand.
            ItemStack mainhandItemStack = player.getMainHandStack();

            // Get player UUID
            UUID playerUuid = player.getUuid();

            // Check if the item is a carrot on a stick with the missile NBT tag and a target has been set.
            if (
                    !(mainhandItemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            mainhandItemStack.hasNbt() && mainhandItemStack.getNbt().getBoolean(IS_MISSILE))
            ) return TypedActionResult.pass(ItemStack.EMPTY);


            Vec3d targetBlockPos;


            if (player.isSneaking()) {

                // Get target block
                targetBlockPos = getTargetBlock(player);
                if (targetBlockPos == null) return TypedActionResult.pass(ItemStack.EMPTY);

            } else {
                targetBlockPos = targetPos.get(playerUuid);
            }

            if (null == targetBlockPos) return TypedActionResult.pass(ItemStack.EMPTY);


            // Get the adjusted tnt entity.
            TntEntity tntEntity = getTntEntity(player, world, targetBlockPos, true);
            // Add the primed TNT to the world, launching the missile.
            world.spawnEntity(tntEntity);

            // Send a message to the player with a fire message.
            player.sendMessage(Text.of("The missile will arrive in 4 sec."));

            // Remove 1 of missile available count
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.setStack(playerInventory.selectedSlot,
                    damageItem(mainhandItemStack, 1));

            // Return the item in the main hand as the result.
            return TypedActionResult.success(mainhandItemStack);
        }

        // If none of the above conditions are met, do nothing.
        return TypedActionResult.pass(ItemStack.EMPTY);
    }


    static Vec3d getTargetBlock(PlayerEntity player) {

        // Perform a raycast to find a target up to a certain distance.
        HitResult hitResult = player.raycast(256f, 1.0f, false);

        // Get the type of hit result, could be entity or block.
        HitResult.Type type = hitResult.getType();

        // If the raycast hit an entity or a block, break the loop; the target is found.
        // Otherwise, return.
        if (type == HitResult.Type.MISS) {
            return null;
        }

        var hitResultPos = hitResult.getPos();

        // If the target is above the world's max height or blow the world's min height, ignore it.
        if (hitResult.getPos().getY() > 320 || hitResult.getPos().getY() < -64) {
            return null;
        }

        // Convert the hit position result to a Vec3d and adjust to the center of the block.
        // And return.
        return new Vec3d(
                Math.floor(hitResultPos.x) + 0.5,
                Math.floor(hitResultPos.y) + 1.5,
                Math.floor(hitResultPos.z) + 0.5
        );

    }
}
