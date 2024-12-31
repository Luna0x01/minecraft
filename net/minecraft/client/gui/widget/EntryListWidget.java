package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;

public abstract class EntryListWidget extends ListWidget {
	public EntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
	}

	@Override
	protected boolean isEntrySelected(int index) {
		return false;
	}

	@Override
	protected void renderBackground() {
	}

	@Override
	protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
		this.getEntry(index).render(index, x, y, this.getRowWidth(), rowHeight, mouseX, mouseY, this.getEntryAt(mouseX, mouseY) == index);
	}

	@Override
	protected void updateItemPosition(int index, int x, int y) {
		this.getEntry(index).updatePosition(index, x, y);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (this.isMouseInList(mouseY)) {
			int i = this.getEntryAt(mouseX, mouseY);
			if (i >= 0) {
				int j = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
				int k = this.yStart + 4 - this.getScrollAmount() + i * this.entryHeight + this.headerHeight;
				int l = mouseX - j;
				int m = mouseY - k;
				if (this.getEntry(i).mouseClicked(i, mouseX, mouseY, button, l, m)) {
					this.setDragging(false);
					return true;
				}
			}
		}

		return false;
	}

	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		for (int i = 0; i < this.getEntryCount(); i++) {
			int j = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
			int k = this.yStart + 4 - this.getScrollAmount() + i * this.entryHeight + this.headerHeight;
			int l = mouseX - j;
			int m = mouseY - k;
			this.getEntry(i).mouseReleased(i, mouseX, mouseY, button, l, m);
		}

		this.setDragging(true);
		return false;
	}

	public abstract EntryListWidget.Entry getEntry(int index);

	public interface Entry {
		void updatePosition(int index, int x, int y);

		void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered);

		boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y);

		void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y);
	}
}
