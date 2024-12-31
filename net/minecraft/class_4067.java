package net.minecraft;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class class_4067 {
	private final BlockPos field_19744;
	private int field_19745;
	private int field_19746;

	public class_4067(BlockPos blockPos, int i, int j) {
		this.field_19744 = blockPos;
		this.field_19745 = i;
		this.field_19746 = j;
	}

	public static class_4067 method_17926(NbtCompound nbtCompound) {
		BlockPos blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound("Pos"));
		int i = nbtCompound.getInt("Rotation");
		int j = nbtCompound.getInt("EntityId");
		return new class_4067(blockPos, i, j);
	}

	public NbtCompound method_17924() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("Pos", NbtHelper.fromBlockPos(this.field_19744));
		nbtCompound.putInt("Rotation", this.field_19745);
		nbtCompound.putInt("EntityId", this.field_19746);
		return nbtCompound;
	}

	public BlockPos method_17927() {
		return this.field_19744;
	}

	public int method_17928() {
		return this.field_19745;
	}

	public int method_17929() {
		return this.field_19746;
	}

	public String method_17930() {
		return method_17925(this.field_19744);
	}

	public static String method_17925(BlockPos blockPos) {
		return "frame-" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
	}
}
