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
		return super.id;
	}

	public boolean isActive() {
		return super.active;
	}

	public void setActive(boolean value) {
		super.active = value;
	}

	public void setMessage(String message) {
		super.message = message;
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	public int getY() {
		return super.y;
	}

	@Override
	public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
		if (super.isMouseOver(client, mouseX, mouseY)) {
			this.realmsButton.clicked(mouseX, mouseY);
		}

		return super.isMouseOver(client, mouseX, mouseY);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.realmsButton.released(mouseX, mouseY);
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
