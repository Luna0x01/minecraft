package net.minecraft;

import java.util.Random;
import net.minecraft.world.gen.class_1779;

public abstract class class_4041<R extends class_4035> implements class_4039<R> {
	private long field_19545;
	private long field_19546;
	protected long field_19543;
	protected class_1779 field_19544;

	public class_4041(long l) {
		this.field_19543 = l;
		this.field_19543 = this.field_19543 * (this.field_19543 * 6364136223846793005L + 1442695040888963407L);
		this.field_19543 += l;
		this.field_19543 = this.field_19543 * (this.field_19543 * 6364136223846793005L + 1442695040888963407L);
		this.field_19543 += l;
		this.field_19543 = this.field_19543 * (this.field_19543 * 6364136223846793005L + 1442695040888963407L);
		this.field_19543 += l;
	}

	public void method_17851(long l) {
		this.field_19545 = l;
		this.field_19545 = this.field_19545 * (this.field_19545 * 6364136223846793005L + 1442695040888963407L);
		this.field_19545 = this.field_19545 + this.field_19543;
		this.field_19545 = this.field_19545 * (this.field_19545 * 6364136223846793005L + 1442695040888963407L);
		this.field_19545 = this.field_19545 + this.field_19543;
		this.field_19545 = this.field_19545 * (this.field_19545 * 6364136223846793005L + 1442695040888963407L);
		this.field_19545 = this.field_19545 + this.field_19543;
		this.field_19544 = new class_1779(new Random(l));
	}

	@Override
	public void method_17844(long l, long m) {
		this.field_19546 = this.field_19545;
		this.field_19546 = this.field_19546 * (this.field_19546 * 6364136223846793005L + 1442695040888963407L);
		this.field_19546 += l;
		this.field_19546 = this.field_19546 * (this.field_19546 * 6364136223846793005L + 1442695040888963407L);
		this.field_19546 += m;
		this.field_19546 = this.field_19546 * (this.field_19546 * 6364136223846793005L + 1442695040888963407L);
		this.field_19546 += l;
		this.field_19546 = this.field_19546 * (this.field_19546 * 6364136223846793005L + 1442695040888963407L);
		this.field_19546 += m;
	}

	@Override
	public int method_17850(int i) {
		int j = (int)((this.field_19546 >> 24) % (long)i);
		if (j < 0) {
			j += i;
		}

		this.field_19546 = this.field_19546 * (this.field_19546 * 6364136223846793005L + 1442695040888963407L);
		this.field_19546 = this.field_19546 + this.field_19545;
		return j;
	}

	@Override
	public int method_17848(int... is) {
		return is[this.method_17850(is.length)];
	}

	@Override
	public class_1779 method_17849() {
		return this.field_19544;
	}
}
