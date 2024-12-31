package net.minecraft.client.render;

import net.minecraft.class_4277;

public class DownloadedSkinParser implements BufferedImageSkinProvider {
	@Override
	public class_4277 method_19128(class_4277 arg) {
		boolean bl = arg.method_19478() == 32;
		if (bl) {
			class_4277 lv = new class_4277(64, 64, true);
			lv.method_19470(arg);
			arg.close();
			arg = lv;
			lv.method_19461(0, 32, 64, 32, 0);
			lv.method_19464(4, 16, 16, 32, 4, 4, true, false);
			lv.method_19464(8, 16, 16, 32, 4, 4, true, false);
			lv.method_19464(0, 20, 24, 32, 4, 12, true, false);
			lv.method_19464(4, 20, 16, 32, 4, 12, true, false);
			lv.method_19464(8, 20, 8, 32, 4, 12, true, false);
			lv.method_19464(12, 20, 16, 32, 4, 12, true, false);
			lv.method_19464(44, 16, -8, 32, 4, 4, true, false);
			lv.method_19464(48, 16, -8, 32, 4, 4, true, false);
			lv.method_19464(40, 20, 0, 32, 4, 12, true, false);
			lv.method_19464(44, 20, -8, 32, 4, 12, true, false);
			lv.method_19464(48, 20, -16, 32, 4, 12, true, false);
			lv.method_19464(52, 20, -8, 32, 4, 12, true, false);
		}

		method_19175(arg, 0, 0, 32, 16);
		if (bl) {
			method_19174(arg, 32, 0, 64, 32);
		}

		method_19175(arg, 0, 16, 64, 32);
		method_19175(arg, 16, 48, 48, 64);
		return arg;
	}

	@Override
	public void setAvailable() {
	}

	private static void method_19174(class_4277 arg, int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				int o = arg.method_19459(m, n);
				if ((o >> 24 & 0xFF) < 128) {
					return;
				}
			}
		}

		for (int p = i; p < k; p++) {
			for (int q = j; q < l; q++) {
				arg.method_19460(p, q, arg.method_19459(p, q) & 16777215);
			}
		}
	}

	private static void method_19175(class_4277 arg, int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				arg.method_19460(m, n, arg.method_19459(m, n) | 0xFF000000);
			}
		}
	}
}
