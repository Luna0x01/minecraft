package net.minecraft.realms;

import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

public abstract class RealmListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmListEntry> {
	@Override
	public abstract void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return false;
	}
}
