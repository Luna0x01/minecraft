package net.minecraft.client.gui.screen.resourcepack;

import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.resource.ResourcePackLoader;

public class ResourcePackEntryWidget extends ResourcePackWidget {
	private final ResourcePackLoader.Entry entry;

	public ResourcePackEntryWidget(ResourcePackScreen resourcePackScreen, ResourcePackLoader.Entry entry) {
		super(resourcePackScreen);
		this.entry = entry;
	}

	@Override
	protected void bindIcon() {
		this.entry.bindIcon(this.client.getTextureManager());
	}

	@Override
	protected int getFormat() {
		return this.entry.getFormat();
	}

	@Override
	protected String getDescription() {
		return this.entry.getDescription();
	}

	@Override
	protected String getName() {
		return this.entry.getName();
	}

	public ResourcePackLoader.Entry getEntry() {
		return this.entry;
	}
}
