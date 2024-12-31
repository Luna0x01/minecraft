package net.minecraft.client.resource;

import net.minecraft.text.Text;

public class ResourcePackMetadata implements ResourceMetadataProvider {
	private final Text description;
	private final int format;

	public ResourcePackMetadata(Text text, int i) {
		this.description = text;
		this.format = i;
	}

	public Text getDescription() {
		return this.description;
	}

	public int getPackFormat() {
		return this.format;
	}
}
