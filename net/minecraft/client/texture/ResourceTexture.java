package net.minecraft.client.texture;

import java.io.IOException;
import net.minecraft.class_4277;
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
		Resource resource = manager.getResource(this.field_6555);
		Throwable var3 = null;

		try (class_4277 lv = class_4277.method_19472(resource.getInputStream())) {
			boolean bl = false;
			boolean bl2 = false;
			if (resource.hasMetadata()) {
				try {
					TextureResourceMetadata textureResourceMetadata = resource.method_21371(TextureResourceMetadata.field_21050);
					if (textureResourceMetadata != null) {
						bl = textureResourceMetadata.method_5980();
						bl2 = textureResourceMetadata.method_5981();
					}
				} catch (RuntimeException var32) {
					LOGGER.warn("Failed reading metadata of: {}", this.field_6555, var32);
				}
			}

			this.method_19530();
			TextureUtil.prepareImage(this.getGlId(), 0, lv.method_19458(), lv.method_19478());
			lv.method_19463(0, 0, 0, 0, 0, lv.method_19458(), lv.method_19478(), bl, bl2, false);
		} catch (Throwable var35) {
			var3 = var35;
			throw var35;
		} finally {
			if (resource != null) {
				if (var3 != null) {
					try {
						resource.close();
					} catch (Throwable var30) {
						var3.addSuppressed(var30);
					}
				} else {
					resource.close();
				}
			}
		}
	}
}
