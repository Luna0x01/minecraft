package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

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
		this.list.renderEntry(i, j, k, l, m, n);
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
		return this.list.mouseClicked(d, e, i) ? true : method_18507(this, d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.list.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.list.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
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

	public int method_18508() {
		return this.yStart;
	}

	public int method_18509() {
		return this.yEnd;
	}

	public int method_18510() {
		return this.headerHeight;
	}

	public double method_18511() {
		return this.field_20083;
	}

	public int method_18512() {
		return this.entryHeight;
	}
}
