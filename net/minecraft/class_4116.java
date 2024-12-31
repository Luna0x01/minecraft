package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

public final class class_4116 implements AutoCloseable {
	private final MinecraftClient field_20022;
	private final Map<Long, class_4111> field_20023 = Maps.newHashMap();
	private final Map<Long, class_4117> field_20024 = Maps.newHashMap();
	private final Map<class_4117, class_4111> field_20025 = Maps.newHashMap();

	public class_4116(MinecraftClient minecraftClient) {
		this.field_20022 = minecraftClient;
		GLFW.glfwSetMonitorCallback(this::method_18289);
		PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();

		for (int i = 0; i < pointerBuffer.limit(); i++) {
			long l = pointerBuffer.get(i);
			this.field_20023.put(l, new class_4111(this, l));
		}
	}

	private void method_18289(long l, int i) {
		if (i == 262145) {
			this.field_20023.put(l, new class_4111(this, l));
		} else if (i == 262146) {
			this.field_20023.remove(l);
		}
	}

	public class_4111 method_18288(long l) {
		return (class_4111)this.field_20023.get(l);
	}

	public class_4111 method_18290(class_4117 arg) {
		long l = GLFW.glfwGetWindowMonitor(arg.method_18315());
		if (l != 0L) {
			return (class_4111)this.field_20023.get(l);
		} else {
			class_4111 lv = (class_4111)this.field_20023.values().iterator().next();
			int i = -1;
			int j = arg.method_18323();
			int k = j + arg.method_18319();
			int m = arg.method_18324();
			int n = m + arg.method_18320();

			for (class_4111 lv2 : this.field_20023.values()) {
				int o = lv2.method_18235();
				int p = o + lv2.method_18233().method_18280();
				int q = lv2.method_18236();
				int r = q + lv2.method_18233().method_18282();
				int s = MathHelper.clamp(j, o, p);
				int t = MathHelper.clamp(k, o, p);
				int u = MathHelper.clamp(m, q, r);
				int v = MathHelper.clamp(n, q, r);
				int w = Math.max(0, t - s);
				int x = Math.max(0, v - u);
				int y = w * x;
				if (y > i) {
					lv = lv2;
					i = y;
				}
			}

			if (lv != this.field_20025.get(arg)) {
				this.field_20025.put(arg, lv);
				GameOptions.Option.FULLSCREEN_RESOLUTION.setMaxValue((float)lv.method_18237());
			}

			return lv;
		}
	}

	public class_4117 method_18291(RunArgs.WindowInformation windowInformation, String string) {
		return new class_4117(this.field_20022, this, windowInformation, string);
	}

	public void close() {
		GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
		if (gLFWMonitorCallback != null) {
			gLFWMonitorCallback.free();
		}
	}
}
