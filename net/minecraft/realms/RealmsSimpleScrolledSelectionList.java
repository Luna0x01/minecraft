package net.minecraft.realms;

import net.minecraft.class_4122;
import net.minecraft.client.gui.widget.DelegatingRealmsSimpleScrolledSelectionListWidget;

public abstract class RealmsSimpleScrolledSelectionList extends RealmsGuiEventListener {
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

	protected void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
	}

	public void renderItem(int i, int j, int k, int l, int m, int n) {
		this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
	}

	public int getItemCount() {
		return 0;
	}

	public boolean selectItem(int i, int j, double d, double e) {
		return true;
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

	protected void renderList(int i, int j, int k, int l) {
	}
}
