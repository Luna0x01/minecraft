package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.text.Text;

public class class_3260 implements class_3262 {
	private final class_3260.class_3261 field_15906;
	private String field_15907;
	private String field_15908;
	private long field_15909;
	private boolean field_15910;

	public class_3260(class_3260.class_3261 arg, Text text, @Nullable Text text2) {
		this.field_15906 = arg;
		this.field_15907 = text.asUnformattedString();
		this.field_15908 = text2 == null ? null : text2.asUnformattedString();
	}

	@Override
	public class_3262.class_3263 method_14486(class_3264 arg, long l) {
		if (this.field_15910) {
			this.field_15909 = l;
			this.field_15910 = false;
		}

		arg.method_14494().getTextureManager().bindTexture(field_15914);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		arg.drawTexture(0, 0, 0, 64, 160, 32);
		if (this.field_15908 == null) {
			arg.method_14494().textRenderer.draw(this.field_15907, 18, 12, -256);
		} else {
			arg.method_14494().textRenderer.draw(this.field_15907, 18, 7, -256);
			arg.method_14494().textRenderer.draw(this.field_15908, 18, 18, -1);
		}

		return l - this.field_15909 < 5000L ? class_3262.class_3263.SHOW : class_3262.class_3263.HIDE;
	}

	public void method_14485(Text text, @Nullable Text text2) {
		this.field_15907 = text.asUnformattedString();
		this.field_15908 = text2 == null ? null : text2.asUnformattedString();
		this.field_15910 = true;
	}

	public class_3260.class_3261 method_14487() {
		return this.field_15906;
	}

	public static void method_14484(class_3264 arg, class_3260.class_3261 arg2, Text text, @Nullable Text text2) {
		class_3260 lv = arg.method_14493(class_3260.class, arg2);
		if (lv == null) {
			arg.method_14491(new class_3260(arg2, text, text2));
		} else {
			lv.method_14485(text, text2);
		}
	}

	public static enum class_3261 {
		TUTORIAL_HINT,
		NARRATOR_TOGGLE;
	}
}
