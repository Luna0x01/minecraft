package net.minecraft.client.font;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class EmptyGlyphRenderer extends GlyphRenderer {
	public EmptyGlyphRenderer() {
		super(RenderLayer.getText(new Identifier("")), RenderLayer.getTextSeeThrough(new Identifier("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void draw(boolean italic, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {
	}
}
