package net.minecraft.client.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.resource.ResourceManager;

public class NativeImageBackedTexture extends AbstractTexture {
	private final int[] pixels;
	private final int width;
	private final int height;

	public NativeImageBackedTexture(BufferedImage bufferedImage) {
		this(bufferedImage.getWidth(), bufferedImage.getHeight());
		bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.pixels, 0, bufferedImage.getWidth());
		this.upload();
	}

	public NativeImageBackedTexture(int i, int j) {
		this.width = i;
		this.height = j;
		this.pixels = new int[i * j];
		TextureUtil.prepareImage(this.getGlId(), i, j);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
	}

	public void upload() {
		TextureUtil.method_5861(this.getGlId(), this.pixels, this.width, this.height);
	}

	public int[] getPixels() {
		return this.pixels;
	}
}
