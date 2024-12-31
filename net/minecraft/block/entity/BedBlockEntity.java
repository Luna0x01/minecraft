package net.minecraft.block.entity;

import net.minecraft.block.BedBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;

public class BedBlockEntity extends BlockEntity {
	private DyeColor color = DyeColor.RED;

	public void method_14365(ItemStack itemStack) {
		this.setColor(DyeColor.byId(itemStack.getData()));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("color")) {
			this.color = DyeColor.byId(nbt.getInt("color"));
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("color", this.color.getId());
		return nbt;
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 11, this.getUpdatePacketContent());
	}

	public DyeColor getColor() {
		return this.color;
	}

	public void setColor(DyeColor color) {
		this.color = color;
		this.markDirty();
	}

	public boolean method_14366() {
		return BedBlock.method_14304(this.getDataValue());
	}

	public ItemStack toItemStack() {
		return new ItemStack(Items.BED, 1, this.color.getId());
	}
}
