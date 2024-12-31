package net.minecraft.realms;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DelegatingRealmsButtonWidget;
import net.minecraft.util.Identifier;

public abstract class RealmsButton {
	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private final DelegatingRealmsButtonWidget proxy;

	public RealmsButton(int i, int j, int k, String string) {
		this.proxy = new DelegatingRealmsButtonWidget(this, i, j, k, string) {
			@Override
			public void method_18374(double d, double e) {
				RealmsButton.this.onClick(d, e);
			}
		};
	}

	public RealmsButton(int i, int j, int k, int l, int m, String string) {
		this.proxy = new DelegatingRealmsButtonWidget(this, i, j, k, string, l, m) {
			@Override
			public void method_18374(double d, double e) {
				RealmsButton.this.onClick(d, e);
			}
		};
	}

	public ButtonWidget getProxy() {
		return this.proxy;
	}

	public int getId() {
		return this.proxy.getId();
	}

	public boolean active() {
		return this.proxy.isActive();
	}

	public void active(boolean active) {
		this.proxy.setActive(active);
	}

	public void msg(String message) {
		this.proxy.setMessage(message);
	}

	public int getWidth() {
		return this.proxy.getWidth();
	}

	public int getHeight() {
		return this.proxy.getHeight();
	}

	public int getY() {
		return this.proxy.getY();
	}

	public void render(int i, int j, float f) {
		this.proxy.method_891(i, j, f);
	}

	public void blit(int x, int y, int u, int v, int width, int height) {
		this.proxy.drawTexture(x, y, u, v, width, height);
	}

	public void renderBg(int mouseX, int mouseY) {
	}

	public int getYImage(boolean isHovered) {
		return this.proxy.getDelegateYImage(isHovered);
	}

	public abstract void onClick(double d, double e);

	public void onRelease(double d, double e) {
	}
}
