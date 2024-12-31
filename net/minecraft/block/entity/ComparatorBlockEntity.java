package net.minecraft.block.entity;

import net.minecraft.nbt.NbtCompound;

public class ComparatorBlockEntity extends BlockEntity {
	private int outputSignal;

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("OutputSignal", this.outputSignal);
		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.outputSignal = nbt.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
}
