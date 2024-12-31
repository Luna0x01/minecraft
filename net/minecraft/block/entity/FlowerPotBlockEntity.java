package net.minecraft.block.entity;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
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

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		Identifier identifier = Item.REGISTRY.getIdentifier(this.item);
		nbt.putString("Item", identifier == null ? "" : identifier.toString());
		nbt.putInt("Data", this.data);
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

	@Override
	public Packet getPacket() {
		NbtCompound nbtCompound = new NbtCompound();
		this.toNbt(nbtCompound);
		nbtCompound.remove("Item");
		nbtCompound.putInt("Item", Item.getRawId(this.item));
		return new BlockEntityUpdateS2CPacket(this.pos, 5, nbtCompound);
	}

	public void setFlower(Item item, int data) {
		this.item = item;
		this.data = data;
	}

	public Item getItem() {
		return this.item;
	}

	public int getData() {
		return this.data;
	}
}
