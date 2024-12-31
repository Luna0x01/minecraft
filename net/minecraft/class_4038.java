package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public final class class_4038 implements class_4035 {
	private final class_4061 field_19539;
	private final Long2IntLinkedOpenHashMap field_19540;
	private final int field_19541;
	private final class_4036 field_19542;

	public class_4038(Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap, int i, class_4036 arg, class_4061 arg2) {
		this.field_19540 = long2IntLinkedOpenHashMap;
		this.field_19541 = i;
		this.field_19542 = arg;
		this.field_19539 = arg2;
	}

	@Override
	public int method_17837(int i, int j) {
		long l = this.method_17843(i, j);
		synchronized (this.field_19540) {
			int k = this.field_19540.get(l);
			if (k != Integer.MIN_VALUE) {
				return k;
			} else {
				int m = this.field_19539.apply(i, j);
				this.field_19540.put(l, m);
				if (this.field_19540.size() > this.field_19541) {
					for (int n = 0; n < this.field_19541 / 16; n++) {
						this.field_19540.removeFirstInt();
					}
				}

				return m;
			}
		}
	}

	private long method_17843(int i, int j) {
		long l = 1L;
		l <<= 26;
		l |= (long)(i + this.field_19542.method_17838()) & 67108863L;
		l <<= 26;
		return l | (long)(j + this.field_19542.method_17839()) & 67108863L;
	}

	public int method_17842() {
		return this.field_19541;
	}
}
