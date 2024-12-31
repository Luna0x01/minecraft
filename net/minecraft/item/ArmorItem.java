package net.minecraft.item;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
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
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem extends Item {
	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private static final UUID[] field_12277 = new UUID[]{
		UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
		UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
		UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
		UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};
	public static final String[] EMPTY = new String[]{
		"minecraft:items/empty_armor_slot_boots",
		"minecraft:items/empty_armor_slot_leggings",
		"minecraft:items/empty_armor_slot_chestplate",
		"minecraft:items/empty_armor_slot_helmet"
	};
	public static final DispenserBehavior ARMOR_DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			ItemStack itemStack = ArmorItem.method_11353(pointer, stack);
			return itemStack != null ? itemStack : super.dispenseSilently(pointer, stack);
		}
	};
	public final EquipmentSlot field_12275;
	public final int protection;
	public final float field_12276;
	public final int materialId;
	private final ArmorItem.Material material;

	public static ItemStack method_11353(BlockPointer blockPointer, ItemStack itemStack) {
		BlockPos blockPos = blockPointer.getBlockPos().offset(DispenserBlock.getDirection(blockPointer.getBlockStateData()));
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		Box box = new Box((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
		List<LivingEntity> list = blockPointer.getWorld()
			.getEntitiesInBox(LivingEntity.class, box, Predicates.and(EntityPredicate.EXCEPT_SPECTATOR, new EntityPredicate.Armored(itemStack)));
		if (list.isEmpty()) {
			return null;
		} else {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			EquipmentSlot equipmentSlot = MobEntity.method_13083(itemStack);
			ItemStack itemStack2 = itemStack.copy();
			itemStack2.count = 1;
			livingEntity.equipStack(equipmentSlot, itemStack2);
			if (livingEntity instanceof MobEntity) {
				((MobEntity)livingEntity).method_13077(equipmentSlot, 2.0F);
			}

			itemStack.count--;
			return itemStack;
		}
	}

	public ArmorItem(ArmorItem.Material material, int i, EquipmentSlot equipmentSlot) {
		this.material = material;
		this.field_12275 = equipmentSlot;
		this.materialId = i;
		this.protection = material.method_11356(equipmentSlot);
		this.setMaxDamage(material.method_11354(equipmentSlot));
		this.field_12276 = material.method_11357();
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.COMBAT);
		DispenserBlock.SPECIAL_ITEMS.put(this, ARMOR_DISPENSER_BEHAVIOR);
	}

	public EquipmentSlot method_11352() {
		return this.field_12275;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	public ArmorItem.Material getMaterial() {
		return this.material;
	}

	public boolean hasColor(ItemStack stack) {
		if (this.material != ArmorItem.Material.LEATHER) {
			return false;
		} else {
			NbtCompound nbtCompound = stack.getNbt();
			return nbtCompound != null && nbtCompound.contains("display", 10) ? nbtCompound.getCompound("display").contains("color", 3) : false;
		}
	}

	public int getColor(ItemStack stack) {
		if (this.material != ArmorItem.Material.LEATHER) {
			return 16777215;
		} else {
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound != null) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("display");
				if (nbtCompound2 != null && nbtCompound2.contains("color", 3)) {
					return nbtCompound2.getInt("color");
				}
			}

			return 10511680;
		}
	}

	public void removeColor(ItemStack stack) {
		if (this.material == ArmorItem.Material.LEATHER) {
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound != null) {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("display");
				if (nbtCompound2.contains("color")) {
					nbtCompound2.remove("color");
				}
			}
		}
	}

	public void setColor(ItemStack stack, int color) {
		if (this.material != ArmorItem.Material.LEATHER) {
			throw new UnsupportedOperationException("Can't dye non-leather!");
		} else {
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound == null) {
				nbtCompound = new NbtCompound();
				stack.setNbt(nbtCompound);
			}

			NbtCompound nbtCompound2 = nbtCompound.getCompound("display");
			if (!nbtCompound.contains("display", 10)) {
				nbtCompound.put("display", nbtCompound2);
			}

			nbtCompound2.putInt("color", color);
		}
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.material.getRepairIngredient() == ingredient.getItem() ? true : super.canRepair(stack, ingredient);
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		EquipmentSlot equipmentSlot = MobEntity.method_13083(itemStack);
		ItemStack itemStack2 = playerEntity.getStack(equipmentSlot);
		if (itemStack2 == null) {
			playerEntity.equipStack(equipmentSlot, itemStack.copy());
			itemStack.count = 0;
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

	public static enum Material {
		LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, Sounds.ITEM_ARMOR_EQUIP_LEATHER, 0.0F),
		CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, Sounds.ITEM_ARMOR_EQUIP_CHAIN, 0.0F),
		IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, Sounds.ITEM_ARMOR_EQUIP_IRON, 0.0F),
		GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, Sounds.ITEM_ARMOR_EQUIP_GOLD, 0.0F),
		DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, Sounds.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);

		private final String name;
		private final int durability;
		private final int[] protection;
		private final int enchantability;
		private final Sound field_12278;
		private final float field_12279;

		private Material(String string2, int j, int[] is, int k, Sound sound, float f) {
			this.name = string2;
			this.durability = j;
			this.protection = is;
			this.enchantability = k;
			this.field_12278 = sound;
			this.field_12279 = f;
		}

		public int method_11354(EquipmentSlot equipmentSlot) {
			return ArmorItem.BASE_DURABILITY[equipmentSlot.method_13032()] * this.durability;
		}

		public int method_11356(EquipmentSlot equipmentSlot) {
			return this.protection[equipmentSlot.method_13032()];
		}

		public int getEnchantability() {
			return this.enchantability;
		}

		public Sound method_11355() {
			return this.field_12278;
		}

		public Item getRepairIngredient() {
			if (this == LEATHER) {
				return Items.LEATHER;
			} else if (this == CHAIN) {
				return Items.IRON_INGOT;
			} else if (this == GOLD) {
				return Items.GOLD_INGOT;
			} else if (this == IRON) {
				return Items.IRON_INGOT;
			} else {
				return this == DIAMOND ? Items.DIAMOND : null;
			}
		}

		public String getName() {
			return this.name;
		}

		public float method_11357() {
			return this.field_12279;
		}
	}
}
