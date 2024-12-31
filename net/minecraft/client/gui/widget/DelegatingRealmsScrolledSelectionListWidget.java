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
	protected boolean method_18414(int i, int j, double d, double e) {
		return this.list.selectItem(i, j, d, e);
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

	@Override
	protected int getMaxPosition() {
		return this.list.getMaxPosition();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.list.getScrollbarPosition();
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.list.mouseScrolled(d) ? true : super.mouseScrolled(d);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.list.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.list.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.list.mouseDragged(d, e, i, f, g);
	}
}
