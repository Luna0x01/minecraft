package net.minecraft.util.shape;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;

public final class VoxelShapes {
	private static final VoxelShape FULL_CUBE = Util.make(() -> {
		VoxelSet voxelSet = new BitSetVoxelSet(1, 1, 1);
		voxelSet.set(0, 0, 0, true, true);
		return new SimpleVoxelShape(voxelSet);
	});
	public static final VoxelShape UNBOUNDED = cuboid(
		Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
	);
	private static final VoxelShape EMPTY = new ArrayVoxelShape(
		new BitSetVoxelSet(0, 0, 0), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0})
	);

	public static VoxelShape empty() {
		return EMPTY;
	}

	public static VoxelShape fullCube() {
		return FULL_CUBE;
	}

	public static VoxelShape cuboid(double d, double e, double f, double g, double h, double i) {
		return cuboid(new Box(d, e, f, g, h, i));
	}

	public static VoxelShape cuboid(Box box) {
		int i = findRequiredBitResolution(box.x1, box.x2);
		int j = findRequiredBitResolution(box.y1, box.y2);
		int k = findRequiredBitResolution(box.z1, box.z2);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return box.contains(0.5, 0.5, 0.5) ? fullCube() : empty();
			} else {
				int l = 1 << i;
				int m = 1 << j;
				int n = 1 << k;
				int o = (int)Math.round(box.x1 * (double)l);
				int p = (int)Math.round(box.x2 * (double)l);
				int q = (int)Math.round(box.y1 * (double)m);
				int r = (int)Math.round(box.y2 * (double)m);
				int s = (int)Math.round(box.z1 * (double)n);
				int t = (int)Math.round(box.z2 * (double)n);
				BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(l, m, n, o, q, s, p, r, t);

				for (long u = (long)o; u < (long)p; u++) {
					for (long v = (long)q; v < (long)r; v++) {
						for (long w = (long)s; w < (long)t; w++) {
							bitSetVoxelSet.set((int)u, (int)v, (int)w, false, true);
						}
					}
				}

				return new SimpleVoxelShape(bitSetVoxelSet);
			}
		} else {
			return new ArrayVoxelShape(FULL_CUBE.voxels, new double[]{box.x1, box.x2}, new double[]{box.y1, box.y2}, new double[]{box.z1, box.z2});
		}
	}

	private static int findRequiredBitResolution(double d, double e) {
		if (!(d < -1.0E-7) && !(e > 1.0000001)) {
			for (int i = 0; i <= 3; i++) {
				double f = d * (double)(1 << i);
				double g = e * (double)(1 << i);
				boolean bl = Math.abs(f - Math.floor(f)) < 1.0E-7;
				boolean bl2 = Math.abs(g - Math.floor(g)) < 1.0E-7;
				if (bl && bl2) {
					return i;
				}
			}

			return -1;
		} else {
			return -1;
		}
	}

	protected static long lcm(int i, int j) {
		return (long)i * (long)(j / IntMath.gcd(i, j));
	}

	public static VoxelShape union(VoxelShape voxelShape, VoxelShape voxelShape2) {
		return combineAndSimplify(voxelShape, voxelShape2, BooleanBiFunction.OR);
	}

	public static VoxelShape union(VoxelShape voxelShape, VoxelShape... voxelShapes) {
		return (VoxelShape)Arrays.stream(voxelShapes).reduce(voxelShape, VoxelShapes::union);
	}

	public static VoxelShape combineAndSimplify(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		return combine(voxelShape, voxelShape2, booleanBiFunction).simplify();
	}

	public static VoxelShape combine(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		if (booleanBiFunction.apply(false, false)) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException());
		} else if (voxelShape == voxelShape2) {
			return booleanBiFunction.apply(true, true) ? voxelShape : empty();
		} else {
			boolean bl = booleanBiFunction.apply(true, false);
			boolean bl2 = booleanBiFunction.apply(false, true);
			if (voxelShape.isEmpty()) {
				return bl2 ? voxelShape2 : empty();
			} else if (voxelShape2.isEmpty()) {
				return bl ? voxelShape : empty();
			} else {
				PairList pairList = createListPair(
					1, voxelShape.getPointPositions(Direction.Axis.field_11048), voxelShape2.getPointPositions(Direction.Axis.field_11048), bl, bl2
				);
				PairList pairList2 = createListPair(
					pairList.getPairs().size() - 1,
					voxelShape.getPointPositions(Direction.Axis.field_11052),
					voxelShape2.getPointPositions(Direction.Axis.field_11052),
					bl,
					bl2
				);
				PairList pairList3 = createListPair(
					(pairList.getPairs().size() - 1) * (pairList2.getPairs().size() - 1),
					voxelShape.getPointPositions(Direction.Axis.field_11051),
					voxelShape2.getPointPositions(Direction.Axis.field_11051),
					bl,
					bl2
				);
				BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.combine(voxelShape.voxels, voxelShape2.voxels, pairList, pairList2, pairList3, booleanBiFunction);
				return (VoxelShape)(pairList instanceof FractionalPairList && pairList2 instanceof FractionalPairList && pairList3 instanceof FractionalPairList
					? new SimpleVoxelShape(bitSetVoxelSet)
					: new ArrayVoxelShape(bitSetVoxelSet, pairList.getPairs(), pairList2.getPairs(), pairList3.getPairs()));
			}
		}
	}

	public static boolean matchesAnywhere(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		if (booleanBiFunction.apply(false, false)) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException());
		} else if (voxelShape == voxelShape2) {
			return booleanBiFunction.apply(true, true);
		} else if (voxelShape.isEmpty()) {
			return booleanBiFunction.apply(false, !voxelShape2.isEmpty());
		} else if (voxelShape2.isEmpty()) {
			return booleanBiFunction.apply(!voxelShape.isEmpty(), false);
		} else {
			boolean bl = booleanBiFunction.apply(true, false);
			boolean bl2 = booleanBiFunction.apply(false, true);

			for (Direction.Axis axis : AxisCycleDirection.AXES) {
				if (voxelShape.getMaximum(axis) < voxelShape2.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}

				if (voxelShape2.getMaximum(axis) < voxelShape.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}
			}

			PairList pairList = createListPair(
				1, voxelShape.getPointPositions(Direction.Axis.field_11048), voxelShape2.getPointPositions(Direction.Axis.field_11048), bl, bl2
			);
			PairList pairList2 = createListPair(
				pairList.getPairs().size() - 1,
				voxelShape.getPointPositions(Direction.Axis.field_11052),
				voxelShape2.getPointPositions(Direction.Axis.field_11052),
				bl,
				bl2
			);
			PairList pairList3 = createListPair(
				(pairList.getPairs().size() - 1) * (pairList2.getPairs().size() - 1),
				voxelShape.getPointPositions(Direction.Axis.field_11051),
				voxelShape2.getPointPositions(Direction.Axis.field_11051),
				bl,
				bl2
			);
			return matchesAnywhere(pairList, pairList2, pairList3, voxelShape.voxels, voxelShape2.voxels, booleanBiFunction);
		}
	}

	private static boolean matchesAnywhere(
		PairList pairList, PairList pairList2, PairList pairList3, VoxelSet voxelSet, VoxelSet voxelSet2, BooleanBiFunction booleanBiFunction
	) {
		return !pairList.forEachPair(
			(i, j, k) -> pairList2.forEachPair(
					(kx, l, m) -> pairList3.forEachPair(
							(mx, n, o) -> !booleanBiFunction.apply(voxelSet.inBoundsAndContains(i, kx, mx), voxelSet2.inBoundsAndContains(j, l, n))
						)
				)
		);
	}

	public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> stream, double d) {
		Iterator<VoxelShape> iterator = stream.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(d) < 1.0E-7) {
				return 0.0;
			}

			d = ((VoxelShape)iterator.next()).calculateMaxDistance(axis, box, d);
		}

		return d;
	}

	public static double calculatePushVelocity(Direction.Axis axis, Box box, WorldView worldView, double d, EntityContext entityContext, Stream<VoxelShape> stream) {
		return calculatePushVelocity(box, worldView, d, entityContext, AxisCycleDirection.between(axis, Direction.Axis.field_11051), stream);
	}

	private static double calculatePushVelocity(
		Box box, WorldView worldView, double d, EntityContext entityContext, AxisCycleDirection axisCycleDirection, Stream<VoxelShape> stream
	) {
		if (box.getXLength() < 1.0E-6 || box.getYLength() < 1.0E-6 || box.getZLength() < 1.0E-6) {
			return d;
		} else if (Math.abs(d) < 1.0E-7) {
			return 0.0;
		} else {
			AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
			Direction.Axis axis = axisCycleDirection2.cycle(Direction.Axis.field_11048);
			Direction.Axis axis2 = axisCycleDirection2.cycle(Direction.Axis.field_11052);
			Direction.Axis axis3 = axisCycleDirection2.cycle(Direction.Axis.field_11051);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			int i = MathHelper.floor(box.getMin(axis) - 1.0E-7) - 1;
			int j = MathHelper.floor(box.getMax(axis) + 1.0E-7) + 1;
			int k = MathHelper.floor(box.getMin(axis2) - 1.0E-7) - 1;
			int l = MathHelper.floor(box.getMax(axis2) + 1.0E-7) + 1;
			double e = box.getMin(axis3) - 1.0E-7;
			double f = box.getMax(axis3) + 1.0E-7;
			boolean bl = d > 0.0;
			int m = bl ? MathHelper.floor(box.getMax(axis3) - 1.0E-7) - 1 : MathHelper.floor(box.getMin(axis3) + 1.0E-7) + 1;
			int n = clamp(d, e, f);
			int o = bl ? 1 : -1;

			for (int p = m; bl ? p <= n : p >= n; p += o) {
				for (int q = i; q <= j; q++) {
					for (int r = k; r <= l; r++) {
						int s = 0;
						if (q == i || q == j) {
							s++;
						}

						if (r == k || r == l) {
							s++;
						}

						if (p == m || p == n) {
							s++;
						}

						if (s < 3) {
							mutable.set(axisCycleDirection2, q, r, p);
							BlockState blockState = worldView.getBlockState(mutable);
							if ((s != 1 || blockState.exceedsCube()) && (s != 2 || blockState.getBlock() == Blocks.field_10008)) {
								d = blockState.getCollisionShape(worldView, mutable, entityContext)
									.calculateMaxDistance(axis3, box.offset((double)(-mutable.getX()), (double)(-mutable.getY()), (double)(-mutable.getZ())), d);
								if (Math.abs(d) < 1.0E-7) {
									return 0.0;
								}

								n = clamp(d, e, f);
							}
						}
					}
				}
			}

			double[] ds = new double[]{d};
			stream.forEach(voxelShape -> ds[0] = voxelShape.calculateMaxDistance(axis3, box, ds[0]));
			return ds[0];
		}
	}

	private static int clamp(double d, double e, double f) {
		return d > 0.0 ? MathHelper.floor(f + d) + 1 : MathHelper.floor(e + d) - 1;
	}

	public static boolean isSideCovered(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape == fullCube() && voxelShape2 == fullCube()) {
			return true;
		} else if (voxelShape2.isEmpty()) {
			return false;
		} else {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.field_11056 ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.field_11056 ? voxelShape2 : voxelShape;
			BooleanBiFunction booleanBiFunction = axisDirection == Direction.AxisDirection.field_11056 ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
			return DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)
				&& DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)
				&& !matchesAnywhere(
					new SlicedVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SlicedVoxelShape(voxelShape4, axis, 0), booleanBiFunction
				);
		}
	}

	public static VoxelShape extrudeFace(VoxelShape voxelShape, Direction direction) {
		if (voxelShape == fullCube()) {
			return fullCube();
		} else {
			Direction.Axis axis = direction.getAxis();
			boolean bl;
			int i;
			if (direction.getDirection() == Direction.AxisDirection.field_11056) {
				bl = DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis), 1.0, 1.0E-7);
				i = voxelShape.voxels.getSize(axis) - 1;
			} else {
				bl = DoubleMath.fuzzyEquals(voxelShape.getMinimum(axis), 0.0, 1.0E-7);
				i = 0;
			}

			return (VoxelShape)(!bl ? empty() : new SlicedVoxelShape(voxelShape, axis, i));
		}
	}

	public static boolean adjacentSidesCoverSquare(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape != fullCube() && voxelShape2 != fullCube()) {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.field_11056 ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.field_11056 ? voxelShape2 : voxelShape;
			if (!DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)) {
				voxelShape3 = empty();
			}

			if (!DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)) {
				voxelShape4 = empty();
			}

			return !matchesAnywhere(
				fullCube(),
				combine(new SlicedVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SlicedVoxelShape(voxelShape4, axis, 0), BooleanBiFunction.OR),
				BooleanBiFunction.ONLY_FIRST
			);
		} else {
			return true;
		}
	}

	public static boolean unionCoversFullCube(VoxelShape voxelShape, VoxelShape voxelShape2) {
		if (voxelShape == fullCube() || voxelShape2 == fullCube()) {
			return true;
		} else {
			return voxelShape.isEmpty() && voxelShape2.isEmpty()
				? false
				: !matchesAnywhere(fullCube(), combine(voxelShape, voxelShape2, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
		}
	}

	@VisibleForTesting
	protected static PairList createListPair(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int j = doubleList.size() - 1;
		int k = doubleList2.size() - 1;
		if (doubleList instanceof FractionalDoubleList && doubleList2 instanceof FractionalDoubleList) {
			long l = lcm(j, k);
			if ((long)i * l <= 256L) {
				return new FractionalPairList(j, k);
			}
		}

		if (doubleList.getDouble(j) < doubleList2.getDouble(0) - 1.0E-7) {
			return new DisjointPairList(doubleList, doubleList2, false);
		} else if (doubleList2.getDouble(k) < doubleList.getDouble(0) - 1.0E-7) {
			return new DisjointPairList(doubleList2, doubleList, true);
		} else if (j != k || !Objects.equals(doubleList, doubleList2)) {
			return new SimplePairList(doubleList, doubleList2, bl, bl2);
		} else if (doubleList instanceof IdentityPairList) {
			return (PairList)doubleList;
		} else {
			return (PairList)(doubleList2 instanceof IdentityPairList ? (PairList)doubleList2 : new IdentityPairList(doubleList));
		}
	}

	public interface BoxConsumer {
		void consume(double d, double e, double f, double g, double h, double i);
	}
}
