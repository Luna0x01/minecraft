package net.minecraft.client.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Identifier field_6555;

	public ResourceTexture(Identifier identifier) {
		this.field_6555 = identifier;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		this.clearGlId();
		InputStream inputStream = null;

		try {
			Resource resource = manager.getResource(this.field_6555);
			inputStream = resource.getInputStream();
			BufferedImage bufferedImage = TextureUtil.create(inputStream);
			boolean bl = false;
			boolean bl2 = false;
			if (resource.hasMetadata()) {
				try {
					TextureResourceMetadata textureResourceMetadata = resource.getMetadata("texture");
					if (textureResourceMetadata != null) {
						bl = textureResourceMetadata.method_5980();
						bl2 = textureResourceMetadata.method_5981();
					}
				} catch (RuntimeException var11) {
					LOGGER.warn("Failed reading metadata of: " + this.field_6555, var11);
				}
			}

			TextureUtil.method_5860(this.getGlId(), bufferedImage, bl, bl2);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
