package net.minecraft;

import net.minecraft.client.input.Input;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class class_3313 implements class_3318 {
	private static final Text field_16205 = new TranslatableText(
		"tutorial.move.title", class_3316.method_14725("forward"), class_3316.method_14725("left"), class_3316.method_14725("back"), class_3316.method_14725("right")
	);
	private static final Text field_16206 = new TranslatableText("tutorial.move.description", class_3316.method_14725("jump"));
	private static final Text field_16207 = new TranslatableText("tutorial.look.title");
	private static final Text field_16208 = new TranslatableText("tutorial.look.description");
	private final class_3316 field_16209;
	private class_3266 field_16210;
	private class_3266 field_16211;
	private int field_16212;
	private int field_16213;
	private int field_16214;
	private boolean field_16215;
	private boolean field_16216;
	private int field_16217 = -1;
	private int field_16218 = -1;

	public class_3313(class_3316 arg) {
		this.field_16209 = arg;
	}

	@Override
	public void method_14731() {
		this.field_16212++;
		if (this.field_16215) {
			this.field_16213++;
			this.field_16215 = false;
		}

		if (this.field_16216) {
			this.field_16214++;
			this.field_16216 = false;
		}

		if (this.field_16217 == -1 && this.field_16213 > 40) {
			if (this.field_16210 != null) {
				this.field_16210.method_14498();
				this.field_16210 = null;
			}

			this.field_16217 = this.field_16212;
		}

		if (this.field_16218 == -1 && this.field_16214 > 40) {
			if (this.field_16211 != null) {
				this.field_16211.method_14498();
				this.field_16211 = null;
			}

			this.field_16218 = this.field_16212;
		}

		if (this.field_16217 != -1 && this.field_16218 != -1) {
			if (this.field_16209.method_14730() == GameMode.SURVIVAL) {
				this.field_16209.method_14724(class_3319.FIND_TREE);
			} else {
				this.field_16209.method_14724(class_3319.NONE);
			}
		}

		if (this.field_16210 != null) {
			this.field_16210.method_14499((float)this.field_16213 / 40.0F);
		}

		if (this.field_16211 != null) {
			this.field_16211.method_14499((float)this.field_16214 / 40.0F);
		}

		if (this.field_16212 >= 100) {
			if (this.field_16217 == -1 && this.field_16210 == null) {
				this.field_16210 = new class_3266(class_3266.class_3267.MOVEMENT_KEYS, field_16205, field_16206, true);
				this.field_16209.method_14729().method_14462().method_14491(this.field_16210);
			} else if (this.field_16217 != -1 && this.field_16212 - this.field_16217 >= 20 && this.field_16218 == -1 && this.field_16211 == null) {
				this.field_16211 = new class_3266(class_3266.class_3267.MOUSE, field_16207, field_16208, true);
				this.field_16209.method_14729().method_14462().method_14491(this.field_16211);
			}
		}
	}

	@Override
	public void method_14737() {
		if (this.field_16210 != null) {
			this.field_16210.method_14498();
			this.field_16210 = null;
		}

		if (this.field_16211 != null) {
			this.field_16211.method_14498();
			this.field_16211 = null;
		}
	}

	@Override
	public void method_14736(Input input) {
		if (input.pressingForward || input.pressingBack || input.pressingLeft || input.pressingRight || input.jumping) {
			this.field_16215 = true;
		}
	}

	@Override
	public void method_19640(double d, double e) {
		if (Math.abs(d) > 0.01 || Math.abs(e) > 0.01) {
			this.field_16216 = true;
		}
	}
}
