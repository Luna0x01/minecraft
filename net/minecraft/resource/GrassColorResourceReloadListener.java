package net.minecraft.resource;

import java.io.IOException;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;

public class GrassColorResourceReloadListener implements ResourceReloadListener {
	private static final Identifier GRASS_COLOR_TEXTURE = new Identifier("textures/colormap/grass.png");

	@Override
	public void reload(ResourceManager resourceManager) {
		try {
			GrassColors.setColorMap(TextureUtil.toPixels(resourceManager, GRASS_COLOR_TEXTURE));
		} catch (IOException var3) {
		}
	}
}
