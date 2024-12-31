package net.minecraft.util.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.class_4081;
import net.minecraft.class_4083;
import net.minecraft.class_4084;
import net.minecraft.class_4085;
import net.minecraft.class_4089;
import net.minecraft.class_4090;
import net.minecraft.class_4092;
import net.minecraft.class_4094;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.SliceVoxelShape;
import net.minecraft.util.shape.VoxelSet;

public final class VoxelShapes {
	private static final VoxelShape field_19849 = new ArrayVoxelShape(
		new class_4081(0, 0, 0), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0})
	);
	private static final VoxelShape field_19850 = Util.make(() -> {
		VoxelSet voxelSet = new class_4081(1, 1, 1);
		voxelSet.method_18022(0, 0, 0, true, true);
		return new class_4084(voxelSet);
	});

	public static VoxelShape empty() {
		return field_19849;
	}

	public static VoxelShape matchesAnywhere() {
		return field_19850;
	}

	public static VoxelShape cuboid(double d, double e, double f, double g, double h, double i) {
		return method_18049(new Box(d, e, f, g, h, i));
	}

	public static VoxelShape method_18049(Box box) {
		int i = method_18045(box.minX, box.maxX);
		int j = method_18045(box.minY, box.maxY);
		int k = method_18045(box.minZ, box.maxZ);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return box.method_18007(0.5, 0.5, 0.5) ? matchesAnywhere() : empty();
			} else {
				int l = 1 << i;
				int m = 1 << j;
				int n = 1 << k;
				int o = (int)Math.round(box.minX * (double)l);
				int p = (int)Math.round(box.maxX * (double)l);
				int q = (int)Math.round(box.minY * (double)m);
				int r = (int)Math.round(box.maxY * (double)m);
				int s = (int)Math.round(box.minZ * (double)n);
				int t = (int)Math.round(box.maxZ * (double)n);
				class_4081 lv = new class_4081(l, m, n, o, q, s, p, r, t);

				for (long u = (long)o; u < (long)p; u++) {
					for (long v = (long)q; v < (long)r; v++) {
						for (long w = (long)s; w < (long)t; w++) {
							lv.method_18022((int)u, (int)v, (int)w, false, true);
						}
					}
				}

				return new class_4084(lv);
			}
		} else {
			return new ArrayVoxelShape(field_19850.voxels, new double[]{box.minX, box.maxX}, new double[]{box.minY, box.maxY}, new double[]{box.minZ, box.maxZ});
		}
	}

	private static int method_18045(double d, double e) {
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

	protected static long method_18047(int i, int j) {
		return (long)i * (long)(j / IntMath.gcd(i, j));
	}

	public static VoxelShape union(VoxelShape first, VoxelShape second) {
		return combineAndSimplify(first, second, BooleanBiFunction.OR);
	}

	public static VoxelShape combineAndSimplify(VoxelShape first, VoxelShape second, BooleanBiFunction function) {
		return combine(first, second, function).simplify();
	}

	public static VoxelShape combine(VoxelShape first, VoxelShape second, BooleanBiFunction function) {
		if (function.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (first == second) {
			return function.apply(true, true) ? first : empty();
		} else {
			boolean bl = function.apply(true, false);
			boolean bl2 = function.apply(false, true);
			if (first.isEmpty()) {
				return bl2 ? second : empty();
			} else if (second.isEmpty()) {
				return bl ? first : empty();
			} else {
				class_4090 lv = method_18048(1, first.getIncludedPoints(Direction.Axis.X), second.getIncludedPoints(Direction.Axis.X), bl, bl2);
				class_4090 lv2 = method_18048(lv.method_18040().size() - 1, first.getIncludedPoints(Direction.Axis.Y), second.getIncludedPoints(Direction.Axis.Y), bl, bl2);
				class_4090 lv3 = method_18048(
					(lv.method_18040().size() - 1) * (lv2.method_18040().size() - 1),
					first.getIncludedPoints(Direction.Axis.Z),
					second.getIncludedPoints(Direction.Axis.Z),
					bl,
					bl2
				);
				class_4081 lv4 = class_4081.method_18015(first.voxels, second.voxels, lv, lv2, lv3, function);
				return (VoxelShape)(lv instanceof class_4085 && lv2 instanceof class_4085 && lv3 instanceof class_4085
					? new class_4084(lv4)
					: new ArrayVoxelShape(lv4, lv.method_18040(), lv2.method_18040(), lv3.method_18040()));
			}
		}
	}

	public static boolean matchesAnywhere(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		if (booleanBiFunction.apply(false, false)) {
			throw new IllegalArgumentException();
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

			class_4090 lv = method_18048(1, voxelShape.getIncludedPoints(Direction.Axis.X), voxelShape2.getIncludedPoints(Direction.Axis.X), bl, bl2);
			class_4090 lv2 = method_18048(
				lv.method_18040().size() - 1, voxelShape.getIncludedPoints(Direction.Axis.Y), voxelShape2.getIncludedPoints(Direction.Axis.Y), bl, bl2
			);
			class_4090 lv3 = method_18048(
				(lv.method_18040().size() - 1) * (lv2.method_18040().size() - 1),
				voxelShape.getIncludedPoints(Direction.Axis.Z),
				voxelShape2.getIncludedPoints(Direction.Axis.Z),
				bl,
				bl2
			);
			return method_18053(lv, lv2, lv3, voxelShape.voxels, voxelShape2.voxels, booleanBiFunction);
		}
	}

	private static boolean method_18053(
		class_4090 arg, class_4090 arg2, class_4090 arg3, VoxelSet voxelSet, VoxelSet voxelSet2, BooleanBiFunction booleanBiFunction
	) {
		return !arg.method_18041(
			(i, j, k) -> arg2.method_18041(
					(kx, l, m) -> arg3.method_18041((mx, n, o) -> !booleanBiFunction.apply(voxelSet.inBoundsAndContains(i, kx, mx), voxelSet2.inBoundsAndContains(j, l, n)))
				)
		);
	}

	public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> stream, double d) {
		Iterator<VoxelShape> iterator = stream.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(d) < 1.0E-7) {
				return 0.0;
			}

			d = ((VoxelShape)iterator.next()).method_18075(axis, box, d);
		}

		return d;
	}

	public static boolean method_18056(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape == matchesAnywhere() && voxelShape2 == matchesAnywhere()) {
			return true;
		} else if (voxelShape2.isEmpty()) {
			return false;
		} else {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getAxisDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
			BooleanBiFunction booleanBiFunction = axisDirection == Direction.AxisDirection.POSITIVE ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
			return DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)
				&& DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)
				&& !matchesAnywhere(
					new SliceVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape4, axis, 0), booleanBiFunction
				);
		}
	}

	public static boolean method_18060(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape != matchesAnywhere() && voxelShape2 != matchesAnywhere()) {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getAxisDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
			if (!DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)) {
				voxelShape3 = empty();
			}

			if (!DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)) {
				voxelShape4 = empty();
			}

			return !matchesAnywhere(
				matchesAnywhere(),
				combine(new SliceVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape4, axis, 0), BooleanBiFunction.OR),
				BooleanBiFunction.ONLY_FIRST
			);
		} else {
			return true;
		}
	}

	@VisibleForTesting
	protected static class_4090 method_18048(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		if (doubleList instanceof class_4083 && doubleList2 instanceof class_4083) {
			int j = doubleList.size() - 1;
			int k = doubleList2.size() - 1;
			long l = method_18047(j, k);
			if ((long)i * l <= 256L) {
				return new class_4085(j, k);
			}
		}

		if (doubleList.getDouble(doubleList.size() - 1) < doubleList2.getDouble(0) - 1.0E-7) {
			return new class_4094(doubleList, doubleList2, false);
		} else if (doubleList2.getDouble(doubleList2.size() - 1) < doubleList.getDouble(0) - 1.0E-7) {
			return new class_4094(doubleList2, doubleList, true);
		} else if (Objects.equals(doubleList, doubleList2)) {
			if (doubleList instanceof class_4089) {
				return (class_4090)doubleList;
			} else {
				return (class_4090)(doubleList2 instanceof class_4089 ? (class_4090)doubleList2 : new class_4089(doubleList));
			}
		} else {
			return new class_4092(doubleList, doubleList2, bl, bl2);
		}
	}

	public interface BoxConsumer {
		void consume(double d, double e, double f, double g, double h, double i);
	}
}
