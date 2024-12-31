package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class class_3990 extends PersistentState {
	private LongSet field_19401 = new LongOpenHashSet();
	private LongSet field_19402 = new LongOpenHashSet();

	public class_3990(String string) {
		super(string);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.field_19401 = new LongOpenHashSet(nbt.getLongArray("All"));
		this.field_19402 = new LongOpenHashSet(nbt.getLongArray("Remaining"));
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putLongArray("All", this.field_19401.toLongArray());
		nbt.putLongArray("Remaining", this.field_19402.toLongArray());
		return nbt;
	}

	public void method_17643(long l) {
		this.field_19401.add(l);
		this.field_19402.add(l);
	}

	public boolean method_17644(long l) {
		return this.field_19401.contains(l);
	}

	public boolean method_17645(long l) {
		return this.field_19402.contains(l);
	}

	public void method_17646(long l) {
		this.field_19402.remove(l);
	}

	public LongSet method_17642() {
		return this.field_19401;
	}
}
