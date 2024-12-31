package net.minecraft.world;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PortalUtil {
	public static PortalUtil.Rectangle getLargestRectangle(
		BlockPos center, Direction.Axis primaryAxis, int primaryMaxBlocks, Direction.Axis secondaryAxis, int secondaryMaxBlocks, Predicate<BlockPos> predicate
	) {
		BlockPos.Mutable mutable = center.mutableCopy();
		Direction direction = Direction.get(Direction.AxisDirection.NEGATIVE, primaryAxis);
		Direction direction2 = direction.getOpposite();
		Direction direction3 = Direction.get(Direction.AxisDirection.NEGATIVE, secondaryAxis);
		Direction direction4 = direction3.getOpposite();
		int i = moveWhile(predicate, mutable.set(center), direction, primaryMaxBlocks);
		int j = moveWhile(predicate, mutable.set(center), direction2, primaryMaxBlocks);
		int k = i;
		PortalUtil.IntBounds[] intBoundss = new PortalUtil.IntBounds[i + 1 + j];
		intBoundss[i] = new PortalUtil.IntBounds(
			moveWhile(predicate, mutable.set(center), direction3, secondaryMaxBlocks), moveWhile(predicate, mutable.set(center), direction4, secondaryMaxBlocks)
		);
		int l = intBoundss[i].min;

		for (int m = 1; m <= i; m++) {
			PortalUtil.IntBounds intBounds = intBoundss[k - (m - 1)];
			intBoundss[k - m] = new PortalUtil.IntBounds(
				moveWhile(predicate, mutable.set(center).move(direction, m), direction3, intBounds.min),
				moveWhile(predicate, mutable.set(center).move(direction, m), direction4, intBounds.max)
			);
		}

		for (int n = 1; n <= j; n++) {
			PortalUtil.IntBounds intBounds2 = intBoundss[k + n - 1];
			intBoundss[k + n] = new PortalUtil.IntBounds(
				moveWhile(predicate, mutable.set(center).move(direction2, n), direction3, intBounds2.min),
				moveWhile(predicate, mutable.set(center).move(direction2, n), direction4, intBounds2.max)
			);
		}

		int o = 0;
		int p = 0;
		int q = 0;
		int r = 0;
		int[] is = new int[intBoundss.length];

		for (int s = l; s >= 0; s--) {
			for (int t = 0; t < intBoundss.length; t++) {
				PortalUtil.IntBounds intBounds3 = intBoundss[t];
				int u = l - intBounds3.min;
				int v = l + intBounds3.max;
				is[t] = s >= u && s <= v ? v + 1 - s : 0;
			}

			Pair<PortalUtil.IntBounds, Integer> pair = findLargestRectangle(is);
			PortalUtil.IntBounds intBounds4 = (PortalUtil.IntBounds)pair.getFirst();
			int w = 1 + intBounds4.max - intBounds4.min;
			int x = (Integer)pair.getSecond();
			if (w * x > q * r) {
				o = intBounds4.min;
				p = s;
				q = w;
				r = x;
			}
		}

		return new PortalUtil.Rectangle(center.offset(primaryAxis, o - k).offset(secondaryAxis, p - l), q, r);
	}

	private static int moveWhile(Predicate<BlockPos> predicate, BlockPos.Mutable mutable, Direction direction, int max) {
		int i = 0;

		while (i < max && predicate.test(mutable.move(direction))) {
			i++;
		}

		return i;
	}

	@VisibleForTesting
	static Pair<PortalUtil.IntBounds, Integer> findLargestRectangle(int[] heights) {
		int i = 0;
		int j = 0;
		int k = 0;
		IntStack intStack = new IntArrayList();
		intStack.push(0);

		for (int l = 1; l <= heights.length; l++) {
			int m = l == heights.length ? 0 : heights[l];

			while (!intStack.isEmpty()) {
				int n = heights[intStack.topInt()];
				if (m >= n) {
					intStack.push(l);
					break;
				}

				intStack.popInt();
				int o = intStack.isEmpty() ? 0 : intStack.topInt() + 1;
				if (n * (l - o) > k * (j - i)) {
					j = l;
					i = o;
					k = n;
				}
			}

			if (intStack.isEmpty()) {
				intStack.push(l);
			}
		}

		return new Pair(new PortalUtil.IntBounds(i, j - 1), k);
	}

	public static class IntBounds {
		public final int min;
		public final int max;

		public IntBounds(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public String toString() {
			return "IntBounds{min=" + this.min + ", max=" + this.max + '}';
		}
	}

	public static class Rectangle {
		public final BlockPos lowerLeft;
		public final int width;
		public final int height;

		public Rectangle(BlockPos lowerLeft, int width, int height) {
			this.lowerLeft = lowerLeft;
			this.width = width;
			this.height = height;
		}
	}
}
