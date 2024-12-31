package net.minecraft.client.gui.screen.resourcepack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Formatting;

public abstract class ResourcePackListWidget extends EntryListWidget<ResourcePackEntryWidget> {
	protected final MinecraftClient clientInstance;

	public ResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.clientInstance = minecraftClient;
		this.centerListVertically = false;
		this.setHeader(true, (int)((float)minecraftClient.textRenderer.fontHeight * 1.5F));
	}

	@Override
	protected void renderHeader(int x, int y, Tessellator tessellator) {
		String string = Formatting.UNDERLINE + "" + Formatting.BOLD + this.getTitle();
		this.clientInstance
			.textRenderer
			.method_18355(
				string, (float)(x + this.width / 2 - this.clientInstance.textRenderer.getStringWidth(string) / 2), (float)Math.min(this.yStart + 3, y), 16777215
			);
	}

	protected abstract String getTitle();

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.xEnd - 6;
	}

	public void method_18829(ResourcePackEntryWidget resourcePackEntryWidget) {
		super.method_18398(resourcePackEntryWidget);
	}
}
