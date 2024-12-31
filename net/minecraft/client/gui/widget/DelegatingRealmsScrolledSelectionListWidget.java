package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class DelegatingRealmsScrolledSelectionListWidget extends ListWidget {
	private final RealmsScrolledSelectionList list;

	public DelegatingRealmsScrolledSelectionListWidget(RealmsScrolledSelectionList realmsScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.list = realmsScrolledSelectionList;
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
		this.list.renderItem(i, j, k, l, m, n);
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
	}
}
