package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.class_4083;
import net.minecraft.class_4099;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;

public class SliceVoxelShape extends VoxelShape {
	private final VoxelShape field_19851;
	private final Direction.Axis field_19852;
	private final DoubleList field_19853 = new class_4083(1);

	public SliceVoxelShape(VoxelShape voxelShape, Direction.Axis axis, int i) {
		super(method_18063(voxelShape.voxels, axis, i));
		this.field_19851 = voxelShape;
		this.field_19852 = axis;
	}

	private static VoxelSet method_18063(VoxelSet voxelSet, Direction.Axis axis, int i) {
		return new class_4099(
			voxelSet,
			axis.choose(i, 0, 0),
			axis.choose(0, i, 0),
			axis.choose(0, 0, i),
			axis.choose(i + 1, voxelSet.field_19834, voxelSet.field_19834),
			axis.choose(voxelSet.field_19835, i + 1, voxelSet.field_19835),
			axis.choose(voxelSet.field_19836, voxelSet.field_19836, i + 1)
		);
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return axis == this.field_19852 ? this.field_19853 : this.field_19851.getIncludedPoints(axis);
	}
}
