package net.iceice666.threejl.items.artilleries;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static net.iceice666.threejl.Util.damageItem;

public class RocketLauncher {
    public static final String IS_ROCKET_LAUNCHER = "is_rocket_launcher";

    private RocketLauncher() {
    }

    public static TypedActionResult<ItemStack> register(PlayerEntity player, World world, Hand hand) {
        if (hand != Hand.MAIN_HAND) return TypedActionResult.pass(ItemStack.EMPTY);
        ItemStack itemStack = player.getMainHandStack();
        if (!(
                itemStack.isOf(net.minecraft.item.Items.CARROT_ON_A_STICK) &&
                        itemStack.hasNbt() && itemStack.getNbt().getBoolean(IS_ROCKET_LAUNCHER)
        )) return TypedActionResult.pass(ItemStack.EMPTY);


        var position = player.getEyePos();
        var pitch = player.getPitch();
        var yaw = player.getYaw();


        ServerWorld serverWorld = (ServerWorld) world;

        // Create the Fireball entity
        FireballEntity fireball = new FireballEntity(EntityType.FIREBALL, world);

        // Set the Fireball's position
        fireball.setPosition(position.getX(), position.getY(), position.getZ());


        fireball.powerX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) / 10;
        fireball.powerY = -Math.sin(Math.toRadians(pitch)) / 10;
        fireball.powerZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) / 10;


        // Set the explosion power (optional, defaults to 1)
        //fireball.explosionPower = explosionPower;

        // Add the Fireball to the world
        serverWorld.spawnEntity(fireball);

        player.getInventory().setStack(player.getInventory().selectedSlot, damageItem(itemStack, 1));


        return TypedActionResult.success(itemStack);
    }
}
