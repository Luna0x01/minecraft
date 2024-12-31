package net.minecraft;

public interface class_4055 extends class_4052, class_4058 {
	int method_17890(class_4040 arg, int i);

	@Override
	default int method_17882(class_4039<?> arg, class_4036 arg2, class_4035 arg3, int i, int j) {
		return this.method_17890(arg, arg3.method_17837(i, j));
	}
}
