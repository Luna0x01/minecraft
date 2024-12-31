package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class class_3595 extends PersistentState {
	private LongSet field_17490 = new LongOpenHashSet();

	public class_3595(String string) {
		super(string);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.field_17490 = new LongOpenHashSet(nbt.getLongArray("Forced"));
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putLongArray("Forced", this.field_17490.toLongArray());
		return nbt;
	}

	public LongSet method_16296() {
		return this.field_17490;
	}
}
