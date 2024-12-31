package net.minecraft;

public interface class_4052 extends class_4060 {
	default <R extends class_4035> class_4037<R> method_17883(class_4039<R> arg, class_4037<R> arg2) {
		return arg3 -> {
			R lv = arg2.make(this.method_17893(arg3));
			return arg.method_17846(arg3, (i, j) -> {
				arg.method_17844((long)(i + arg3.method_17838()), (long)(j + arg3.method_17839()));
				return this.method_17882(arg, arg3, lv, i, j);
			}, lv);
		};
	}

	int method_17882(class_4039<?> arg, class_4036 arg2, class_4035 arg3, int i, int j);
}
