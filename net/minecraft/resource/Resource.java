package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.util.Identifier;

public interface Resource extends Closeable {
	Identifier getId();

	InputStream getInputStream();

	boolean hasMetadata();

	@Nullable
	<T extends ResourceMetadataProvider> T getMetadata(String key);

	String getResourcePackName();
}
