package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.Generic3x3ScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public class DispenserBlockEntity extends class_2737 {
	private static final Random RANDOM = new Random();
	private DefaultedList<ItemStack> field_15153 = DefaultedList.ofSize(9, ItemStack.EMPTY);

	protected DispenserBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public DispenserBlockEntity() {
		this(BlockEntityType.DISPENSER);
	}

	@Override
	public int getInvSize() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15153) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public int chooseNonEmptySlot() {
		this.method_11662(null);
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.field_15153.size(); k++) {
			if (!this.field_15153.get(k).isEmpty() && RANDOM.nextInt(j++) == 0) {
				i = k;
			}
		}

		return i;
	}

	public int addToFirstFreeSlot(ItemStack stack) {
		for (int i = 0; i < this.field_15153.size(); i++) {
			if (this.field_15153.get(i).isEmpty()) {
				this.setInvStack(i, stack);
				return i;
			}
		}

		return -1;
	}

	@Override
	public Text method_15540() {
		Text text = this.method_15541();
		return (Text)(text != null ? text : new TranslatableText("container.dispenser"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15153 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11661(nbt)) {
			class_2960.method_13927(nbt, this.field_15153);
		}

		if (nbt.contains("CustomName", 8)) {
			this.field_18643 = Text.Serializer.deserializeText(nbt.getString("CustomName"));
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (!this.method_11663(nbt)) {
			class_2960.method_13923(nbt, this.field_15153);
		}

		Text text = this.method_15541();
		if (text != null) {
			nbt.putString("CustomName", Text.Serializer.serialize(text));
		}

		return nbt;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public String getId() {
		return "minecraft:dispenser";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		this.method_11662(player);
		return new Generic3x3ScreenHandler(inventory, this);
	}

	@Override
	protected DefaultedList<ItemStack> method_13730() {
		return this.field_15153;
	}

	@Override
	protected void method_16834(DefaultedList<ItemStack> defaultedList) {
		this.field_15153 = defaultedList;
	}
}
