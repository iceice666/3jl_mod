package net.iceice666.threejl.items.artilleries;

import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

import static net.iceice666.threejl.Util.damageItem;
import static net.iceice666.threejl.Util.getCurrentUnixTimestamp;
import static net.iceice666.threejl.items.artilleries.Artillery.getTntEntity;


public class Missile {

    // A constant key to check if the item has a specific NBT tag that marks it as a missile.
    public static final String IS_MISSILE = "is_missile";
    static final TypedActionResult<ItemStack> FAILED = TypedActionResult.pass(ItemStack.EMPTY);
    // A static Vec3d to hold the position of the target where the missile will strike.
    static HashMap<UUID, Vec3d> targetPos = new HashMap<>();
    static HashMap<UUID, Long> cooldown = new HashMap<>();
    static HashMap<UUID, Long> prevClick = new HashMap<>();


    private Missile() {
    }

    static boolean doOffhand(ServerPlayerEntity player) {
        // Get target block
        var targetBlockPos = getTargetBlock(player);

        // Get player UUID
        UUID playerUuid = player.getUuid();

        // Save to map
        targetPos.put(playerUuid, targetBlockPos);

        if (targetBlockPos == null) return false;


        // Send a response to player


        MutableText text = ((MutableText) Text.of("Target locked! "))
                .setStyle(
                        Style.EMPTY
                                .withColor(Formatting.GREEN)
                );

        text.append(((MutableText) Text.of(targetBlockPos.toString()))
                .setStyle(
                        Style.EMPTY
                                .withColor(Formatting.AQUA)
                ));


        OverlayMessageS2CPacket overlayPacket = new OverlayMessageS2CPacket(text);

        player.networkHandler.sendPacket(overlayPacket);

        return true;
    }

    static boolean doMainhand(
            ServerPlayerEntity player,
            World world,
            ItemStack mainhandItemStack,
            Vec3d targetBlockPos
    ) {
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
        return true;
    }

    // This method handles the custom behavior when the item is used.
    public static TypedActionResult<ItemStack> register(ServerPlayerEntity player, World world, Hand hand) {


        // Check if the off-hand is used.
        if (hand == Hand.OFF_HAND) {

            // Search target by getting the item in the off-hand.
            ItemStack offhandItemStack = player.getOffHandStack();


            // Check if the item is a carrot on a stick with the missile NBT tag.
            if (
                    !(offhandItemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            offhandItemStack.hasNbt() && offhandItemStack.getNbt().getBoolean(IS_MISSILE))
            ) return FAILED;

            // Find target
            return doOffhand(player) ?
                    TypedActionResult.success(offhandItemStack) : FAILED;


        } else if (hand == Hand.MAIN_HAND) {
            // If the main hand is used, then it's time to fire the missile!

            // Get the item in the main hand.
            ItemStack mainhandItemStack = player.getMainHandStack();


            // Check if the item is a carrot on a stick with the missile NBT tag and a target has been set.
            if (
                    !(mainhandItemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                            mainhandItemStack.hasNbt() && mainhandItemStack.getNbt().getBoolean(IS_MISSILE))
            ) return FAILED;

            // Get player UUID
            UUID playerUuid = player.getUuid();


            // Get target block
            Vec3d targetBlockPos;
            if (player.isSneaking()) {
                targetBlockPos = getTargetBlock(player);
            } else {
                targetBlockPos = targetPos.get(playerUuid);
            }
            // If target not found, return.
            if (targetBlockPos == null) {
                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        ((MutableText) Text.of("You have to set a target first!"))
                                .setStyle(
                                        Style.EMPTY
                                                .withColor(Formatting.RED)
                                )
                ));
                return FAILED;
            }


            // Get current unix timestamp
            var currentUnixTimestamp = getCurrentUnixTimestamp();

            // If this is first click or the interval of two clicks greater than 1 sec, return.
            if (prevClick.get(playerUuid) == null
                    || currentUnixTimestamp - prevClick.get(playerUuid) > 1) {
                prevClick.put(playerUuid, currentUnixTimestamp);
                return FAILED;
            }


            // This missile is not in cooldown
            if (cooldown.get(playerUuid) == null
                    || (currentUnixTimestamp - cooldown.get(playerUuid) - 15) >= 0
            ) {
                // Set cooldown
                cooldown.put(playerUuid, getCurrentUnixTimestamp());
                prevClick.put(playerUuid, null);

                // Launch
                return doMainhand(player, world, mainhandItemStack, targetBlockPos) ?
                        TypedActionResult.success(mainhandItemStack) : FAILED;
            }

            // This missile is in cooldown
            int deltaCd = (int) (currentUnixTimestamp - cooldown.get(playerUuid) - 15);

            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    ((MutableText) Text.of("You have to wait " + -deltaCd + " secs to launch next missile!"))
                            .setStyle(
                                    Style.EMPTY
                                            .withColor(Formatting.RED)
                                            .withFormatting(Formatting.BOLD)
                            )
            ));
            return FAILED;


        }

        // If none of the above conditions are met, do nothing.
        return FAILED;
    }


    static Vec3d getTargetBlock(PlayerEntity player) {

        // Perform a raycast to find a target up to a certain distance.
        HitResult hitResult = player.raycast(128f, 1.0f, false);

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
