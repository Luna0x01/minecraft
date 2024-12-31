package net.minecraft.client;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackWidget;
import net.minecraft.client.resource.ResourcePackMetadata;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2846 extends ResourcePackWidget {
	private static final Logger field_13352 = LogManager.getLogger();
	private final ResourcePack field_13353;
	private final Identifier field_13354;

	public class_2846(ResourcePackScreen resourcePackScreen, ResourcePack resourcePack) {
		super(resourcePackScreen);
		this.field_13353 = resourcePack;

		NativeImageBackedTexture nativeImageBackedTexture;
		try {
			nativeImageBackedTexture = new NativeImageBackedTexture(resourcePack.getIcon());
		} catch (IOException var5) {
			nativeImageBackedTexture = TextureUtil.MISSING_TEXTURE;
		}

		this.field_13354 = this.client.getTextureManager().registerDynamicTexture("texturepackicon", nativeImageBackedTexture);
	}

	@Override
	protected int getFormat() {
		return 2;
	}

	@Override
	protected String getDescription() {
		try {
			ResourcePackMetadata resourcePackMetadata = this.field_13353.parseMetadata(this.client.getResourcePackLoader().metadataSerializer, "pack");
			if (resourcePackMetadata != null) {
				return resourcePackMetadata.getDescription().asFormattedString();
			}
		} catch (JsonParseException var2) {
			field_13352.error("Couldn't load metadata info", var2);
		} catch (IOException var3) {
			field_13352.error("Couldn't load metadata info", var3);
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
		return "Server";
	}

	@Override
	protected void bindIcon() {
		this.client.getTextureManager().bindTexture(this.field_13354);
	}

	@Override
	protected boolean isVisible() {
		return false;
	}

	@Override
	public boolean method_12199() {
		return true;
	}
}
