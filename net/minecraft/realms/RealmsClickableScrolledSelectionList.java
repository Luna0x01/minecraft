package net.minecraft.realms;

import net.minecraft.client.gui.widget.DelegatingRealmsClickableScrolledSelectionListWidget;

public class RealmsClickableScrolledSelectionList {
	private final DelegatingRealmsClickableScrolledSelectionListWidget proxy;

	public RealmsClickableScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new DelegatingRealmsClickableScrolledSelectionListWidget(this, i, j, k, l, m);
	}

	public void render(int mouseX, int mouseY, float tickDelta) {
		this.proxy.render(mouseX, mouseY, tickDelta);
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

	protected void renderItem(int index, int x, int y, int rowHeight, Tezzelator tessellator, int mouseX, int mouseY) {
	}

	public void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
		this.renderItem(index, x, y, rowHeight, Tezzelator.instance, mouseX, mouseY);
	}

	public int getItemCount() {
		return 0;
	}

	public void selectItem(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
	}

	public boolean isSelectedItem(int index) {
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

	public void customMouseEvent(int yStart, int yEnd, int headerHeight, float scrollAmount, int entryHeight) {
	}

	public void scroll(int amount) {
		this.proxy.scroll(amount);
	}

	public int getScroll() {
		return this.proxy.getScrollAmount();
	}

	protected void renderList(int x, int y, int mouseX, int mouseY) {
	}

	public void itemClicked(int mouseX, int mouseY, int button, int x, int y) {
	}

	public void renderSelected(int width, int height, int textHeight, Tezzelator tessellator) {
	}

	public void setLeftPos(int x) {
		this.proxy.setXPos(x);
	}
}
