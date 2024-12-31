package net.minecraft.realms;

import net.minecraft.client.render.Tessellator;

public class Tezzelator {
	public static Tessellator tessellator = Tessellator.getInstance();
	public static final Tezzelator instance = new Tezzelator();

	public void end() {
		tessellator.draw();
	}

	public Tezzelator vertex(double x, double y, double z) {
		tessellator.getBuffer().vertex(x, y, z);
		return this;
	}

	public void color(float red, float green, float blue, float alpha) {
		tessellator.getBuffer().color(red, green, blue, alpha);
	}

	public void tex2(short u, short v) {
		tessellator.getBuffer().texture2(u, v);
	}

	public void normal(float x, float y, float z) {
		tessellator.getBuffer().normal(x, y, z);
	}

	public void begin(int drawMode, RealmsVertexFormat format) {
		tessellator.getBuffer().begin(drawMode, format.getVertexFormat());
	}

	public void endVertex() {
		tessellator.getBuffer().next();
	}

	public void offset(double x, double y, double z) {
		tessellator.getBuffer().offset(x, y, z);
	}

	public RealmsBufferBuilder color(int red, int green, int blue, int alpha) {
		return new RealmsBufferBuilder(tessellator.getBuffer().color(red, green, blue, alpha));
	}

	public Tezzelator tex(double u, double v) {
		tessellator.getBuffer().texture(u, v);
		return this;
	}
}
