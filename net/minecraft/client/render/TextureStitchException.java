package net.minecraft.client.render;

public class TextureStitchException extends RuntimeException {
	private final TextureStitcher.Holder holder;

	public TextureStitchException(TextureStitcher.Holder holder, String string) {
		super(string);
		this.holder = holder;
	}
}
