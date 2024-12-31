package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem extends Item {
	private static final int[] BASE_DURABILITY = new int[]{11, 16, 15, 13};
	public static final String[] EMPTY = new String[]{
		"minecraft:items/empty_armor_slot_helmet",
		"minecraft:items/empty_armor_slot_chestplate",
		"minecraft:items/empty_armor_slot_leggings",
		"minecraft:items/empty_armor_slot_boots"
	};
	private static final DispenserBehavior ARMOR_DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
			int i = blockPos.getX();
			int j = blockPos.getY();
			int k = blockPos.getZ();
			Box box = new Box((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
			List<LivingEntity> list = pointer.getWorld()
				.getEntitiesInBox(LivingEntity.class, box, Predicates.and(EntityPredicate.EXCEPT_SPECTATOR, new EntityPredicate.Armored(stack)));
			if (list.size() > 0) {
				LivingEntity livingEntity = (LivingEntity)list.get(0);
				int l = livingEntity instanceof PlayerEntity ? 1 : 0;
				int m = MobEntity.getEquipableSlot(stack);
				ItemStack itemStack = stack.copy();
				itemStack.count = 1;
				livingEntity.setArmorSlot(m - l, itemStack);
				if (livingEntity instanceof MobEntity) {
					((MobEntity)livingEntity).method_5388(m, 2.0F);
				}

				stack.count--;
				return stack;
			} else {
				return super.dispenseSilently(pointer, stack);
			}
		}
	};
	public final int slot;
	public final int protection;
	public final int materialId;
	private final ArmorItem.Material material;

	public ArmorItem(ArmorItem.Material material, int i, int j) {
		this.material = material;
		this.slot = j;
		this.materialId = i;
		this.protection = material.getProtection(j);
		this.setMaxDamage(material.getDurability(j));
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.COMBAT);
		DispenserBlock.SPECIAL_ITEMS.put(this, ARMOR_DISPENSER_BEHAVIOR);
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		if (color > 0) {
			return 16777215;
		} else {
			int i = this.getColor(stack);
			if (i < 0) {
				i = 16777215;
			}

			return i;
		}
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
		} else if (!stack.hasNbt()) {
			return false;
		} else {
			return !stack.getNbt().contains("display", 10) ? false : stack.getNbt().getCompound("display").contains("color", 3);
		}
	}

	public int getColor(ItemStack stack) {
		if (this.material != ArmorItem.Material.LEATHER) {
			return -1;
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
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		int i = MobEntity.getEquipableSlot(stack) - 1;
		ItemStack itemStack = player.getArmorSlot(i);
		if (itemStack == null) {
			player.setArmorSlot(i, stack.copy());
			stack.count = 0;
		}

		return stack;
	}

	public static enum Material {
		LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
		CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
		IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
		GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
		DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

		private final String name;
		private final int durability;
		private final int[] protection;
		private final int enchantability;

		private Material(String string2, int j, int[] is, int k) {
			this.name = string2;
			this.durability = j;
			this.protection = is;
			this.enchantability = k;
		}

		public int getDurability(int slot) {
			return ArmorItem.BASE_DURABILITY[slot] * this.durability;
		}

		public int getProtection(int slot) {
			return this.protection[slot];
		}

		public int getEnchantability() {
			return this.enchantability;
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
	}
}
