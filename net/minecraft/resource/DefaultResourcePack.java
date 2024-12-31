package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resource.AssetsIndex;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;

public class DefaultResourcePack implements ResourcePack {
	public static final Set<String> NAMESPACES = ImmutableSet.of("minecraft", "realms");
	private final AssetsIndex assetsIndex;

	public DefaultResourcePack(AssetsIndex assetsIndex) {
		this.assetsIndex = assetsIndex;
	}

	@Override
	public InputStream open(Identifier id) throws IOException {
		InputStream inputStream = this.openClassLoader(id);
		if (inputStream != null) {
			return inputStream;
		} else {
			InputStream inputStream2 = this.openFile(id);
			if (inputStream2 != null) {
				return inputStream2;
			} else {
				throw new FileNotFoundException(id.getPath());
			}
		}
	}

	@Nullable
	public InputStream openFile(Identifier id) throws FileNotFoundException {
		File file = this.assetsIndex.method_12496(id);
		return file != null && file.isFile() ? new FileInputStream(file) : null;
	}

	private InputStream openClassLoader(Identifier id) {
		return DefaultResourcePack.class.getResourceAsStream("/assets/" + id.getNamespace() + "/" + id.getPath());
	}

	@Override
	public boolean contains(Identifier id) {
		return this.openClassLoader(id) != null || this.assetsIndex.method_12497(id);
	}

	@Override
	public Set<String> getNamespaces() {
		return NAMESPACES;
	}

	@Override
	public <T extends ResourceMetadataProvider> T parseMetadata(MetadataSerializer serializer, String key) throws IOException {
		try {
			InputStream inputStream = new FileInputStream(this.assetsIndex.method_12495());
			return AbstractFileResourcePack.parseMetadata(serializer, inputStream, key);
		} catch (RuntimeException var4) {
			return null;
		} catch (FileNotFoundException var5) {
			return null;
		}
	}

	@Override
	public BufferedImage getIcon() throws IOException {
		return TextureUtil.create(DefaultResourcePack.class.getResourceAsStream("/" + new Identifier("pack.png").getPath()));
	}

	@Override
	public String getName() {
		return "Default";
	}
}
