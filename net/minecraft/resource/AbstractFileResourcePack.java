package net.minecraft.resource;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractFileResourcePack implements ResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final File base;

	public AbstractFileResourcePack(File file) {
		this.base = file;
	}

	private static String getFilename(Identifier id) {
		return String.format("%s/%s/%s", "assets", id.getNamespace(), id.getPath());
	}

	protected static String relativize(File base, File target) {
		return base.toURI().relativize(target.toURI()).getPath();
	}

	@Override
	public InputStream open(Identifier id) throws IOException {
		return this.openFile(getFilename(id));
	}

	@Override
	public boolean contains(Identifier id) {
		return this.containsFile(getFilename(id));
	}

	protected abstract InputStream openFile(String name) throws IOException;

	protected abstract boolean containsFile(String name);

	protected void warnNonLowercaseNamespace(String namespace) {
		LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", new Object[]{namespace, this.base});
	}

	@Override
	public <T extends ResourceMetadataProvider> T parseMetadata(MetadataSerializer serializer, String key) throws IOException {
		return parseMetadata(serializer, this.openFile("pack.mcmeta"), key);
	}

	static <T extends ResourceMetadataProvider> T parseMetadata(MetadataSerializer serializer, InputStream inputStream, String key) {
		JsonObject jsonObject = null;
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
			jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
		} catch (RuntimeException var9) {
			throw new JsonParseException(var9);
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}

		return serializer.fromJson(key, jsonObject);
	}

	@Override
	public BufferedImage getIcon() throws IOException {
		return TextureUtil.create(this.openFile("pack.png"));
	}

	@Override
	public String getName() {
		return this.base.getName();
	}
}
