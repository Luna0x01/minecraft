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
	protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
		this.getEntry(i).method_6700(i, j, k, this.getRowWidth(), l, m, n, this.isMouseInList(n) && this.getEntryAt(m, n) == i, f);
	}

	@Override
	protected void method_9528(int i, int j, int k, float f) {
		this.getEntry(i).method_9473(i, j, k, f);
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
		void method_9473(int i, int j, int k, float f);

		void method_6700(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

		boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y);

		void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y);
	}
}
