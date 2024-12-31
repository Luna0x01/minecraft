package net.minecraft;

public interface class_4053 extends class_4060 {
	default <R extends class_4035> class_4037<R> method_17887(class_4039<R> arg, class_4037<R> arg2, class_4037<R> arg3) {
		return arg4 -> {
			R lv = arg2.make(this.method_17893(arg4));
			R lv2 = arg3.make(this.method_17893(arg4));
			return arg.method_17847(arg4, (i, j) -> {
				arg.method_17844((long)(i + arg4.method_17838()), (long)(j + arg4.method_17839()));
				return this.method_17888(arg, arg4, lv, lv2, i, j);
			}, lv, lv2);
		};
	}

	int method_17888(class_4040 arg, class_4036 arg2, class_4035 arg3, class_4035 arg4, int i, int j);
}
