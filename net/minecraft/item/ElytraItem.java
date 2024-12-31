package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem extends Item {
	public ElytraItem(Item.Settings settings) {
		super(settings);
		this.addProperty(new Identifier("broken"), (itemStack, world, livingEntity) -> method_11370(itemStack) ? 0.0F : 1.0F);
		DispenserBlock.method_16665(this, ArmorItem.ARMOR_DISPENSER_BEHAVIOR);
	}

	public static boolean method_11370(ItemStack itemStack) {
		return itemStack.getDamage() < itemStack.getMaxDamage() - 1;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.PHANTOM_MEMBRANE;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.method_13083(itemStack);
		ItemStack itemStack2 = player.getStack(equipmentSlot);
		if (itemStack2.isEmpty()) {
			player.equipStack(equipmentSlot, itemStack.copy());
			itemStack.setCount(0);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
