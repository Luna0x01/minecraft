package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;

public abstract class LockButtonWidget extends ButtonWidget {
	private boolean locked;

	public LockButtonWidget(int i, int j, int k) {
		super(i, j, k, 20, 20, "");
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public void method_891(int i, int j, float f) {
		if (this.visible) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			LockButtonWidget.IconLocation iconLocation;
			if (this.locked) {
				if (!this.active) {
					iconLocation = LockButtonWidget.IconLocation.LOCKED_DISABLED;
				} else if (bl) {
					iconLocation = LockButtonWidget.IconLocation.LOCKED_HOVER;
				} else {
					iconLocation = LockButtonWidget.IconLocation.LOCKED;
				}
			} else if (!this.active) {
				iconLocation = LockButtonWidget.IconLocation.UNLOCKED_DISABLED;
			} else if (bl) {
				iconLocation = LockButtonWidget.IconLocation.UNLOCKED_HOVER;
			} else {
				iconLocation = LockButtonWidget.IconLocation.UNLOCKED;
			}

			this.drawTexture(this.x, this.y, iconLocation.getU(), iconLocation.getV(), this.width, this.height);
		}
	}

	static enum IconLocation {
		LOCKED(0, 146),
		LOCKED_HOVER(0, 166),
		LOCKED_DISABLED(0, 186),
		UNLOCKED(20, 146),
		UNLOCKED_HOVER(20, 166),
		UNLOCKED_DISABLED(20, 186);

		private final int u;
		private final int v;

		private IconLocation(int j, int k) {
			this.u = j;
			this.v = k;
		}

		public int getU() {
			return this.u;
		}

		public int getV() {
			return this.v;
		}
	}
}
