package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DelegatingRealmsButtonWidget;
import net.minecraft.util.Identifier;

public class RealmsButton {
	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	private DelegatingRealmsButtonWidget proxy;

	public RealmsButton(int i, int j, int k, String string) {
		this.proxy = new DelegatingRealmsButtonWidget(this, i, j, k, string);
	}

	public RealmsButton(int i, int j, int k, int l, int m, String string) {
		this.proxy = new DelegatingRealmsButtonWidget(this, i, j, k, string, l, m);
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

	public void render(int mouseX, int mouseY) {
		this.proxy.render(MinecraftClient.getInstance(), mouseX, mouseY);
	}

	public void clicked(int mouseX, int mouseY) {
	}

	public void released(int mouseX, int mouseY) {
	}

	public void blit(int x, int y, int u, int v, int width, int height) {
		this.proxy.drawTexture(x, y, u, v, width, height);
	}

	public void renderBg(int mouseX, int mouseY) {
	}

	public int getYImage(boolean isHovered) {
		return this.proxy.getDelegateYImage(isHovered);
	}
}
