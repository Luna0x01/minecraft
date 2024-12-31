package net.minecraft;

public interface class_4051 {
	default <R extends class_4035> class_4037<R> method_17877(class_4039<R> arg) {
		return arg2 -> arg.method_17845(arg2, (i, j) -> {
				arg.method_17844((long)(i + arg2.method_17838()), (long)(j + arg2.method_17839()));
				return this.method_17880(arg, arg2, i, j);
			});
	}

	int method_17880(class_4040 arg, class_4036 arg2, int i, int j);
}
