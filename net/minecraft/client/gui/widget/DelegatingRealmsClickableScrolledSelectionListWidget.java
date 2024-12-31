package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.input.Mouse;

public class DelegatingRealmsClickableScrolledSelectionListWidget extends ListWidget {
	private final RealmsClickableScrolledSelectionList list;

	public DelegatingRealmsClickableScrolledSelectionListWidget(
		RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, int i, int j, int k, int l, int m
	) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.list = realmsClickableScrolledSelectionList;
	}

	@Override
	protected int getEntryCount() {
		return this.list.getItemCount();
	}

	@Override
	protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
		this.list.selectItem(index, doubleClick, lastMouseX, lastMouseY);
	}

	@Override
	protected boolean isEntrySelected(int index) {
		return this.list.isSelectedItem(index);
	}

	@Override
	protected void renderBackground() {
		this.list.renderBackground();
	}

	@Override
	protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
		this.list.renderEntry(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
	}

	public int getLastMouseY() {
		return this.lastMouseY;
	}

	public int getLastMouseX() {
		return this.lastMouseX;
	}

	@Override
	protected int getMaxPosition() {
		return this.list.getMaxPosition();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.list.getScrollbarPosition();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		if (this.scrollSpeed > 0.0F && Mouse.getEventButtonState()) {
			this.list.customMouseEvent(this.yStart, this.yEnd, this.headerHeight, this.scrollAmount, this.entryHeight);
		}
	}

	public void renderSelected(int width, int height, int textHeight, Tezzelator tessellator) {
		this.list.renderSelected(width, height, textHeight, tessellator);
	}

	@Override
	protected void method_6704(int i, int j, int k, int l, float f) {
		int m = this.getEntryCount();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.entryHeight + this.headerHeight;
			int p = this.entryHeight - 4;
			if (o > this.yEnd || o + p < this.yStart) {
				this.method_9528(n, i, o, f);
			}

			if (this.renderSelection && this.isEntrySelected(n)) {
				this.renderSelected(this.width, o, p, Tezzelator.instance);
			}

			this.method_1055(n, i, o, p, k, l, f);
		}
	}
}
