package net.minecraft.block.entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.Weighting;

public class SpawnerBlockEntityBehaviorEntry extends Weighting.Weight {
	private final NbtCompound tag;

	public SpawnerBlockEntityBehaviorEntry() {
		super(1);
		this.tag = new NbtCompound();
		this.tag.putString("id", "Pig");
	}

	public SpawnerBlockEntityBehaviorEntry(NbtCompound nbtCompound) {
		this(nbtCompound.contains("Weight", 99) ? nbtCompound.getInt("Weight") : 1, nbtCompound.getCompound("Entity"));
	}

	public SpawnerBlockEntityBehaviorEntry(int i, NbtCompound nbtCompound) {
		super(i);
		this.tag = nbtCompound;
	}

	public NbtCompound toCompoundTag() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("Entity", this.tag);
		nbtCompound.putInt("Weight", this.weight);
		return nbtCompound;
	}

	public NbtCompound getCompoundTag() {
		return this.tag;
	}
}
