package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends EntryListWidget<E> {
	public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	public boolean changeFocus(boolean bl) {
		boolean bl2 = super.changeFocus(bl);
		if (bl2) {
			this.ensureVisible(this.getFocused());
		}

		return bl2;
	}

	@Override
	protected boolean isSelectedItem(int i) {
		return false;
	}

	public abstract static class Entry<E extends ElementListWidget.Entry<E>> extends EntryListWidget.Entry<E> implements ParentElement {
		@Nullable
		private Element focused;
		private boolean dragging;

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean bl) {
			this.dragging = bl;
		}

		@Override
		public void setFocused(@Nullable Element element) {
			this.focused = element;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}
	}
}
