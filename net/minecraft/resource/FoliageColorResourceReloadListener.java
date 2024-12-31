package net.minecraft.resource;

import java.io.IOException;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;

public class FoliageColorResourceReloadListener implements ResourceReloadListener {
	private static final Identifier FOLIAGE_TEXTURE = new Identifier("textures/colormap/foliage.png");

	@Override
	public void reload(ResourceManager resourceManager) {
		try {
			FoliageColors.setColorMap(TextureUtil.method_19534(resourceManager, FOLIAGE_TEXTURE));
		} catch (IOException var3) {
		}
	}
}
