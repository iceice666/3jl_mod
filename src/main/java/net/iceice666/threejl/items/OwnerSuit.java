package net.iceice666.threejl.items;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.util.Rarity;

public class OwnerSuit {
    Item.Settings settings = new Item.Settings().fireproof().rarity(Rarity.EPIC);

    private void addBasicThings(ItemStack item) {

        item.getOrCreateNbt().putBoolean("UNBREAKABLE", true);

        item.addHideFlag(ItemStack.TooltipSection.UNBREAKABLE);
        item.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
        item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        item.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
    }


    public ItemStack getHelmet() {
        ItemStack item = Items.register("leather_helmet", (Item) new DyeableArmorItem(ArmorMaterials.LEATHER,
                ArmorItem.Type.HELMET, settings)).getDefaultStack();

        item.addEnchantment(Enchantments.FIRE_PROTECTION, 10);
        item.addEnchantment(Enchantments.AQUA_AFFINITY, 1);
        item.addEnchantment(Enchantments.RESPIRATION, 10);

        item.addAttributeModifier(
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier("Armor", 5, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.HEAD);

        item.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier("Armor", 0.2, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.HEAD);

        addBasicThings(item);

        return item;
    }


    public ItemStack getChestplate() {
        ItemStack item = Items.register("elytra",
                (Item) new ElytraItem(settings)
        ).getDefaultStack();

        item.addEnchantment(Enchantments.PROTECTION, 20);

        item.addAttributeModifier(
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier("Armor", 12, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.CHEST);

        item.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier("Armor", 0.4, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.CHEST);

        addBasicThings(item);

        return item;
    }

    public ItemStack getLeggings() {
        ItemStack item = Items.register("leather_leggings", (Item) new DyeableArmorItem(ArmorMaterials.LEATHER,
                ArmorItem.Type.LEGGINGS, settings)).getDefaultStack();


        item.addEnchantment(Enchantments.BLAST_PROTECTION, 10);
        item.addEnchantment(Enchantments.SWIFT_SNEAK, 5);

        item.addAttributeModifier(
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier("Armor", 9, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.LEGS);

        item.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier("Armor", 0.3, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.LEGS);

        addBasicThings(item);

        return item;
    }


    public ItemStack getBoots() {
        ItemStack item = Items.register("leather_boots", (Item) new DyeableArmorItem(ArmorMaterials.LEATHER,
                ArmorItem.Type.BOOTS, settings)).getDefaultStack();


        item.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 10);
        item.addEnchantment(Enchantments.FEATHER_FALLING, 10);
        item.addEnchantment(Enchantments.DEPTH_STRIDER, 3);
        item.addEnchantment(Enchantments.SOUL_SPEED, 10);

        item.addAttributeModifier(
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier("Armor", 4, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.FEET);

        item.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier("Armor", 0.1, EntityAttributeModifier.Operation.ADDITION),
                EquipmentSlot.FEET);

        addBasicThings(item);

        return item;
    }


}
