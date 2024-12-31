package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_3542;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem extends Item {
	private static final UUID[] field_12277 = new UUID[]{
		UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
		UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
		UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
		UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};
	public static final DispenserBehavior ARMOR_DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			ItemStack itemStack = ArmorItem.method_11353(pointer, stack);
			return itemStack.isEmpty() ? super.dispenseSilently(pointer, stack) : itemStack;
		}
	};
	protected final EquipmentSlot field_12275;
	protected final int protection;
	protected final float field_12276;
	protected final class_3542 field_4159;

	public static ItemStack method_11353(BlockPointer blockPointer, ItemStack itemStack) {
		BlockPos blockPos = blockPointer.getBlockPos().offset(blockPointer.getBlockState().getProperty(DispenserBlock.FACING));
		List<LivingEntity> list = blockPointer.getWorld()
			.method_16325(LivingEntity.class, new Box(blockPos), EntityPredicate.field_16705.and(new EntityPredicate.Armored(itemStack)));
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			EquipmentSlot equipmentSlot = MobEntity.method_13083(itemStack);
			ItemStack itemStack2 = itemStack.split(1);
			livingEntity.equipStack(equipmentSlot, itemStack2);
			if (livingEntity instanceof MobEntity) {
				((MobEntity)livingEntity).method_13077(equipmentSlot, 2.0F);
				((MobEntity)livingEntity).setPersistent();
			}

			return itemStack;
		}
	}

	public ArmorItem(class_3542 arg, EquipmentSlot equipmentSlot, Item.Settings settings) {
		super(settings.setMaxDamageIfAbsent(arg.method_15999(equipmentSlot)));
		this.field_4159 = arg;
		this.field_12275 = equipmentSlot;
		this.protection = arg.method_16001(equipmentSlot);
		this.field_12276 = arg.method_16004();
		DispenserBlock.method_16665(this, ARMOR_DISPENSER_BEHAVIOR);
	}

	public EquipmentSlot method_11352() {
		return this.field_12275;
	}

	@Override
	public int getEnchantability() {
		return this.field_4159.method_15998();
	}

	public class_3542 method_4602() {
		return this.field_4159;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.field_4159.method_16002().test(ingredient) || super.canRepair(stack, ingredient);
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

	@Override
	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.method_6326(equipmentSlot);
		if (equipmentSlot == this.field_12275) {
			multimap.put(
				EntityAttributes.GENERIC_ARMOR.getId(), new AttributeModifier(field_12277[equipmentSlot.method_13032()], "Armor modifier", (double)this.protection, 0)
			);
			multimap.put(
				EntityAttributes.GENERIC_ARMOR_TOUGHNESS.getId(),
				new AttributeModifier(field_12277[equipmentSlot.method_13032()], "Armor toughness", (double)this.field_12276, 0)
			);
		}

		return multimap;
	}

	public int method_15997() {
		return this.protection;
	}
}
