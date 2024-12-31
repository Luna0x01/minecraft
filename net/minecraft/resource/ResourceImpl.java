package net.minecraft.resource;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;
import org.apache.commons.io.IOUtils;

public class ResourceImpl implements Resource {
	private final Map<String, ResourceMetadataProvider> metaProviders = Maps.newHashMap();
	private final String packName;
	private final Identifier id;
	private final InputStream inputStream;
	private final InputStream metaInputStream;
	private final MetadataSerializer metaSerializer;
	private boolean readMetadata;
	private JsonObject metadata;

	public ResourceImpl(String string, Identifier identifier, InputStream inputStream, InputStream inputStream2, MetadataSerializer metadataSerializer) {
		this.packName = string;
		this.id = identifier;
		this.inputStream = inputStream;
		this.metaInputStream = inputStream2;
		this.metaSerializer = metadataSerializer;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	@Override
	public boolean hasMetadata() {
		return this.metaInputStream != null;
	}

	@Override
	public <T extends ResourceMetadataProvider> T getMetadata(String key) {
		if (!this.hasMetadata()) {
			return null;
		} else {
			if (this.metadata == null && !this.readMetadata) {
				this.readMetadata = true;
				BufferedReader bufferedReader = null;

				try {
					bufferedReader = new BufferedReader(new InputStreamReader(this.metaInputStream));
					this.metadata = new JsonParser().parse(bufferedReader).getAsJsonObject();
				} finally {
					IOUtils.closeQuietly(bufferedReader);
				}
			}

			T resourceMetadataProvider = (T)this.metaProviders.get(key);
			if (resourceMetadataProvider == null) {
				resourceMetadataProvider = this.metaSerializer.fromJson(key, this.metadata);
			}

			return resourceMetadataProvider;
		}
	}

	@Override
	public String getResourcePackName() {
		return this.packName;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof ResourceImpl)) {
			return false;
		} else {
			ResourceImpl resourceImpl = (ResourceImpl)o;
			if (this.id != null ? this.id.equals(resourceImpl.id) : resourceImpl.id == null) {
				return this.packName != null ? this.packName.equals(resourceImpl.packName) : resourceImpl.packName == null;
			} else {
				return false;
			}
		}
	}

	public int hashCode() {
		int i = this.packName != null ? this.packName.hashCode() : 0;
		return 31 * i + (this.id != null ? this.id.hashCode() : 0);
	}
}
