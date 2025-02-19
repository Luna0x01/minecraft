package net.minecraft.client.realms;

import java.util.Collection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

public abstract class RealmsObjectSelectionList<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends AlwaysSelectedEntryListWidget<E> {
	protected RealmsObjectSelectionList(int width, int height, int top, int bottom, int itemHeight) {
		super(MinecraftClient.getInstance(), width, height, top, bottom, itemHeight);
	}

	public void setSelectedItem(int index) {
		if (index == -1) {
			this.setSelected(null);
		} else if (super.getEntryCount() != 0) {
			this.setSelected(this.getEntry(index));
		}
	}

	public void setSelected(int index) {
		this.setSelectedItem(index);
	}

	public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
	}

	@Override
	public int getMaxPosition() {
		return 0;
	}

	@Override
	public int getScrollbarPositionX() {
		return this.getRowLeft() + this.getRowWidth();
	}

	@Override
	public int getRowWidth() {
		return (int)((double)this.width * 0.6);
	}

	@Override
	public void replaceEntries(Collection<E> newEntries) {
		super.replaceEntries(newEntries);
	}

	@Override
	public int getEntryCount() {
		return super.getEntryCount();
	}

	@Override
	public int getRowTop(int index) {
		return super.getRowTop(index);
	}

	@Override
	public int getRowLeft() {
		return super.getRowLeft();
	}

	public int addEntry(E entry) {
		return super.addEntry(entry);
	}

	public void clear() {
		this.clearEntries();
	}
}
