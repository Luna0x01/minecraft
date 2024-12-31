package net.minecraft.client.resource;

public class FontMetadata implements ResourceMetadataProvider {
	private final float[] widths;
	private final float[] lefts;
	private final float[] spacings;

	public FontMetadata(float[] fs, float[] gs, float[] hs) {
		this.widths = fs;
		this.lefts = gs;
		this.spacings = hs;
	}
}
