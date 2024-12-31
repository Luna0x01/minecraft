package net.minecraft.realms;

import net.minecraft.client.gui.widget.DelegatingRealmsSimpleScrolledSelectionListWidget;

public class RealmsSimpleScrolledSelectionList {
	private final DelegatingRealmsSimpleScrolledSelectionListWidget proxy;

	public RealmsSimpleScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new DelegatingRealmsSimpleScrolledSelectionListWidget(this, i, j, k, l, m);
	}

	public void render(int mouseX, int mouseY, float delta) {
		this.proxy.render(mouseX, mouseY, delta);
	}

	public int width() {
		return this.proxy.getWidth();
	}

	public int getLastMouseY() {
		return this.proxy.getLastMouseY();
	}

	public int getLastMouseX() {
		return this.proxy.getLastMouseX();
	}

	protected void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
	}

	public void renderItem(int i, int j, int k, int l, int m, int n) {
		this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
	}

	public int getItemCount() {
		return 0;
	}

	public void selectItem(int i, boolean bl, int j, int k) {
	}

	public boolean isSelectedItem(int i) {
		return false;
	}

	public void renderBackground() {
	}

	public int getMaxPosition() {
		return 0;
	}

	public int getScrollbarPosition() {
		return this.proxy.getWidth() / 2 + 124;
	}

	public void mouseEvent() {
		this.proxy.handleMouse();
	}

	public void scroll(int amount) {
		this.proxy.scroll(amount);
	}

	public int getScroll() {
		return this.proxy.getScrollAmount();
	}

	protected void renderList(int i, int j, int k, int l) {
	}
}
