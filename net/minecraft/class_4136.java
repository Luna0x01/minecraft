package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class class_4136 {
	private final Identifier field_20124;
	private final float field_20125;
	private final float field_20126;
	private final float field_20127;
	private final float field_20128;
	private final float field_20129;
	private final float field_20130;
	private final float field_20131;
	private final float field_20132;

	public class_4136(Identifier identifier, float f, float g, float h, float i, float j, float k, float l, float m) {
		this.field_20124 = identifier;
		this.field_20125 = f;
		this.field_20126 = g;
		this.field_20127 = h;
		this.field_20128 = i;
		this.field_20129 = j;
		this.field_20130 = k;
		this.field_20131 = l;
		this.field_20132 = m;
	}

	public void method_18482(TextureManager textureManager, boolean bl, float f, float g, BufferBuilder bufferBuilder, float h, float i, float j, float k) {
		int l = 3;
		float m = f + this.field_20129;
		float n = f + this.field_20130;
		float o = this.field_20131 - 3.0F;
		float p = this.field_20132 - 3.0F;
		float q = g + o;
		float r = g + p;
		float s = bl ? 1.0F - 0.25F * o : 0.0F;
		float t = bl ? 1.0F - 0.25F * p : 0.0F;
		bufferBuilder.vertex((double)(m + s), (double)q, 0.0).texture((double)this.field_20125, (double)this.field_20127).color(h, i, j, k).next();
		bufferBuilder.vertex((double)(m + t), (double)r, 0.0).texture((double)this.field_20125, (double)this.field_20128).color(h, i, j, k).next();
		bufferBuilder.vertex((double)(n + t), (double)r, 0.0).texture((double)this.field_20126, (double)this.field_20128).color(h, i, j, k).next();
		bufferBuilder.vertex((double)(n + s), (double)q, 0.0).texture((double)this.field_20126, (double)this.field_20127).color(h, i, j, k).next();
	}

	@Nullable
	public Identifier method_18481() {
		return this.field_20124;
	}
}
