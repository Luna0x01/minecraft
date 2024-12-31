package net.minecraft.resource;

import java.io.InputStream;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.util.Identifier;

public interface Resource {
	Identifier getId();

	InputStream getInputStream();

	boolean hasMetadata();

	<T extends ResourceMetadataProvider> T getMetadata(String key);

	String getResourcePackName();
}
