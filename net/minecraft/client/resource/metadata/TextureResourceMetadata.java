package net.minecraft.client.resource.metadata;

import net.minecraft.client.resource.TextureMetadataSerializer;

public class TextureResourceMetadata {
	public static final TextureMetadataSerializer field_21050 = new TextureMetadataSerializer();
	private final boolean field_6674;
	private final boolean field_6675;

	public TextureResourceMetadata(boolean bl, boolean bl2) {
		this.field_6674 = bl;
		this.field_6675 = bl2;
	}

	public boolean method_5980() {
		return this.field_6674;
	}

	public boolean method_5981() {
		return this.field_6675;
	}
}
