package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements ResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final List<ResourcePack> resourcePacks = Lists.newArrayList();
	private final net.minecraft.util.MetadataSerializer serializer;

	public FallbackResourceManager(net.minecraft.util.MetadataSerializer metadataSerializer) {
		this.serializer = metadataSerializer;
	}

	public void addResourcePack(ResourcePack pack) {
		this.resourcePacks.add(pack);
	}

	@Override
	public Set<String> getAllNamespaces() {
		return null;
	}

	@Override
	public Resource getResource(Identifier id) throws IOException {
		this.method_12498(id);
		ResourcePack resourcePack = null;
		Identifier identifier = method_5883(id);

		for (int i = this.resourcePacks.size() - 1; i >= 0; i--) {
			ResourcePack resourcePack2 = (ResourcePack)this.resourcePacks.get(i);
			if (resourcePack == null && resourcePack2.contains(identifier)) {
				resourcePack = resourcePack2;
			}

			if (resourcePack2.contains(id)) {
				InputStream inputStream = null;
				if (resourcePack != null) {
					inputStream = this.method_10362(identifier, resourcePack);
				}

				return new ResourceImpl(resourcePack2.getName(), id, this.method_10362(id, resourcePack2), inputStream, this.serializer);
			}
		}

		throw new FileNotFoundException(id.toString());
	}

	protected InputStream method_10362(Identifier id, ResourcePack pack) throws IOException {
		InputStream inputStream = pack.open(id);
		return (InputStream)(LOGGER.isDebugEnabled() ? new FallbackResourceManager.LeakedResourceStream(inputStream, id, pack.getName()) : inputStream);
	}

	private void method_12498(Identifier identifier) throws IOException {
		if (identifier.getPath().contains("..")) {
			throw new IOException("Invalid relative path to resource: " + identifier);
		}
	}

	@Override
	public List<Resource> getAllResources(Identifier id) throws IOException {
		this.method_12498(id);
		List<Resource> list = Lists.newArrayList();
		Identifier identifier = method_5883(id);

		for (ResourcePack resourcePack : this.resourcePacks) {
			if (resourcePack.contains(id)) {
				InputStream inputStream = resourcePack.contains(identifier) ? this.method_10362(identifier, resourcePack) : null;
				list.add(new ResourceImpl(resourcePack.getName(), id, this.method_10362(id, resourcePack), inputStream, this.serializer));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(id.toString());
		} else {
			return list;
		}
	}

	static Identifier method_5883(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), identifier.getPath() + ".mcmeta");
	}

	static class LeakedResourceStream extends InputStream {
		private final InputStream stream;
		private final String field_11260;
		private boolean field_11261;

		public LeakedResourceStream(InputStream inputStream, Identifier identifier, String string) {
			this.stream = inputStream;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
			this.field_11260 = "Leaked resource: '" + identifier + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream;
		}

		public void close() throws IOException {
			this.stream.close();
			this.field_11261 = true;
		}

		protected void finalize() throws Throwable {
			if (!this.field_11261) {
				FallbackResourceManager.LOGGER.warn(this.field_11260);
			}

			super.finalize();
		}

		public int read() throws IOException {
			return this.stream.read();
		}
	}
}
