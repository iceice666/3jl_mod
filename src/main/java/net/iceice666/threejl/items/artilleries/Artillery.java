package net.iceice666.threejl.items.artilleries;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class Artillery {
    @NotNull
    public static TntEntity getTntEntity(PlayerEntity player, World world, Vec3d targetPos) {
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
