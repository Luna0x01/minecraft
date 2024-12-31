package net.minecraft.world.gen.feature;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class FeatureState extends PersistentState {
	private NbtCompound features = new NbtCompound();

	public FeatureState(String string) {
		super(string);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.features = nbt.getCompound("Features");
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		nbt.put("Features", this.features);
	}

	public void putFeature(NbtCompound nbt, int x, int z) {
		this.features.put(format(x, z), nbt);
	}

	public static String format(int x, int z) {
		return "[" + x + "," + z + "]";
	}

	public NbtCompound getFeatures() {
		return this.features;
	}
}
