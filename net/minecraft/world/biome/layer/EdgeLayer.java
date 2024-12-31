package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4055;
import net.minecraft.class_4057;

public class EdgeLayer {
	public static enum class_4043 implements class_4057 {
		INSTANCE;

		@Override
		public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
			return m != 1 || i != 3 && j != 3 && l != 3 && k != 3 && i != 4 && j != 4 && l != 4 && k != 4 ? m : 2;
		}
	}

	public static enum class_4044 implements class_4057 {
		INSTANCE;

		@Override
		public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
			return m != 4 || i != 1 && j != 1 && l != 1 && k != 1 && i != 2 && j != 2 && l != 2 && k != 2 ? m : 3;
		}
	}

	public static enum class_4045 implements class_4055 {
		INSTANCE;

		@Override
		public int method_17890(class_4040 arg, int i) {
			if (!class_4046.method_17863(i) && arg.method_17850(13) == 0) {
				i |= 1 + arg.method_17850(15) << 8 & 3840;
			}

			return i;
		}
	}
}
