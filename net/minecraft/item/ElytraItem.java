package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem extends Item {
	public ElytraItem() {
		this.maxCount = 1;
		this.setMaxDamage(432);
		this.setItemGroup(ItemGroup.TRANSPORTATION);
		this.addProperty(new Identifier("broken"), new ItemPropertyGetter() {
			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				return ElytraItem.method_11370(stack) ? 0.0F : 1.0F;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(this, ArmorItem.ARMOR_DISPENSER_BEHAVIOR);
	}

	public static boolean method_11370(ItemStack itemStack) {
		return itemStack.getDamage() < itemStack.getMaxDamage() - 1;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.LEATHER;
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
