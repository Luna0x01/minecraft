package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public class class_4042 extends class_4041<class_4038> {
	private final Long2IntLinkedOpenHashMap field_19547 = new Long2IntLinkedOpenHashMap(16, 0.25F);
	private final int field_19548;
	private final int field_19549;

	public class_4042(int i, int j, long l, long m) {
		super(m);
		this.field_19547.defaultReturnValue(Integer.MIN_VALUE);
		this.field_19548 = i;
		this.field_19549 = j;
		this.method_17851(l);
	}

	public class_4038 method_17845(class_4036 arg, class_4061 arg2) {
		return new class_4038(this.field_19547, this.field_19548, arg, arg2);
	}

	public class_4038 method_17846(class_4036 arg, class_4061 arg2, class_4038 arg3) {
		return new class_4038(this.field_19547, Math.min(256, arg3.method_17842() * 4), arg, arg2);
	}

	public class_4038 method_17847(class_4036 arg, class_4061 arg2, class_4038 arg3, class_4038 arg4) {
		return new class_4038(this.field_19547, Math.min(256, Math.max(arg3.method_17842(), arg4.method_17842()) * 4), arg, arg2);
	}
}
