package net.minecraft.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface ResourcePack extends AutoCloseable {
	String METADATA_PATH_SUFFIX = ".mcmeta";
	String PACK_METADATA_NAME = "pack.mcmeta";

	@Nullable
	InputStream openRoot(String fileName) throws IOException;

	InputStream open(ResourceType type, Identifier id) throws IOException;

	Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter);

	boolean contains(ResourceType type, Identifier id);

	Set<String> getNamespaces(ResourceType type);

	@Nullable
	<T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException;

	String getName();

	void close();
}
