package net.minecraft;

public interface class_4056 extends class_4052, class_4059 {
	int method_17891(class_4040 arg, int i);

	@Override
	default int method_17882(class_4039<?> arg, class_4036 arg2, class_4035 arg3, int i, int j) {
		int k = arg3.method_17837(i + 1, j + 1);
		return this.method_17891(arg, k);
	}
}
