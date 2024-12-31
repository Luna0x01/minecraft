package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class SwitchWidget extends ButtonWidget {
	private boolean value;
	private final String label;
	private final PagedEntryListWidget.Listener listener;

	public SwitchWidget(PagedEntryListWidget.Listener listener, int i, int j, int k, String string, boolean bl) {
		super(i, j, k, 150, 20, "");
		this.label = string;
		this.value = bl;
		this.message = this.getMessage();
		this.listener = listener;
	}

	private String getMessage() {
		return I18n.translate(this.label) + ": " + I18n.translate(this.value ? "gui.yes" : "gui.no");
	}

	public void setValue(boolean value) {
		this.value = value;
		this.message = this.getMessage();
		this.listener.setBooleanValue(this.id, value);
	}

	@Override
	public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
		if (super.isMouseOver(client, mouseX, mouseY)) {
			this.value = !this.value;
			this.message = this.getMessage();
			this.listener.setBooleanValue(this.id, this.value);
			return true;
		} else {
			return false;
		}
	}
}
