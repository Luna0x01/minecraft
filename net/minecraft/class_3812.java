package net.minecraft;

import java.util.Random;

public class class_3812 extends Random {
	private int field_19067;

	public class_3812() {
	}

	public class_3812(long l) {
		super(l);
	}

	public void method_17285(int i) {
		for (int j = 0; j < i; j++) {
			this.next(1);
		}
	}

	protected int next(int i) {
		this.field_19067++;
		return super.next(i);
	}

	public long method_17286(int i, int j) {
		long l = (long)i * 341873128712L + (long)j * 132897987541L;
		this.setSeed(l);
		return l;
	}

	public long method_17288(long l, int i, int j) {
		this.setSeed(l);
		long m = this.nextLong() | 1L;
		long n = this.nextLong() | 1L;
		long o = (long)i * m + (long)j * n ^ l;
		this.setSeed(o);
		return o;
	}

	public long method_17290(long l, int i, int j) {
		long m = l + (long)i + (long)(10000 * j);
		this.setSeed(m);
		return m;
	}

	public long method_17291(long l, int i, int j) {
		this.setSeed(l);
		long m = this.nextLong();
		long n = this.nextLong();
		long o = (long)i * m ^ (long)j * n ^ l;
		this.setSeed(o);
		return o;
	}

	public long method_17289(long l, int i, int j, int k) {
		long m = (long)i * 341873128712L + (long)j * 132897987541L + l + (long)k;
		this.setSeed(m);
		return m;
	}

	public static Random method_17287(int i, int j, long l, long m) {
		return new Random(l + (long)(i * i * 4987142) + (long)(i * 5947611) + (long)(j * j) * 4392871L + (long)(j * 389711) ^ m);
	}
}
