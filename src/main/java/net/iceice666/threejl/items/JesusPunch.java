package net.iceice666.threejl.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

import static net.iceice666.threejl.Util.*;

public class JesusPunch {
    public static final String IS_JESUS_PUNCH = "is_jesus_punch";
    public static final String IS_JESUS = "is_jesus";
    static final ActionResult FAILED = ActionResult.PASS;
    static HashMap<UUID, Long> playerCooldown = new HashMap<>();

    private JesusPunch() {
    }


    private static ActionResult doMainhand(ServerPlayerEntity player, ServerPlayerEntity targetPlayer) {
        long currentUnixTimestamp = getCurrentUnixTimestamp();

        var cooldown = playerCooldown.getOrDefault(targetPlayer.getUuid(), currentUnixTimestamp - 60);

        var deltaCooldown = currentUnixTimestamp - (cooldown + 60);

        var itemStack = player.getMainHandStack();

        if (!itemStack.getNbt().getBoolean(IS_JESUS) && deltaCooldown < 0) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    ((MutableText) Text.of("這名玩家已經接受懲罰了，暫時放他一馬吧！"))
                            .setStyle(
                                    Style.EMPTY
                                            .withColor(Formatting.GREEN)
                            )
            ));

            return ActionResult.PASS;
        }

        playerCooldown.put(targetPlayer.getUuid(), currentUnixTimestamp);

        ServerWorld serverWorld = player.getServerWorld();

        var targetPlayerPos = targetPlayer.getPos();

        targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 255));
        targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 255));
        targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 60, 255));

        targetPlayer.playSound(SoundEvent.of(
                new Identifier("minecraft", "block.anvil.land")
        ), 1, 1);

        targetPlayer.playSound(SoundEvent.of(
                new Identifier("minecraft", "entity.player.hurt")
        ), 1, 1);

        targetPlayer.playSound(SoundEvent.of(
                new Identifier("minecraft", "item.trident.thunder")
        ), 1, 1);


        serverWorld.spawnParticles(
                targetPlayer,
                (ParticleEffect) Registries.PARTICLE_TYPE.get(new Identifier("minecraft", "explosion_emitter")),
                true,
                targetPlayerPos.x,
                targetPlayerPos.y,
                targetPlayerPos.z,
                10,
                1,
                1,
                1,
                0.5
        );


        targetPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(
                10, 60, 10)
        );

        targetPlayer.networkHandler.sendPacket(new TitleS2CPacket(
                ((MutableText) Text.of("喝啊！"))
                        .setStyle(
                                Style.EMPTY
                                        .withColor(Formatting.RED)
                        ))
        );

        targetPlayer.networkHandler.sendPacket(new SubtitleS2CPacket(
                ((MutableText) Text.of("任何邪惡！終將繩之以法！"))
                        .setStyle(
                                Style.EMPTY
                                        .withColor(Formatting.RED)
                        ))
        );


        player.getInventory().setStack(player.getInventory().selectedSlot,
                damageItem(itemStack, itemStack.getMaxDamage()));

        return ActionResult.SUCCESS;
    }

    static ActionResult doOffhand(ServerPlayerEntity player, ServerPlayerEntity targetPlayer) {

        targetPlayer.sendMessage(Text.of(
                """
                                    
                        我係耶穌
                        你係罪人
                        快d悔改
                        如果唔係我用耶穌神拳打你
                                 
                         """.trim()));

        MutableText text = (MutableText) Text.of("你已警告");
        text.append(targetPlayer.getDisplayName());

        player.sendMessage(text);


        return ActionResult.SUCCESS;
    }

    public static ActionResult register(PlayerEntity p, Entity targetEntity, @Nullable HitResult hitResult) {
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        if (hitResult == null) return FAILED;
        if (hitResult.getType() != HitResult.Type.ENTITY) return FAILED;
        if (!targetEntity.isPlayer()) return FAILED;

        var nbt = new NbtCompound();
        nbt.putBoolean(IS_JESUS_PUNCH, true);

        if (isHeldItemValid(player, Hand.OFF_HAND, Items.CARROT_ON_A_STICK, nbt)) {
            return doOffhand(player, (ServerPlayerEntity) targetEntity);
        } else if (isHeldItemValid(player, Hand.MAIN_HAND, Items.CARROT_ON_A_STICK, nbt)) {
            return doMainhand(player, (ServerPlayerEntity) targetEntity);
        }


        return FAILED;
    }


}
