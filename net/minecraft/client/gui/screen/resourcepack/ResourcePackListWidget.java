package net.minecraft.client.gui.screen.resourcepack;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Formatting;

public abstract class ResourcePackListWidget extends EntryListWidget {
	protected final MinecraftClient clientInstance;
	protected final List<ResourcePackWidget> widgets;

	public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j, List<ResourcePackWidget> list) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.clientInstance = minecraftClient;
		this.widgets = list;
		this.centerListVertically = false;
		this.setHeader(true, (int)((float)minecraftClient.textRenderer.fontHeight * 1.5F));
	}

	@Override
	protected void renderHeader(int x, int y, Tessellator tessellator) {
		String string = Formatting.UNDERLINE + "" + Formatting.BOLD + this.getTitle();
		this.clientInstance
			.textRenderer
			.draw(string, x + this.width / 2 - this.clientInstance.textRenderer.getStringWidth(string) / 2, Math.min(this.yStart + 3, y), 16777215);
	}

	protected abstract String getTitle();

	public List<ResourcePackWidget> getWidgets() {
		return this.widgets;
	}

	@Override
	protected int getEntryCount() {
		return this.getWidgets().size();
	}

	public ResourcePackWidget getEntry(int i) {
		return (ResourcePackWidget)this.getWidgets().get(i);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.xEnd - 6;
	}
}
