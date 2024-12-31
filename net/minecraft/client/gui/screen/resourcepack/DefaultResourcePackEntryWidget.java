package net.minecraft.client.gui.screen.resourcepack;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.resource.ResourcePackMetadata;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResourcePackEntryWidget extends ResourcePackWidget {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ResourcePack pack = this.client.getResourcePackLoader().defaultResourcePack;
	private final Identifier identifier;

	public DefaultResourcePackEntryWidget(ResourcePackScreen resourcePackScreen) {
		super(resourcePackScreen);

		NativeImageBackedTexture nativeImageBackedTexture;
		try {
			nativeImageBackedTexture = new NativeImageBackedTexture(this.pack.getIcon());
		} catch (IOException var4) {
			nativeImageBackedTexture = TextureUtil.MISSING_TEXTURE;
		}

		this.identifier = this.client.getTextureManager().registerDynamicTexture("texturepackicon", nativeImageBackedTexture);
	}

	@Override
	protected int getFormat() {
		return 1;
	}

	@Override
	protected String getDescription() {
		try {
			ResourcePackMetadata resourcePackMetadata = this.pack.parseMetadata(this.client.getResourcePackLoader().metadataSerializer, "pack");
			if (resourcePackMetadata != null) {
				return resourcePackMetadata.getDescription().asFormattedString();
			}
		} catch (JsonParseException var2) {
			LOGGER.error("Couldn't load metadata info", var2);
		} catch (IOException var3) {
			LOGGER.error("Couldn't load metadata info", var3);
		}

		return Formatting.RED + "Missing " + "pack.mcmeta" + " :(";
	}

	@Override
	protected boolean isNotSelected() {
		return false;
	}

	@Override
	protected boolean isSelected() {
		return false;
	}

	@Override
	protected boolean canSortUp() {
		return false;
	}

	@Override
	protected boolean canSortDown() {
		return false;
	}

	@Override
	protected String getName() {
		return "Default";
	}

	@Override
	protected void bindIcon() {
		this.client.getTextureManager().bindTexture(this.identifier);
	}

	@Override
	protected boolean isVisible() {
		return false;
	}
}
