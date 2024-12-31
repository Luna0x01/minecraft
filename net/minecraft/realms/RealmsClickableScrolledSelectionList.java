package net.minecraft.realms;

import net.minecraft.class_4122;
import net.minecraft.client.gui.widget.DelegatingRealmsClickableScrolledSelectionListWidget;

public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
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

	protected void renderItem(int index, int x, int y, int rowHeight, Tezzelator tessellator, int mouseX, int mouseY) {
	}

	public void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
		this.renderItem(index, x, y, rowHeight, Tezzelator.instance, mouseX, mouseY);
	}

	public int getItemCount() {
		return 0;
	}

	public boolean selectItem(int i, int j, double d, double e) {
		return true;
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

	@Override
	public class_4122 getProxy() {
		return this.proxy;
	}

	public void scroll(int amount) {
		this.proxy.scroll(amount);
	}

	public int getScroll() {
		return this.proxy.getScrollAmount();
	}

	protected void renderList(int x, int y, int mouseX, int mouseY) {
	}

	public void itemClicked(int i, int j, double d, double e, int k) {
	}

	public void renderSelected(int width, int height, int textHeight, Tezzelator tessellator) {
	}

	public void setLeftPos(int x) {
		this.proxy.setXPos(x);
	}

	public int method_20307() {
		return this.proxy.method_18508();
	}

	public int method_20308() {
		return this.proxy.method_18509();
	}

	public int headerHeight() {
		return this.proxy.method_18510();
	}

	public double method_20309() {
		return this.proxy.method_18511();
	}

	public int itemHeight() {
		return this.proxy.method_18512();
	}

	public boolean isVisible() {
		return this.proxy.method_18417();
	}
}
