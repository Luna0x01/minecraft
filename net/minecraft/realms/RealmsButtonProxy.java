package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class RealmsButtonProxy extends ButtonWidget implements RealmsAbstractButtonProxy<RealmsButton> {
	private final RealmsButton button;

	public RealmsButtonProxy(RealmsButton realmsButton, int i, int j, String string, int k, int l, ButtonWidget.PressAction pressAction) {
		super(i, j, k, l, string, pressAction);
		this.button = realmsButton;
	}

	@Override
	public boolean active() {
		return this.active;
	}

	@Override
	public void active(boolean bl) {
		this.active = bl;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	@Override
	public void setMessage(String string) {
		super.setMessage(string);
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int y() {
		return this.y;
	}

	@Override
	public void onClick(double d, double e) {
		this.button.onPress();
	}

	@Override
	public void onRelease(double d, double e) {
		this.button.onRelease(d, e);
	}

	@Override
	public void renderBg(MinecraftClient minecraftClient, int i, int j) {
		this.button.renderBg(i, j);
	}

	@Override
	public void renderButton(int i, int j, float f) {
		this.button.renderButton(i, j, f);
	}

	public void superRenderButton(int i, int j, float f) {
		super.renderButton(i, j, f);
	}

	public RealmsButton getButton() {
		return this.button;
	}

	@Override
	public int getYImage(boolean bl) {
		return this.button.getYImage(bl);
	}

	public int getSuperYImage(boolean bl) {
		return super.getYImage(bl);
	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public boolean isHovered() {
		return super.isHovered();
	}
}
