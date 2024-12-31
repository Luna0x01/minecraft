package net.minecraft;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;

final class class_4099 extends VoxelSet {
	private final VoxelSet field_19854;
	private final int field_19855;
	private final int field_19856;
	private final int field_19857;
	private final int field_19858;
	private final int field_19859;
	private final int field_19860;

	public class_4099(VoxelSet voxelSet, int i, int j, int k, int l, int m, int n) {
		super(l - i, m - j, n - k);
		this.field_19854 = voxelSet;
		this.field_19855 = i;
		this.field_19856 = j;
		this.field_19857 = k;
		this.field_19858 = l;
		this.field_19859 = m;
		this.field_19860 = n;
	}

	@Override
	public boolean method_18031(int i, int j, int k) {
		return this.field_19854.method_18031(this.field_19855 + i, this.field_19856 + j, this.field_19857 + k);
	}

	@Override
	public void method_18022(int i, int j, int k, boolean bl, boolean bl2) {
		this.field_19854.method_18022(this.field_19855 + i, this.field_19856 + j, this.field_19857 + k, bl, bl2);
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return Math.max(0, this.field_19854.getMin(axis) - axis.choose(this.field_19855, this.field_19856, this.field_19857));
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return Math.min(
			axis.choose(this.field_19858, this.field_19859, this.field_19860),
			this.field_19854.getMax(axis) - axis.choose(this.field_19855, this.field_19856, this.field_19857)
		);
	}
}
