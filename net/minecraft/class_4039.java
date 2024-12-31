package net.minecraft;

public interface class_4039<R extends class_4035> extends class_4040 {
	void method_17844(long l, long m);

	R method_17845(class_4036 arg, class_4061 arg2);

	default R method_17846(class_4036 arg, class_4061 arg2, R arg3) {
		return this.method_17845(arg, arg2);
	}

	default R method_17847(class_4036 arg, class_4061 arg2, R arg3, R arg4) {
		return this.method_17845(arg, arg2);
	}

	int method_17848(int... is);
}
