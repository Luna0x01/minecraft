package net.minecraft;

public interface class_4057 extends class_4052, class_4059 {
	int method_17892(class_4040 arg, int i, int j, int k, int l, int m);

	@Override
	default int method_17882(class_4039<?> arg, class_4036 arg2, class_4035 arg3, int i, int j) {
		return this.method_17892(
			arg,
			arg3.method_17837(i + 1, j + 0),
			arg3.method_17837(i + 2, j + 1),
			arg3.method_17837(i + 1, j + 2),
			arg3.method_17837(i + 0, j + 1),
			arg3.method_17837(i + 1, j + 1)
		);
	}
}
