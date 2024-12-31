package net.minecraft.client.render.model;

import java.util.Arrays;
import net.minecraft.client.texture.Sprite;

public class TexturedBakedQuad extends BakedQuad {
	private final Sprite sprite;

	public TexturedBakedQuad(BakedQuad bakedQuad, Sprite sprite) {
		super(
			Arrays.copyOf(bakedQuad.getVertexData(), bakedQuad.getVertexData().length),
			bakedQuad.colorIndex,
			BakedQuadFactory.decodeDirection(bakedQuad.getVertexData())
		);
		this.sprite = sprite;
		this.method_10047();
	}

	private void method_10047() {
		for (int i = 0; i < 4; i++) {
			this.method_10046(i);
		}
	}

	private void method_10046(int i) {
		int j = 7 * i;
		float f = Float.intBitsToFloat(this.vertexData[j]);
		float g = Float.intBitsToFloat(this.vertexData[j + 1]);
		float h = Float.intBitsToFloat(this.vertexData[j + 2]);
		float k = 0.0F;
		float l = 0.0F;
		switch (this.direction) {
			case DOWN:
				k = f * 16.0F;
				l = (1.0F - h) * 16.0F;
				break;
			case UP:
				k = f * 16.0F;
				l = h * 16.0F;
				break;
			case NORTH:
				k = (1.0F - f) * 16.0F;
				l = (1.0F - g) * 16.0F;
				break;
			case SOUTH:
				k = f * 16.0F;
				l = (1.0F - g) * 16.0F;
				break;
			case WEST:
				k = h * 16.0F;
				l = (1.0F - g) * 16.0F;
				break;
			case EAST:
				k = (1.0F - h) * 16.0F;
				l = (1.0F - g) * 16.0F;
		}

		this.vertexData[j + 4] = Float.floatToRawIntBits(this.sprite.getFrameU((double)k));
		this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(this.sprite.getFrameV((double)l));
	}
}
