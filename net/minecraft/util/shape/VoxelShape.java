package net.minecraft.util.shape;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.OffsetDoubleList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class VoxelShape {
	protected final VoxelSet voxels;
	@Nullable
	private VoxelShape[] shapeCache;

	VoxelShape(VoxelSet voxelSet) {
		this.voxels = voxelSet;
	}

	public double getMinimum(Direction.Axis axis) {
		int i = this.voxels.getMin(axis);
		return i >= this.voxels.getSize(axis) ? Double.POSITIVE_INFINITY : this.getPointPosition(axis, i);
	}

	public double getMaximum(Direction.Axis axis) {
		int i = this.voxels.getMax(axis);
		return i <= 0 ? Double.NEGATIVE_INFINITY : this.getPointPosition(axis, i);
	}

	public Box getBoundingBox() {
		if (this.isEmpty()) {
			throw new UnsupportedOperationException("No bounds for empty shape.");
		} else {
			return new Box(
				this.getMinimum(Direction.Axis.field_11048),
				this.getMinimum(Direction.Axis.field_11052),
				this.getMinimum(Direction.Axis.field_11051),
				this.getMaximum(Direction.Axis.field_11048),
				this.getMaximum(Direction.Axis.field_11052),
				this.getMaximum(Direction.Axis.field_11051)
			);
		}
	}

	protected double getPointPosition(Direction.Axis axis, int i) {
		return this.getPointPositions(axis).getDouble(i);
	}

	protected abstract DoubleList getPointPositions(Direction.Axis axis);

	public boolean isEmpty() {
		return this.voxels.isEmpty();
	}

	public VoxelShape offset(double d, double e, double f) {
		return (VoxelShape)(this.isEmpty()
			? VoxelShapes.empty()
			: new ArrayVoxelShape(
				this.voxels,
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.field_11048), d),
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.field_11052), e),
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.field_11051), f)
			));
	}

	public VoxelShape simplify() {
		VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty()};
		this.forEachBox((d, e, f, g, h, i) -> voxelShapes[0] = VoxelShapes.combine(voxelShapes[0], VoxelShapes.cuboid(d, e, f, g, h, i), BooleanBiFunction.OR));
		return voxelShapes[0];
	}

	public void forEachEdge(VoxelShapes.BoxConsumer boxConsumer) {
		this.voxels
			.forEachEdge(
				(i, j, k, l, m, n) -> boxConsumer.consume(
						this.getPointPosition(Direction.Axis.field_11048, i),
						this.getPointPosition(Direction.Axis.field_11052, j),
						this.getPointPosition(Direction.Axis.field_11051, k),
						this.getPointPosition(Direction.Axis.field_11048, l),
						this.getPointPosition(Direction.Axis.field_11052, m),
						this.getPointPosition(Direction.Axis.field_11051, n)
					),
				true
			);
	}

	public void forEachBox(VoxelShapes.BoxConsumer boxConsumer) {
		DoubleList doubleList = this.getPointPositions(Direction.Axis.field_11048);
		DoubleList doubleList2 = this.getPointPositions(Direction.Axis.field_11052);
		DoubleList doubleList3 = this.getPointPositions(Direction.Axis.field_11051);
		this.voxels
			.forEachBox(
				(i, j, k, l, m, n) -> boxConsumer.consume(
						doubleList.getDouble(i), doubleList2.getDouble(j), doubleList3.getDouble(k), doubleList.getDouble(l), doubleList2.getDouble(m), doubleList3.getDouble(n)
					),
				true
			);
	}

	public List<Box> getBoundingBoxes() {
		List<Box> list = Lists.newArrayList();
		this.forEachBox((d, e, f, g, h, i) -> list.add(new Box(d, e, f, g, h, i)));
		return list;
	}

	public double method_1093(Direction.Axis axis, double d, double e) {
		Direction.Axis axis2 = AxisCycleDirection.field_10963.cycle(axis);
		Direction.Axis axis3 = AxisCycleDirection.field_10965.cycle(axis);
		int i = this.getCoordIndex(axis2, d);
		int j = this.getCoordIndex(axis3, e);
		int k = this.voxels.method_1043(axis, i, j);
		return k >= this.voxels.getSize(axis) ? Double.POSITIVE_INFINITY : this.getPointPosition(axis, k);
	}

	public double method_1102(Direction.Axis axis, double d, double e) {
		Direction.Axis axis2 = AxisCycleDirection.field_10963.cycle(axis);
		Direction.Axis axis3 = AxisCycleDirection.field_10965.cycle(axis);
		int i = this.getCoordIndex(axis2, d);
		int j = this.getCoordIndex(axis3, e);
		int k = this.voxels.method_1058(axis, i, j);
		return k <= 0 ? Double.NEGATIVE_INFINITY : this.getPointPosition(axis, k);
	}

	protected int getCoordIndex(Direction.Axis axis, double d) {
		return MathHelper.binarySearch(0, this.voxels.getSize(axis) + 1, i -> {
			if (i < 0) {
				return false;
			} else {
				return i > this.voxels.getSize(axis) ? true : d < this.getPointPosition(axis, i);
			}
		}) - 1;
	}

	protected boolean contains(double d, double e, double f) {
		return this.voxels
			.inBoundsAndContains(
				this.getCoordIndex(Direction.Axis.field_11048, d), this.getCoordIndex(Direction.Axis.field_11052, e), this.getCoordIndex(Direction.Axis.field_11051, f)
			);
	}

	@Nullable
	public BlockHitResult rayTrace(Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos) {
		if (this.isEmpty()) {
			return null;
		} else {
			Vec3d vec3d3 = vec3d2.subtract(vec3d);
			if (vec3d3.lengthSquared() < 1.0E-7) {
				return null;
			} else {
				Vec3d vec3d4 = vec3d.add(vec3d3.multiply(0.001));
				return this.contains(vec3d4.x - (double)blockPos.getX(), vec3d4.y - (double)blockPos.getY(), vec3d4.z - (double)blockPos.getZ())
					? new BlockHitResult(vec3d4, Direction.getFacing(vec3d3.x, vec3d3.y, vec3d3.z).getOpposite(), blockPos, true)
					: Box.rayTrace(this.getBoundingBoxes(), vec3d, vec3d2, blockPos);
			}
		}
	}

	public VoxelShape getFace(Direction direction) {
		if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
			if (this.shapeCache != null) {
				VoxelShape voxelShape = this.shapeCache[direction.ordinal()];
				if (voxelShape != null) {
					return voxelShape;
				}
			} else {
				this.shapeCache = new VoxelShape[6];
			}

			VoxelShape voxelShape2 = this.getUncachedFace(direction);
			this.shapeCache[direction.ordinal()] = voxelShape2;
			return voxelShape2;
		} else {
			return this;
		}
	}

	private VoxelShape getUncachedFace(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		Direction.AxisDirection axisDirection = direction.getDirection();
		DoubleList doubleList = this.getPointPositions(axis);
		if (doubleList.size() == 2 && DoubleMath.fuzzyEquals(doubleList.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList.getDouble(1), 1.0, 1.0E-7)) {
			return this;
		} else {
			int i = this.getCoordIndex(axis, axisDirection == Direction.AxisDirection.field_11056 ? 0.9999999 : 1.0E-7);
			return new SliceVoxelShape(this, axis, i);
		}
	}

	public double method_1108(Direction.Axis axis, Box box, double d) {
		return this.method_1103(AxisCycleDirection.between(axis, Direction.Axis.field_11048), box, d);
	}

	protected double method_1103(AxisCycleDirection axisCycleDirection, Box box, double d) {
		if (this.isEmpty()) {
			return d;
		} else if (Math.abs(d) < 1.0E-7) {
			return 0.0;
		} else {
			AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
			Direction.Axis axis = axisCycleDirection2.cycle(Direction.Axis.field_11048);
			Direction.Axis axis2 = axisCycleDirection2.cycle(Direction.Axis.field_11052);
			Direction.Axis axis3 = axisCycleDirection2.cycle(Direction.Axis.field_11051);
			double e = box.getMax(axis);
			double f = box.getMin(axis);
			int i = this.getCoordIndex(axis, f + 1.0E-7);
			int j = this.getCoordIndex(axis, e - 1.0E-7);
			int k = Math.max(0, this.getCoordIndex(axis2, box.getMin(axis2) + 1.0E-7));
			int l = Math.min(this.voxels.getSize(axis2), this.getCoordIndex(axis2, box.getMax(axis2) - 1.0E-7) + 1);
			int m = Math.max(0, this.getCoordIndex(axis3, box.getMin(axis3) + 1.0E-7));
			int n = Math.min(this.voxels.getSize(axis3), this.getCoordIndex(axis3, box.getMax(axis3) - 1.0E-7) + 1);
			int o = this.voxels.getSize(axis);
			if (d > 0.0) {
				for (int p = j + 1; p < o; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							if (this.voxels.inBoundsAndContains(axisCycleDirection2, p, q, r)) {
								double g = this.getPointPosition(axis, p) - e;
								if (g >= -1.0E-7) {
									d = Math.min(d, g);
								}

								return d;
							}
						}
					}
				}
			} else if (d < 0.0) {
				for (int s = i - 1; s >= 0; s--) {
					for (int t = k; t < l; t++) {
						for (int u = m; u < n; u++) {
							if (this.voxels.inBoundsAndContains(axisCycleDirection2, s, t, u)) {
								double h = this.getPointPosition(axis, s + 1) - f;
								if (h <= 1.0E-7) {
									d = Math.max(d, h);
								}

								return d;
							}
						}
					}
				}
			}

			return d;
		}
	}

	public String toString() {
		return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
	}
}
