package net.minecraft.client.render.model;

import java.util.Arrays;
import net.minecraft.class_4233;
import net.minecraft.client.texture.Sprite;

public class TexturedBakedQuad extends BakedQuad {
	private final Sprite sprite;

	public TexturedBakedQuad(BakedQuad bakedQuad, Sprite sprite) {
		super(
			Arrays.copyOf(bakedQuad.getVertexData(), bakedQuad.getVertexData().length),
			bakedQuad.colorIndex,
			class_4233.method_19246(bakedQuad.getVertexData()),
			bakedQuad.method_12351()
		);
		this.sprite = sprite;
		this.method_12362();
	}

	private void method_12362() {
		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			this.vertexData[j + 4] = Float.floatToRawIntBits(this.sprite.getFrameU((double)this.field_13552.method_12490(Float.intBitsToFloat(this.vertexData[j + 4]))));
			this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(
				this.sprite.getFrameV((double)this.field_13552.method_12493(Float.intBitsToFloat(this.vertexData[j + 4 + 1])))
			);
		}
	}
}
