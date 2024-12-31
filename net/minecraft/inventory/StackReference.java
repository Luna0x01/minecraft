package net.minecraft.inventory;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface StackReference {
	StackReference EMPTY = new StackReference() {
		@Override
		public ItemStack get() {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean set(ItemStack stack) {
			return false;
		}
	};

	static StackReference of(Inventory inventory, int index, Predicate<ItemStack> stackFilter) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return inventory.getStack(index);
			}

			@Override
			public boolean set(ItemStack stack) {
				if (!stackFilter.test(stack)) {
					return false;
				} else {
					inventory.setStack(index, stack);
					return true;
				}
			}
		};
	}

	static StackReference of(Inventory inventory, int index) {
		return of(inventory, index, stack -> true);
	}

	static StackReference of(LivingEntity entity, EquipmentSlot slot, Predicate<ItemStack> filter) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return entity.getEquippedStack(slot);
			}

			@Override
			public boolean set(ItemStack stack) {
				if (!filter.test(stack)) {
					return false;
				} else {
					entity.equipStack(slot, stack);
					return true;
				}
			}
		};
	}

	static StackReference of(LivingEntity entity, EquipmentSlot slot) {
		return of(entity, slot, stack -> true);
	}

	ItemStack get();

	boolean set(ItemStack stack);
}
