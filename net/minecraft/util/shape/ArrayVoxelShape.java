package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;

final class ArrayVoxelShape extends VoxelShape {
	private final DoubleList pointsX;
	private final DoubleList pointsY;
	private final DoubleList pointsZ;

	ArrayVoxelShape(VoxelSet voxelSet, double[] ds, double[] es, double[] fs) {
		this(
			voxelSet,
			DoubleArrayList.wrap(Arrays.copyOf(ds, voxelSet.getSizeX() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(es, voxelSet.getSizeY() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(fs, voxelSet.getSizeZ() + 1))
		);
	}

	ArrayVoxelShape(VoxelSet voxelSet, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
		super(voxelSet);
		int i = voxelSet.getSizeX() + 1;
		int j = voxelSet.getSizeY() + 1;
		int k = voxelSet.getSizeZ() + 1;
		if (i == doubleList.size() && j == doubleList2.size() && k == doubleList3.size()) {
			this.pointsX = doubleList;
			this.pointsY = doubleList2;
			this.pointsZ = doubleList3;
		} else {
			throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
		}
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		switch (axis) {
			case X:
				return this.pointsX;
			case Y:
				return this.pointsY;
			case Z:
				return this.pointsZ;
			default:
				throw new IllegalArgumentException();
		}
	}
}
