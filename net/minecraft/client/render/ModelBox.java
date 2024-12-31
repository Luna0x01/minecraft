package net.minecraft.client.render;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.util.TexturedQuad;
import net.minecraft.client.util.math.TexturePosition;

public class ModelBox {
	private final TexturePosition[] positions;
	private final TexturedQuad[] quads;
	public final float minX;
	public final float minY;
	public final float minZ;
	public final float maxX;
	public final float maxY;
	public final float maxZ;
	public String name;

	public ModelBox(ModelPart modelPart, int i, int j, float f, float g, float h, int k, int l, int m, float n) {
		this(modelPart, i, j, f, g, h, k, l, m, n, modelPart.mirror);
	}

	public ModelBox(ModelPart modelPart, int i, int j, float f, float g, float h, int k, int l, int m, float n, boolean bl) {
		this.minX = f;
		this.minY = g;
		this.minZ = h;
		this.maxX = f + (float)k;
		this.maxY = g + (float)l;
		this.maxZ = h + (float)m;
		this.positions = new TexturePosition[8];
		this.quads = new TexturedQuad[6];
		float o = f + (float)k;
		float p = g + (float)l;
		float q = h + (float)m;
		f -= n;
		g -= n;
		h -= n;
		o += n;
		p += n;
		q += n;
		if (bl) {
			float r = o;
			o = f;
			f = r;
		}

		TexturePosition texturePosition = new TexturePosition(f, g, h, 0.0F, 0.0F);
		TexturePosition texturePosition2 = new TexturePosition(o, g, h, 0.0F, 8.0F);
		TexturePosition texturePosition3 = new TexturePosition(o, p, h, 8.0F, 8.0F);
		TexturePosition texturePosition4 = new TexturePosition(f, p, h, 8.0F, 0.0F);
		TexturePosition texturePosition5 = new TexturePosition(f, g, q, 0.0F, 0.0F);
		TexturePosition texturePosition6 = new TexturePosition(o, g, q, 0.0F, 8.0F);
		TexturePosition texturePosition7 = new TexturePosition(o, p, q, 8.0F, 8.0F);
		TexturePosition texturePosition8 = new TexturePosition(f, p, q, 8.0F, 0.0F);
		this.positions[0] = texturePosition;
		this.positions[1] = texturePosition2;
		this.positions[2] = texturePosition3;
		this.positions[3] = texturePosition4;
		this.positions[4] = texturePosition5;
		this.positions[5] = texturePosition6;
		this.positions[6] = texturePosition7;
		this.positions[7] = texturePosition8;
		this.quads[0] = new TexturedQuad(
			new TexturePosition[]{texturePosition6, texturePosition2, texturePosition3, texturePosition7},
			i + m + k,
			j + m,
			i + m + k + m,
			j + m + l,
			modelPart.textureWidth,
			modelPart.textureHeight
		);
		this.quads[1] = new TexturedQuad(
			new TexturePosition[]{texturePosition, texturePosition5, texturePosition8, texturePosition4},
			i,
			j + m,
			i + m,
			j + m + l,
			modelPart.textureWidth,
			modelPart.textureHeight
		);
		this.quads[2] = new TexturedQuad(
			new TexturePosition[]{texturePosition6, texturePosition5, texturePosition, texturePosition2},
			i + m,
			j,
			i + m + k,
			j + m,
			modelPart.textureWidth,
			modelPart.textureHeight
		);
		this.quads[3] = new TexturedQuad(
			new TexturePosition[]{texturePosition3, texturePosition4, texturePosition8, texturePosition7},
			i + m + k,
			j + m,
			i + m + k + k,
			j,
			modelPart.textureWidth,
			modelPart.textureHeight
		);
		this.quads[4] = new TexturedQuad(
			new TexturePosition[]{texturePosition2, texturePosition, texturePosition4, texturePosition3},
			i + m,
			j + m,
			i + m + k,
			j + m + l,
			modelPart.textureWidth,
			modelPart.textureHeight
		);
		this.quads[5] = new TexturedQuad(
			new TexturePosition[]{texturePosition5, texturePosition6, texturePosition7, texturePosition8},
			i + m + k + m,
			j + m,
			i + m + k + m + k,
			j + m + l,
			modelPart.textureWidth,
			modelPart.textureHeight
		);
		if (bl) {
			for (TexturedQuad texturedQuad : this.quads) {
				texturedQuad.mirror();
			}
		}
	}

	public void draw(BufferBuilder builder, float scale) {
		for (TexturedQuad texturedQuad : this.quads) {
			texturedQuad.draw(builder, scale);
		}
	}

	public ModelBox setName(String name) {
		this.name = name;
		return this;
	}
}
