package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;

public class FlowerPotBlockEntity extends BlockEntity {
	private Item item;
	private int data;

	public FlowerPotBlockEntity() {
	}

	public FlowerPotBlockEntity(Item item, int i) {
		this.item = item;
		this.data = i;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		Identifier identifier = Item.REGISTRY.getIdentifier(this.item);
		nbt.putString("Item", identifier == null ? "" : identifier.toString());
		nbt.putInt("Data", this.data);
		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("Item", 8)) {
			this.item = Item.getFromId(nbt.getString("Item"));
		} else {
			this.item = Item.byRawId(nbt.getInt("Item"));
		}

		this.data = nbt.getInt("Data");
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 5, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public void setFlower(Item item, int data) {
		this.item = item;
		this.data = data;
	}

	@Nullable
	public ItemStack method_11659() {
		return this.item == null ? null : new ItemStack(this.item, 1, this.data);
	}

	@Nullable
	public Item getItem() {
		return this.item;
	}

	public int getData() {
		return this.data;
	}
}
