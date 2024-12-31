package net.minecraft.client.util;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.TexturePosition;
import net.minecraft.util.math.Vec3d;

public class TexturedQuad {
	public TexturePosition[] positions;
	public int size;
	private boolean field_1507;

	public TexturedQuad(TexturePosition[] texturePositions) {
		this.positions = texturePositions;
		this.size = texturePositions.length;
	}

	public TexturedQuad(TexturePosition[] texturePositions, int i, int j, int k, int l, float f, float g) {
		this(texturePositions);
		float h = 0.0F / f;
		float m = 0.0F / g;
		texturePositions[0] = texturePositions[0].withUv((float)k / f - h, (float)j / g + m);
		texturePositions[1] = texturePositions[1].withUv((float)i / f + h, (float)j / g + m);
		texturePositions[2] = texturePositions[2].withUv((float)i / f + h, (float)l / g - m);
		texturePositions[3] = texturePositions[3].withUv((float)k / f - h, (float)l / g - m);
	}

	public void mirror() {
		TexturePosition[] texturePositions = new TexturePosition[this.positions.length];

		for (int i = 0; i < this.positions.length; i++) {
			texturePositions[i] = this.positions[this.positions.length - i - 1];
		}

		this.positions = texturePositions;
	}

	public void draw(BufferBuilder builder, float scale) {
		Vec3d vec3d = this.positions[1].position.reverseSubtract(this.positions[0].position);
		Vec3d vec3d2 = this.positions[1].position.reverseSubtract(this.positions[2].position);
		Vec3d vec3d3 = vec3d2.crossProduct(vec3d).normalize();
		float f = (float)vec3d3.x;
		float g = (float)vec3d3.y;
		float h = (float)vec3d3.z;
		if (this.field_1507) {
			f = -f;
			g = -g;
			h = -h;
		}

		builder.begin(7, VertexFormats.ENTITY);

		for (int i = 0; i < 4; i++) {
			TexturePosition texturePosition = this.positions[i];
			builder.vertex(texturePosition.position.x * (double)scale, texturePosition.position.y * (double)scale, texturePosition.position.z * (double)scale)
				.texture((double)texturePosition.u, (double)texturePosition.v)
				.normal(f, g, h)
				.next();
		}

		Tessellator.getInstance().draw();
	}
}
