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
	protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
		this.list.renderEntry(index, x, y, rowHeight, mouseX, mouseY);
	}

	public int getWidth() {
		return super.width;
	}

	public int getLastMouseY() {
		return super.lastMouseY;
	}

	public int getLastMouseX() {
		return super.lastMouseX;
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
	protected void renderList(int x, int y, int mouseX, int mouseY) {
		int i = this.getEntryCount();

		for (int j = 0; j < i; j++) {
			int k = y + j * this.entryHeight + this.headerHeight;
			int l = this.entryHeight - 4;
			if (k > this.yEnd || k + l < this.yStart) {
				this.updateItemPosition(j, x, k);
			}

			if (this.renderSelection && this.isEntrySelected(j)) {
				this.renderSelected(this.width, k, l, Tezzelator.instance);
			}

			this.renderEntry(j, x, k, l, mouseX, mouseY);
		}
	}
}
