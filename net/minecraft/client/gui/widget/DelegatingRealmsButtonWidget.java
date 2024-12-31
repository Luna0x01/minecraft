package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsButton;

public class DelegatingRealmsButtonWidget extends ButtonWidget {
	private final RealmsButton realmsButton;

	public DelegatingRealmsButtonWidget(RealmsButton realmsButton, int i, int j, int k, String string) {
		super(i, j, k, string);
		this.realmsButton = realmsButton;
	}

	public DelegatingRealmsButtonWidget(RealmsButton realmsButton, int i, int j, int k, String string, int l, int m) {
		super(i, j, k, l, m, string);
		this.realmsButton = realmsButton;
	}

	public int getId() {
		return this.id;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean value) {
		this.active = value;
	}

	public void setMessage(String message) {
		super.message = message;
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int getY() {
		return this.y;
	}

	@Override
	public void method_18374(double d, double e) {
		this.realmsButton.onClick(d, e);
	}

	@Override
	public void method_18376(double d, double e) {
		this.realmsButton.onRelease(d, e);
	}

	@Override
	public void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
		this.realmsButton.renderBg(mouseX, mouseY);
	}

	public RealmsButton getDelegate() {
		return this.realmsButton;
	}

	@Override
	public int getYImage(boolean isHovered) {
		return this.realmsButton.getYImage(isHovered);
	}

	public int getDelegateYImage(boolean isHovered) {
		return super.getYImage(isHovered);
	}

	public int getHeight() {
		return this.height;
	}
}
