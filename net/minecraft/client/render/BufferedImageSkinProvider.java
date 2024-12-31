package net.minecraft.client.render;

import java.awt.image.BufferedImage;

public interface BufferedImageSkinProvider {
	BufferedImage parseSkin(BufferedImage image);

	void setAvailable();
}
