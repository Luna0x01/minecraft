package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DrawableHelper;

public abstract class class_4121 extends DrawableHelper implements class_4123 {
	@Nullable
	private class_4122 field_20087;
	private boolean field_20088;

	protected abstract List<? extends class_4122> method_18423();

	private final boolean method_18419() {
		return this.field_20088;
	}

	protected final void method_18425(boolean bl) {
		this.field_20088 = bl;
	}

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.field_20087;
	}

	protected void method_18421(@Nullable class_4122 arg) {
		this.field_20087 = arg;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		for (class_4122 lv : this.method_18423()) {
			boolean bl = lv.mouseClicked(d, e, i);
			if (bl) {
				this.method_18424(lv);
				if (i == 0) {
					this.method_18425(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return class_4123.super.keyPressed(i, j, k);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.method_18419() && i == 0 ? this.getFocused().mouseDragged(d, e, i, f, g) : false;
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		this.method_18425(false);
		return class_4123.super.mouseReleased(d, e, i);
	}

	public void method_18424(@Nullable class_4122 arg) {
		this.method_18422(arg, this.method_18423().indexOf(this.getFocused()));
	}

	public void method_18426() {
		int i = this.method_18423().indexOf(this.getFocused());
		int j = i == -1 ? 0 : (i + 1) % this.method_18423().size();
		this.method_18422(this.method_18420(j), i);
	}

	@Nullable
	private class_4122 method_18420(int i) {
		List<? extends class_4122> list = this.method_18423();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			class_4122 lv = (class_4122)list.get((i + k) % j);
			if (lv.method_18427()) {
				return lv;
			}
		}

		return null;
	}

	private void method_18422(@Nullable class_4122 arg, int i) {
		class_4122 lv = i == -1 ? null : (class_4122)this.method_18423().get(i);
		if (lv != arg) {
			if (lv != null) {
				lv.method_18428(false);
			}

			if (arg != null) {
				arg.method_18428(true);
			}

			this.method_18421(arg);
		}
	}
}
