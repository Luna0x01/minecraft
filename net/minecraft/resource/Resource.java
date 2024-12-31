package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface Resource extends Closeable {
	Identifier getId();

	InputStream getInputStream();

	boolean hasMetadata();

	@Nullable
	<T> T getMetadata(ResourceMetadataReader<T> metaReader);

	String getResourcePackName();
}
