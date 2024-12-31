package net.minecraft;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shapes.VoxelShapes;

public final class class_4085 implements class_4090 {
	private final class_4083 field_19830;
	private final int field_19831;
	private final int field_19832;
	private final int field_19833;

	class_4085(int i, int j) {
		this.field_19830 = new class_4083((int)VoxelShapes.method_18047(i, j));
		this.field_19831 = i;
		this.field_19832 = j;
		this.field_19833 = IntMath.gcd(i, j);
	}

	@Override
	public boolean method_18041(class_4090.class_4091 arg) {
		int i = this.field_19831 / this.field_19833;
		int j = this.field_19832 / this.field_19833;

		for (int k = 0; k <= this.field_19830.size(); k++) {
			if (!arg.merge(k / j, k / i, k)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList method_18040() {
		return this.field_19830;
	}
}
