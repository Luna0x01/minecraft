package net.minecraft.util.shape;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
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

	VoxelShape(VoxelSet voxels) {
		this.voxels = voxels;
	}

	public double getMin(Direction.Axis axis) {
		int i = this.voxels.getMin(axis);
		return i >= this.voxels.getSize(axis) ? Double.POSITIVE_INFINITY : this.getPointPosition(axis, i);
	}

	public double getMax(Direction.Axis axis) {
		int i = this.voxels.getMax(axis);
		return i <= 0 ? Double.NEGATIVE_INFINITY : this.getPointPosition(axis, i);
	}

	public Box getBoundingBox() {
		if (this.isEmpty()) {
			throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("No bounds for empty shape."));
		} else {
			return new Box(
				this.getMin(Direction.Axis.X),
				this.getMin(Direction.Axis.Y),
				this.getMin(Direction.Axis.Z),
				this.getMax(Direction.Axis.X),
				this.getMax(Direction.Axis.Y),
				this.getMax(Direction.Axis.Z)
			);
		}
	}

	protected double getPointPosition(Direction.Axis axis, int index) {
		return this.getPointPositions(axis).getDouble(index);
	}

	protected abstract DoubleList getPointPositions(Direction.Axis axis);

	public boolean isEmpty() {
		return this.voxels.isEmpty();
	}

	public VoxelShape offset(double x, double y, double z) {
		return (VoxelShape)(this.isEmpty()
			? VoxelShapes.empty()
			: new ArrayVoxelShape(
				this.voxels,
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.X), x),
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.Y), y),
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.Z), z)
			));
	}

	public VoxelShape simplify() {
		VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty()};
		this.forEachBox(
			(minX, minY, minZ, maxX, maxY, maxZ) -> voxelShapes[0] = VoxelShapes.combine(
					voxelShapes[0], VoxelShapes.cuboid(minX, minY, minZ, maxX, maxY, maxZ), BooleanBiFunction.OR
				)
		);
		return voxelShapes[0];
	}

	public void forEachEdge(VoxelShapes.BoxConsumer boxConsumer) {
		this.voxels
			.forEachEdge(
				(i, j, k, l, m, n) -> boxConsumer.consume(
						this.getPointPosition(Direction.Axis.X, i),
						this.getPointPosition(Direction.Axis.Y, j),
						this.getPointPosition(Direction.Axis.Z, k),
						this.getPointPosition(Direction.Axis.X, l),
						this.getPointPosition(Direction.Axis.Y, m),
						this.getPointPosition(Direction.Axis.Z, n)
					),
				true
			);
	}

	public void forEachBox(VoxelShapes.BoxConsumer boxConsumer) {
		DoubleList doubleList = this.getPointPositions(Direction.Axis.X);
		DoubleList doubleList2 = this.getPointPositions(Direction.Axis.Y);
		DoubleList doubleList3 = this.getPointPositions(Direction.Axis.Z);
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
		this.forEachBox((x1, y1, z1, x2, y2, z2) -> list.add(new Box(x1, y1, z1, x2, y2, z2)));
		return list;
	}

	public double method_35593(Direction.Axis axis, double d, double e) {
		Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
		Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
		int i = this.getCoordIndex(axis2, d);
		int j = this.getCoordIndex(axis3, e);
		int k = this.voxels.method_35592(axis, i, j);
		return k >= this.voxels.getSize(axis) ? Double.POSITIVE_INFINITY : this.getPointPosition(axis, k);
	}

	public double getEndingCoord(Direction.Axis axis, double from, double to) {
		Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
		Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
		int i = this.getCoordIndex(axis2, from);
		int j = this.getCoordIndex(axis3, to);
		int k = this.voxels.getEndingAxisCoord(axis, i, j);
		return k <= 0 ? Double.NEGATIVE_INFINITY : this.getPointPosition(axis, k);
	}

	protected int getCoordIndex(Direction.Axis axis, double coord) {
		return MathHelper.binarySearch(0, this.voxels.getSize(axis) + 1, i -> coord < this.getPointPosition(axis, i)) - 1;
	}

	@Nullable
	public BlockHitResult raycast(Vec3d start, Vec3d end, BlockPos pos) {
		if (this.isEmpty()) {
			return null;
		} else {
			Vec3d vec3d = end.subtract(start);
			if (vec3d.lengthSquared() < 1.0E-7) {
				return null;
			} else {
				Vec3d vec3d2 = start.add(vec3d.multiply(0.001));
				return this.voxels
						.inBoundsAndContains(
							this.getCoordIndex(Direction.Axis.X, vec3d2.x - (double)pos.getX()),
							this.getCoordIndex(Direction.Axis.Y, vec3d2.y - (double)pos.getY()),
							this.getCoordIndex(Direction.Axis.Z, vec3d2.z - (double)pos.getZ())
						)
					? new BlockHitResult(vec3d2, Direction.getFacing(vec3d.x, vec3d.y, vec3d.z).getOpposite(), pos, true)
					: Box.raycast(this.getBoundingBoxes(), start, end, pos);
			}
		}
	}

	public Optional<Vec3d> getClosestPointTo(Vec3d target) {
		if (this.isEmpty()) {
			return Optional.empty();
		} else {
			Vec3d[] vec3ds = new Vec3d[1];
			this.forEachBox((d, e, f, g, h, i) -> {
				double j = MathHelper.clamp(target.getX(), d, g);
				double k = MathHelper.clamp(target.getY(), e, h);
				double l = MathHelper.clamp(target.getZ(), f, i);
				if (vec3ds[0] == null || target.squaredDistanceTo(j, k, l) < target.squaredDistanceTo(vec3ds[0])) {
					vec3ds[0] = new Vec3d(j, k, l);
				}
			});
			return Optional.of(vec3ds[0]);
		}
	}

	public VoxelShape getFace(Direction facing) {
		if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
			if (this.shapeCache != null) {
				VoxelShape voxelShape = this.shapeCache[facing.ordinal()];
				if (voxelShape != null) {
					return voxelShape;
				}
			} else {
				this.shapeCache = new VoxelShape[6];
			}

			VoxelShape voxelShape2 = this.getUncachedFace(facing);
			this.shapeCache[facing.ordinal()] = voxelShape2;
			return voxelShape2;
		} else {
			return this;
		}
	}

	private VoxelShape getUncachedFace(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		DoubleList doubleList = this.getPointPositions(axis);
		if (doubleList.size() == 2 && DoubleMath.fuzzyEquals(doubleList.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList.getDouble(1), 1.0, 1.0E-7)) {
			return this;
		} else {
			Direction.AxisDirection axisDirection = direction.getDirection();
			int i = this.getCoordIndex(axis, axisDirection == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
			return new SlicedVoxelShape(this, axis, i);
		}
	}

	public double calculateMaxDistance(Direction.Axis axis, Box box, double maxDist) {
		return this.calculateMaxDistance(AxisCycleDirection.between(axis, Direction.Axis.X), box, maxDist);
	}

	protected double calculateMaxDistance(AxisCycleDirection axisCycle, Box box, double maxDist) {
		if (this.isEmpty()) {
			return maxDist;
		} else if (Math.abs(maxDist) < 1.0E-7) {
			return 0.0;
		} else {
			AxisCycleDirection axisCycleDirection = axisCycle.opposite();
			Direction.Axis axis = axisCycleDirection.cycle(Direction.Axis.X);
			Direction.Axis axis2 = axisCycleDirection.cycle(Direction.Axis.Y);
			Direction.Axis axis3 = axisCycleDirection.cycle(Direction.Axis.Z);
			double d = box.getMax(axis);
			double e = box.getMin(axis);
			int i = this.getCoordIndex(axis, e + 1.0E-7);
			int j = this.getCoordIndex(axis, d - 1.0E-7);
			int k = Math.max(0, this.getCoordIndex(axis2, box.getMin(axis2) + 1.0E-7));
			int l = Math.min(this.voxels.getSize(axis2), this.getCoordIndex(axis2, box.getMax(axis2) - 1.0E-7) + 1);
			int m = Math.max(0, this.getCoordIndex(axis3, box.getMin(axis3) + 1.0E-7));
			int n = Math.min(this.voxels.getSize(axis3), this.getCoordIndex(axis3, box.getMax(axis3) - 1.0E-7) + 1);
			int o = this.voxels.getSize(axis);
			if (maxDist > 0.0) {
				for (int p = j + 1; p < o; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							if (this.voxels.inBoundsAndContains(axisCycleDirection, p, q, r)) {
								double f = this.getPointPosition(axis, p) - d;
								if (f >= -1.0E-7) {
									maxDist = Math.min(maxDist, f);
								}

								return maxDist;
							}
						}
					}
				}
			} else if (maxDist < 0.0) {
				for (int s = i - 1; s >= 0; s--) {
					for (int t = k; t < l; t++) {
						for (int u = m; u < n; u++) {
							if (this.voxels.inBoundsAndContains(axisCycleDirection, s, t, u)) {
								double g = this.getPointPosition(axis, s + 1) - e;
								if (g <= 1.0E-7) {
									maxDist = Math.max(maxDist, g);
								}

								return maxDist;
							}
						}
					}
				}
			}

			return maxDist;
		}
	}

	public String toString() {
		return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
	}
}
