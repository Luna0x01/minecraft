package net.minecraft;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class class_3742 extends BlockEntity {
	private ItemStack field_18640 = ItemStack.EMPTY;

	public class_3742() {
		super(BlockEntityType.JUKEBOX);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("RecordItem", 10)) {
			this.method_16828(ItemStack.from(nbt.getCompound("RecordItem")));
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (!this.method_16829().isEmpty()) {
			nbt.put("RecordItem", this.method_16829().toNbt(new NbtCompound()));
		}

		return nbt;
	}

	public ItemStack method_16829() {
		return this.field_18640;
	}

	public void method_16828(ItemStack itemStack) {
		this.field_18640 = itemStack;
		this.markDirty();
	}
}
