package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shapes.VoxelShape;

public final class class_4101 extends VoxelShape {
	private final int field_19862;
	private final int field_19863;
	private final int field_19864;

	public class_4101(VoxelSet voxelSet, int i, int j, int k) {
		super(voxelSet);
		this.field_19862 = i;
		this.field_19863 = j;
		this.field_19864 = k;
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return new class_4093(this.voxels.getSize(axis), axis.choose(this.field_19862, this.field_19863, this.field_19864));
	}
}
