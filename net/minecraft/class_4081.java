package net.minecraft;

import java.util.BitSet;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;

public final class class_4081 extends VoxelSet {
	private final BitSet field_19821;
	private int field_19822;
	private int field_19823;
	private int field_19824;
	private int field_19825;
	private int field_19826;
	private int field_19827;

	public class_4081(int i, int j, int k) {
		this(i, j, k, i, j, k, 0, 0, 0);
	}

	public class_4081(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		super(i, j, k);
		this.field_19821 = new BitSet(i * j * k);
		this.field_19822 = l;
		this.field_19823 = m;
		this.field_19824 = n;
		this.field_19825 = o;
		this.field_19826 = p;
		this.field_19827 = q;
	}

	public class_4081(VoxelSet voxelSet) {
		super(voxelSet.field_19834, voxelSet.field_19835, voxelSet.field_19836);
		if (voxelSet instanceof class_4081) {
			this.field_19821 = (BitSet)((class_4081)voxelSet).field_19821.clone();
		} else {
			this.field_19821 = new BitSet(this.field_19834 * this.field_19835 * this.field_19836);

			for (int i = 0; i < this.field_19834; i++) {
				for (int j = 0; j < this.field_19835; j++) {
					for (int k = 0; k < this.field_19836; k++) {
						if (voxelSet.method_18031(i, j, k)) {
							this.field_19821.set(this.method_18013(i, j, k));
						}
					}
				}
			}
		}

		this.field_19822 = voxelSet.getMin(Direction.Axis.X);
		this.field_19823 = voxelSet.getMin(Direction.Axis.Y);
		this.field_19824 = voxelSet.getMin(Direction.Axis.Z);
		this.field_19825 = voxelSet.getMax(Direction.Axis.X);
		this.field_19826 = voxelSet.getMax(Direction.Axis.Y);
		this.field_19827 = voxelSet.getMax(Direction.Axis.Z);
	}

	protected int method_18013(int i, int j, int k) {
		return (i * this.field_19835 + j) * this.field_19836 + k;
	}

	@Override
	public boolean method_18031(int i, int j, int k) {
		return this.field_19821.get(this.method_18013(i, j, k));
	}

	@Override
	public void method_18022(int i, int j, int k, boolean bl, boolean bl2) {
		this.field_19821.set(this.method_18013(i, j, k), bl2);
		if (bl && bl2) {
			this.field_19822 = Math.min(this.field_19822, i);
			this.field_19823 = Math.min(this.field_19823, j);
			this.field_19824 = Math.min(this.field_19824, k);
			this.field_19825 = Math.max(this.field_19825, i + 1);
			this.field_19826 = Math.max(this.field_19826, j + 1);
			this.field_19827 = Math.max(this.field_19827, k + 1);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.field_19821.isEmpty();
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return axis.choose(this.field_19822, this.field_19823, this.field_19824);
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return axis.choose(this.field_19825, this.field_19826, this.field_19827);
	}

	@Override
	protected boolean method_18019(int i, int j, int k, int l) {
		if (k < 0 || l < 0 || i < 0) {
			return false;
		} else {
			return k < this.field_19834 && l < this.field_19835 && j <= this.field_19836
				? this.field_19821.nextClearBit(this.method_18013(k, l, i)) >= this.method_18013(k, l, j)
				: false;
		}
	}

	@Override
	protected void method_18021(int i, int j, int k, int l, boolean bl) {
		this.field_19821.set(this.method_18013(k, l, i), this.method_18013(k, l, j), bl);
	}

	static class_4081 method_18015(VoxelSet voxelSet, VoxelSet voxelSet2, class_4090 arg, class_4090 arg2, class_4090 arg3, BooleanBiFunction booleanBiFunction) {
		class_4081 lv = new class_4081(arg.method_18040().size() - 1, arg2.method_18040().size() - 1, arg3.method_18040().size() - 1);
		int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		arg.method_18041((i, j, k) -> {
			boolean[] bls = new boolean[]{false};
			boolean bl = arg2.method_18041((l, m, n) -> {
				boolean[] bls2 = new boolean[]{false};
				boolean blx = arg3.method_18041((o, p, q) -> {
					boolean blxx = booleanBiFunction.apply(voxelSet.inBoundsAndContains(i, l, o), voxelSet2.inBoundsAndContains(j, m, p));
					if (blxx) {
						lv.field_19821.set(lv.method_18013(k, n, q));
						is[2] = Math.min(is[2], q);
						is[5] = Math.max(is[5], q);
						bls2[0] = true;
					}

					return true;
				});
				if (bls2[0]) {
					is[1] = Math.min(is[1], n);
					is[4] = Math.max(is[4], n);
					bls[0] = true;
				}

				return blx;
			});
			if (bls[0]) {
				is[0] = Math.min(is[0], k);
				is[3] = Math.max(is[3], k);
			}

			return bl;
		});
		lv.field_19822 = is[0];
		lv.field_19823 = is[1];
		lv.field_19824 = is[2];
		lv.field_19825 = is[3] + 1;
		lv.field_19826 = is[4] + 1;
		lv.field_19827 = is[5] + 1;
		return lv;
	}
}
