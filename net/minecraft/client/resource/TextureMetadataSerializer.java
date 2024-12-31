package net.minecraft.client.resource;

import com.google.gson.JsonObject;
import net.minecraft.class_4457;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.util.JsonHelper;

public class TextureMetadataSerializer implements class_4457<TextureResourceMetadata> {
	public TextureResourceMetadata method_21335(JsonObject jsonObject) {
		boolean bl = JsonHelper.getBoolean(jsonObject, "blur", false);
		boolean bl2 = JsonHelper.getBoolean(jsonObject, "clamp", false);
		return new TextureResourceMetadata(bl, bl2);
	}

	@Override
	public String method_5956() {
		return "texture";
	}
}
