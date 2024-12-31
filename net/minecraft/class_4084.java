package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shapes.VoxelShape;

final class class_4084 extends VoxelShape {
	class_4084(VoxelSet voxelSet) {
		super(voxelSet);
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return new class_4083(this.voxels.getSize(axis));
	}
}
