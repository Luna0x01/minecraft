package net.minecraft.client.resource;

import com.google.gson.JsonDeserializer;

public interface MetadataSerializer<T extends ResourceMetadataProvider> extends JsonDeserializer<T> {
	String getName();
}
