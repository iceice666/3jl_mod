package net.iceice666.threejl.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class JesusPunch {
    static final TypedActionResult<ItemStack> FAILED = TypedActionResult.pass(ItemStack.EMPTY);

    private JesusPunch() {
    }

    public static TypedActionResult<ItemStack> register(PlayerEntity p, World world, Hand hand) {
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        HitResult hitResult = player.raycast(4.0f, 1, false);

        if (hitResult.getType() != HitResult.Type.ENTITY) return FAILED;


        Entity object = ((EntityHitResult) hitResult).getEntity();
        if (!object.isPlayer()) return FAILED;

        ServerPlayerEntity targetPlayer = (ServerPlayerEntity) object;


        if (hand == Hand.OFF_HAND) {
            targetPlayer.sendMessage(Text.of(
                    """
                                        
                            我係耶穌
                            你係罪人
                            快d悔改
                            如果唔係我用耶穌神拳打你
                                     
                             """.trim()));


            return TypedActionResult.success(player.getOffHandStack());
        } else if (hand == Hand.MAIN_HAND) {
            // TODO
        }


        return FAILED;
    }
}
