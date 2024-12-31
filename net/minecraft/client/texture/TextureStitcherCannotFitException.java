package net.minecraft.client.texture;

import java.util.Collection;

public class TextureStitcherCannotFitException extends RuntimeException {
	private final Collection<Sprite.Info> sprites;

	public TextureStitcherCannotFitException(Sprite.Info info, Collection<Sprite.Info> collection) {
		super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", info.getId(), info.getWidth(), info.getHeight()));
		this.sprites = collection;
	}

	public Collection<Sprite.Info> getSprites() {
		return this.sprites;
	}
}
