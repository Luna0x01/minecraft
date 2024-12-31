package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class class_4111 {
	private final class_4116 field_19949;
	private final long field_19950;
	private final List<class_4115> field_19951;
	private class_4115 field_19952;
	private int field_19953;
	private int field_19954;

	public class_4111(class_4116 arg, long l) {
		this.field_19949 = arg;
		this.field_19950 = l;
		this.field_19951 = Lists.newArrayList();
		this.method_18230();
	}

	public void method_18230() {
		this.field_19951.clear();
		Buffer buffer = GLFW.glfwGetVideoModes(this.field_19950);

		for (int i = 0; i < buffer.limit(); i++) {
			buffer.position(i);
			class_4115 lv = new class_4115(buffer);
			if (lv.method_18283() >= 8 && lv.method_18284() >= 8 && lv.method_18285() >= 8) {
				this.field_19951.add(lv);
			}
		}

		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetMonitorPos(this.field_19950, is, js);
		this.field_19953 = is[0];
		this.field_19954 = js[0];
		GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(this.field_19950);
		this.field_19952 = new class_4115(gLFWVidMode);
	}

	class_4115 method_18232(Optional<class_4115> optional) {
		if (optional.isPresent()) {
			class_4115 lv = (class_4115)optional.get();

			for (class_4115 lv2 : Lists.reverse(this.field_19951)) {
				if (lv2.equals(lv)) {
					return lv2;
				}
			}
		}

		return this.method_18233();
	}

	int method_18234(Optional<class_4115> optional) {
		if (optional.isPresent()) {
			class_4115 lv = (class_4115)optional.get();

			for (int i = this.field_19951.size() - 1; i >= 0; i--) {
				if (lv.equals(this.field_19951.get(i))) {
					return i;
				}
			}
		}

		return this.field_19951.indexOf(this.method_18233());
	}

	public class_4115 method_18233() {
		return this.field_19952;
	}

	public int method_18235() {
		return this.field_19953;
	}

	public int method_18236() {
		return this.field_19954;
	}

	public class_4115 method_18231(int i) {
		return (class_4115)this.field_19951.get(i);
	}

	public int method_18237() {
		return this.field_19951.size();
	}

	public long method_18238() {
		return this.field_19950;
	}

	public String toString() {
		return String.format("Monitor[%s %sx%s %s]", this.field_19950, this.field_19953, this.field_19954, this.field_19952);
	}
}
