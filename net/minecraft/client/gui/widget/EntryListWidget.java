package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends ListWidget {
	private final List<E> field_20073 = new EntryListWidget.class_4120();

	public EntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	protected boolean method_18414(int i, int j, double d, double e) {
		return this.getEntry(i).mouseClicked(d, e, j);
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
		this.getEntry(i).method_6700(this.getRowWidth(), l, m, n, this.method_18415((double)m, (double)n) && this.method_18411((double)m, (double)n) == i, f);
	}

	@Override
	protected void method_9528(int i, int j, int k, float f) {
		this.getEntry(i).method_18401(f);
	}

	@Override
	public final List<E> method_18423() {
		return this.field_20073;
	}

	protected final void method_18399() {
		this.field_20073.clear();
	}

	private E getEntry(int index) {
		return (E)this.method_18423().get(index);
	}

	protected final void method_18398(E entry) {
		this.field_20073.add(entry);
	}

	@Override
	public void method_18416(int i) {
		this.selectedEntry = i;
		this.time = Util.method_20227();
	}

	@Override
	protected final int getEntryCount() {
		return this.method_18423().size();
	}

	public abstract static class Entry<E extends EntryListWidget.Entry<E>> implements class_4122 {
		protected EntryListWidget<E> field_20074;
		protected int field_20075;

		protected EntryListWidget<E> method_18400() {
			return this.field_20074;
		}

		protected int method_18402() {
			return this.field_20075;
		}

		protected int method_18403() {
			return this.field_20074.yStart + 4 - this.field_20074.getScrollAmount() + this.field_20075 * this.field_20074.entryHeight + this.field_20074.headerHeight;
		}

		protected int method_18404() {
			return this.field_20074.xStart + this.field_20074.width / 2 - this.field_20074.getRowWidth() / 2 + 2;
		}

		protected void method_18401(float f) {
		}

		public abstract void method_6700(int i, int j, int k, int l, boolean bl, float f);
	}

	class class_4120 extends AbstractList<E> {
		private final List<E> field_20077 = Lists.newArrayList();

		private class_4120() {
		}

		public E get(int i) {
			return (E)this.field_20077.get(i);
		}

		public int size() {
			return this.field_20077.size();
		}

		public E set(int i, E entry) {
			E entry2 = (E)this.field_20077.set(i, entry);
			entry.field_20074 = EntryListWidget.this;
			entry.field_20075 = i;
			return entry2;
		}

		public void add(int i, E entry) {
			this.field_20077.add(i, entry);
			entry.field_20074 = EntryListWidget.this;
			entry.field_20075 = i;
			int j = i + 1;

			while (j < this.size()) {
				this.get(j).field_20075 = j++;
			}
		}

		public E remove(int i) {
			E entry = (E)this.field_20077.remove(i);
			int j = i;

			while (j < this.size()) {
				this.get(j).field_20075 = j++;
			}

			return entry;
		}
	}
}
