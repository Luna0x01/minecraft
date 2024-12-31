package net.minecraft.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;

public interface ResourcePack {
	InputStream open(Identifier id) throws IOException;

	boolean contains(Identifier id);

	Set<String> getNamespaces();

	@Nullable
	<T extends ResourceMetadataProvider> T parseMetadata(MetadataSerializer serializer, String key) throws IOException;

	BufferedImage getIcon() throws IOException;

	String getName();
}
