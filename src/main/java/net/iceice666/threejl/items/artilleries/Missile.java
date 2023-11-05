package net.iceice666.threejl.items.artilleries;

import net.iceice666.threejl.registers.ItemRegister;
import net.minecraft.entity.EntityType;
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
import org.jetbrains.annotations.NotNull;

import static net.iceice666.threejl.registers.ItemRegister.utils.isPlayerInSurvival;
import static net.iceice666.threejl.util.damageItem;

public class Missile implements ItemRegister.Item {


    // A constant key to check if the item has a specific NBT tag that marks it as a missile.
    public final static String IS_MISSILE_KEY = "is_missile";
    // A static Vec3d to hold the position of the target where the missile will strike.
    static Vec3d targetPos = null;

    // This method handles the custom behavior when the item is used.
    public static TypedActionResult<ItemStack> register(PlayerEntity player, World world, Hand hand) {

        // Check if the player is not in survival mode, if so, nothing happens.
        if (!isPlayerInSurvival(player)) return TypedActionResult.pass(ItemStack.EMPTY);

        // Check if the off-hand is used.
        if (hand == Hand.OFF_HAND) {

            // Search target by getting the item in the off-hand.
            ItemStack offhandItemStack = player.getOffHandStack();

            // Check if the item is a carrot on a stick with the missile NBT tag.
            if (
                    !(offhandItemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            offhandItemStack.hasNbt() && offhandItemStack.getNbt().getBoolean(IS_MISSILE_KEY))
            ) return TypedActionResult.pass(ItemStack.EMPTY);

            // Initialize the distance for raycasting to find the target position.
            float distance = 0.25f;
            while (true) {
                // Perform a raycast to find a target up to a certain distance.
                HitResult hitResult = player.raycast(distance, 1.0f, false);

                // Get the type of hit result, could be entity or block.
                HitResult.Type type = hitResult.getType();

                // Convert the hit position result to a Vec3d and adjust to the center of the block.
                var hitResultPos = hitResult.getPos();
                targetPos = new Vec3d(
                        Math.floor(hitResultPos.x) + 0.5,
                        Math.floor(hitResultPos.y) + 1.5,
                        Math.floor(hitResultPos.z) + 0.5
                );

                // If the target is above the world's max height, ignore it.
                if (targetPos.getY() > 320) {
                    return TypedActionResult.pass(ItemStack.EMPTY);
                }

                // If the raycast hit an entity or a block, break the loop; the target is found.
                if (type == HitResult.Type.ENTITY || type == HitResult.Type.BLOCK) {
                    break;
                } else {
                    // Otherwise, increase the distance and try again.
                    distance += 0.25f;
                    targetPos = null;
                }
            }

            // Send a message to the player with the coordinates of the target.
            player.sendMessage(Text.of("Target found at" + targetPos));

            return TypedActionResult.success(offhandItemStack);

        } else if (hand == Hand.MAIN_HAND) {
            // If the main hand is used, then it's time to fire the missile!

            // Get the item in the main hand.
            ItemStack mainhandItemStack = player.getMainHandStack();

            // Check if the item is a carrot on a stick with the missile NBT tag and a target has been set.
            if (
                    !(mainhandItemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            mainhandItemStack.hasNbt() && mainhandItemStack.getNbt().getBoolean(IS_MISSILE_KEY) &&
                            targetPos != null)
            ) return TypedActionResult.pass(ItemStack.EMPTY);

            // Get the adjusted tnt entity.
            TntEntity tntEntity = getTntEntity(player, world);
            // Add the primed TNT to the world, launching the missile.
            world.spawnEntity(tntEntity);

            // Send a message to the player with a fire message.
            player.sendMessage(Text.of("The missile will arrive in 4 sec."));

            // Remove 1 of missile available count
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.setStack(playerInventory.selectedSlot, damageItem(mainhandItemStack, 1));

            // Return the item in the main hand as the result.
            return TypedActionResult.success(mainhandItemStack);
        }

        // If none of the above conditions are met, do nothing.
        return TypedActionResult.pass(ItemStack.EMPTY);
    }

    @NotNull
    private static TntEntity getTntEntity(PlayerEntity player, World world) {
        // Get the player's current position.
        Vec3d playerPos = player.getPos();
        // Calculate the motion vector for the missile based on the player's position and the target position.
        Vec3d motion = calcMissileMotion(playerPos, targetPos);

        // Create a new TNT entity (the missile) at the player's position.
        TntEntity tntEntity = new TntEntity(EntityType.TNT, world);
        tntEntity.updatePosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        // Set the calculated motion for the TNT entity.
        tntEntity.setVelocity(motion);
        // Return tnt entity
        return tntEntity;
    }


    public static Vec3d calcMissileMotion(Vec3d playerPos, Vec3d targetPos) {
        // Calculate the differences in each axis between the player and the target.
        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;


        // The calculations below determine the initial velocities needed
        // on each axis to make the TNT entity (missile) travel to the target position.


        /*
        x & z axis

        f = 0.02

        Vt = Vt-1(1-f)
             => V0(0.98)^(t)


        S = Integral [0,80] V0(0.98)^(t)
          = V0 * Integral [0,80] (0.98)^(t)
          => V0 *  39.6655328082212

          => V0 = S / 39.6655328082212
         */
        double motionX = deltaX / 39.6655328082212f;
        double motionZ = deltaZ / 39.6655328082212f;



        /*
        y-axis

        f = 0.02
        g = -0.04

        Vt = Vt-1(1-f) + g
           => (V0+2)(0.98)^t-2

        S = Integral [0,80] (V0+2)((0.98)^t)-2
          => 39.6655328082212*V0 - 80.6689343835576

        V0 = (S+80.6689343835576)/39.6655328082212
         */


        double motionY = (deltaY + 80.6689343835576f) / 39.6655328082212f;

        return new Vec3d(motionX, motionY, motionZ);

    }
}
