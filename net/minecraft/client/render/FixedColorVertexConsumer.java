package net.minecraft.client.render;

public abstract class FixedColorVertexConsumer implements VertexConsumer {
	protected boolean colorFixed = false;
	protected int fixedRed = 255;
	protected int fixedGreen = 255;
	protected int fixedBlue = 255;
	protected int fixedAlpha = 255;

	public void fixedColor(int red, int green, int blue, int alpha) {
		this.fixedRed = red;
		this.fixedGreen = green;
		this.fixedBlue = blue;
		this.fixedAlpha = alpha;
		this.colorFixed = true;
	}
}
