package net.minecraft.util.shape;

import net.minecraft.class_4081;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;

public abstract class VoxelSet {
	private static final Direction.Axis[] field_19837 = Direction.Axis.values();
	protected final int field_19834;
	protected final int field_19835;
	protected final int field_19836;

	protected VoxelSet(int i, int j, int k) {
		this.field_19834 = i;
		this.field_19835 = j;
		this.field_19836 = k;
	}

	public boolean inBoundsAndContains(AxisCycleDirection axisCycleDirection, int i, int j, int k) {
		return this.inBoundsAndContains(
			axisCycleDirection.choose(i, j, k, Direction.Axis.X),
			axisCycleDirection.choose(i, j, k, Direction.Axis.Y),
			axisCycleDirection.choose(i, j, k, Direction.Axis.Z)
		);
	}

	public boolean inBoundsAndContains(int i, int j, int k) {
		if (i < 0 || j < 0 || k < 0) {
			return false;
		} else {
			return i < this.field_19834 && j < this.field_19835 && k < this.field_19836 ? this.method_18031(i, j, k) : false;
		}
	}

	public boolean method_18033(AxisCycleDirection axisCycleDirection, int i, int j, int k) {
		return this.method_18031(
			axisCycleDirection.choose(i, j, k, Direction.Axis.X),
			axisCycleDirection.choose(i, j, k, Direction.Axis.Y),
			axisCycleDirection.choose(i, j, k, Direction.Axis.Z)
		);
	}

	public abstract boolean method_18031(int i, int j, int k);

	public abstract void method_18022(int i, int j, int k, boolean bl, boolean bl2);

	public boolean isEmpty() {
		for (Direction.Axis axis : field_19837) {
			if (this.getMin(axis) >= this.getMax(axis)) {
				return true;
			}
		}

		return false;
	}

	public abstract int getMin(Direction.Axis axis);

	public abstract int getMax(Direction.Axis axis);

	public int method_18029(Direction.Axis axis, int i, int j) {
		int k = this.getSize(axis);
		if (i >= 0 && j >= 0) {
			Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
			Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
			if (i < this.getSize(axis2) && j < this.getSize(axis3)) {
				AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);

				for (int l = 0; l < k; l++) {
					if (this.method_18033(axisCycleDirection, l, i, j)) {
						return l;
					}
				}

				return k;
			} else {
				return k;
			}
		} else {
			return k;
		}
	}

	public int getSize(Direction.Axis axis, int i, int j) {
		if (i >= 0 && j >= 0) {
			Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
			Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
			if (i < this.getSize(axis2) && j < this.getSize(axis3)) {
				int k = this.getSize(axis);
				AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);

				for (int l = k - 1; l >= 0; l--) {
					if (this.method_18033(axisCycleDirection, l, i, j)) {
						return l + 1;
					}
				}

				return 0;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public int getSize(Direction.Axis axis) {
		return axis.choose(this.field_19834, this.field_19835, this.field_19836);
	}

	public int getSizeX() {
		return this.getSize(Direction.Axis.X);
	}

	public int getSizeY() {
		return this.getSize(Direction.Axis.Y);
	}

	public int getSizeZ() {
		return this.getSize(Direction.Axis.Z);
	}

	public void forEachEdge(VoxelSet.class_4088 arg, boolean bl) {
		this.method_18025(arg, AxisCycleDirection.NONE, bl);
		this.method_18025(arg, AxisCycleDirection.FORWARD, bl);
		this.method_18025(arg, AxisCycleDirection.BACKWARD, bl);
	}

	private void method_18025(VoxelSet.class_4088 arg, AxisCycleDirection axisCycleDirection, boolean bl) {
		AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
		int i = this.getSize(axisCycleDirection2.cycle(Direction.Axis.X));
		int j = this.getSize(axisCycleDirection2.cycle(Direction.Axis.Y));
		int k = this.getSize(axisCycleDirection2.cycle(Direction.Axis.Z));

		for (int l = 0; l <= i; l++) {
			for (int m = 0; m <= j; m++) {
				int n = -1;

				for (int o = 0; o <= k; o++) {
					int p = 0;
					int q = 0;

					for (int r = 0; r <= 1; r++) {
						for (int s = 0; s <= 1; s++) {
							if (this.inBoundsAndContains(axisCycleDirection2, l + r - 1, m + s - 1, o)) {
								p++;
								q ^= r ^ s;
							}
						}
					}

					if (p == 1 || p == 3 || p == 2 && (q & 1) == 0) {
						if (bl) {
							if (n == -1) {
								n = o;
							}
						} else {
							arg.consume(
								axisCycleDirection2.choose(l, m, o, Direction.Axis.X),
								axisCycleDirection2.choose(l, m, o, Direction.Axis.Y),
								axisCycleDirection2.choose(l, m, o, Direction.Axis.Z),
								axisCycleDirection2.choose(l, m, o + 1, Direction.Axis.X),
								axisCycleDirection2.choose(l, m, o + 1, Direction.Axis.Y),
								axisCycleDirection2.choose(l, m, o + 1, Direction.Axis.Z)
							);
						}
					} else if (n != -1) {
						arg.consume(
							axisCycleDirection2.choose(l, m, n, Direction.Axis.X),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.Y),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.Z),
							axisCycleDirection2.choose(l, m, o, Direction.Axis.X),
							axisCycleDirection2.choose(l, m, o, Direction.Axis.Y),
							axisCycleDirection2.choose(l, m, o, Direction.Axis.Z)
						);
						n = -1;
					}
				}
			}
		}
	}

	protected boolean method_18019(int i, int j, int k, int l) {
		for (int m = i; m < j; m++) {
			if (!this.inBoundsAndContains(k, l, m)) {
				return false;
			}
		}

		return true;
	}

	protected void method_18021(int i, int j, int k, int l, boolean bl) {
		for (int m = i; m < j; m++) {
			this.method_18022(k, l, m, false, bl);
		}
	}

	protected boolean method_18020(int i, int j, int k, int l, int m) {
		for (int n = i; n < j; n++) {
			if (!this.method_18019(k, l, n, m)) {
				return false;
			}
		}

		return true;
	}

	public void forEachBox(VoxelSet.class_4088 arg, boolean bl) {
		VoxelSet voxelSet = new class_4081(this);

		for (int i = 0; i <= this.field_19834; i++) {
			for (int j = 0; j <= this.field_19835; j++) {
				int k = -1;

				for (int l = 0; l <= this.field_19836; l++) {
					if (voxelSet.inBoundsAndContains(i, j, l)) {
						if (bl) {
							if (k == -1) {
								k = l;
							}
						} else {
							arg.consume(i, j, l, i + 1, j + 1, l + 1);
						}
					} else if (k != -1) {
						int m = i;
						int n = i;
						int o = j;
						int p = j;
						voxelSet.method_18021(k, l, i, j, false);

						while (voxelSet.method_18019(k, l, m - 1, o)) {
							voxelSet.method_18021(k, l, m - 1, o, false);
							m--;
						}

						while (voxelSet.method_18019(k, l, n + 1, o)) {
							voxelSet.method_18021(k, l, n + 1, o, false);
							n++;
						}

						while (voxelSet.method_18020(m, n + 1, k, l, o - 1)) {
							for (int q = m; q <= n; q++) {
								voxelSet.method_18021(k, l, q, o - 1, false);
							}

							o--;
						}

						while (voxelSet.method_18020(m, n + 1, k, l, p + 1)) {
							for (int r = m; r <= n; r++) {
								voxelSet.method_18021(k, l, r, p + 1, false);
							}

							p++;
						}

						arg.consume(m, o, k, n + 1, p + 1, l);
						k = -1;
					}
				}
			}
		}
	}

	public void method_18023(VoxelSet.class_4087 arg) {
		this.method_18024(arg, AxisCycleDirection.NONE);
		this.method_18024(arg, AxisCycleDirection.FORWARD);
		this.method_18024(arg, AxisCycleDirection.BACKWARD);
	}

	private void method_18024(VoxelSet.class_4087 arg, AxisCycleDirection axisCycleDirection) {
		AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
		Direction.Axis axis = axisCycleDirection2.cycle(Direction.Axis.Z);
		int i = this.getSize(axisCycleDirection2.cycle(Direction.Axis.X));
		int j = this.getSize(axisCycleDirection2.cycle(Direction.Axis.Y));
		int k = this.getSize(axis);
		Direction direction = Direction.method_19939(axis, Direction.AxisDirection.NEGATIVE);
		Direction direction2 = Direction.method_19939(axis, Direction.AxisDirection.POSITIVE);

		for (int l = 0; l < i; l++) {
			for (int m = 0; m < j; m++) {
				boolean bl = false;

				for (int n = 0; n <= k; n++) {
					boolean bl2 = n != k && this.method_18033(axisCycleDirection2, l, m, n);
					if (!bl && bl2) {
						arg.consume(
							direction,
							axisCycleDirection2.choose(l, m, n, Direction.Axis.X),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.Y),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.Z)
						);
					}

					if (bl && !bl2) {
						arg.consume(
							direction2,
							axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.X),
							axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.Y),
							axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.Z)
						);
					}

					bl = bl2;
				}
			}
		}
	}

	public interface class_4087 {
		void consume(Direction direction, int i, int j, int k);
	}

	public interface class_4088 {
		void consume(int i, int j, int k, int l, int m, int n);
	}
}
