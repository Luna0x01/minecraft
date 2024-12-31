package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;

public abstract class AbstractTexture implements Texture {
	protected int glId = -1;
	protected boolean bilinear;
	protected boolean mipmap;
	protected boolean lastBilinear;
	protected boolean lastMipmap;

	public void setFilter(boolean bl, boolean bl2) {
		this.bilinear = bl;
		this.mipmap = bl2;
		int i;
		int j;
		if (bl) {
			i = bl2 ? 9987 : 9729;
			j = 9729;
		} else {
			i = bl2 ? 9986 : 9728;
			j = 9728;
		}

		GlStateManager.method_12294(3553, 10241, i);
		GlStateManager.method_12294(3553, 10240, j);
	}

	@Override
	public void pushFilter(boolean bilinear, boolean mipmap) {
		this.lastBilinear = this.bilinear;
		this.lastMipmap = this.mipmap;
		this.setFilter(bilinear, mipmap);
	}

	@Override
	public void pop() {
		this.setFilter(this.lastBilinear, this.lastMipmap);
	}

	@Override
	public int getGlId() {
		if (this.glId == -1) {
			this.glId = TextureUtil.getTexLevelParameter();
		}

		return this.glId;
	}

	public void clearGlId() {
		if (this.glId != -1) {
			TextureUtil.deleteTexture(this.glId);
			this.glId = -1;
		}
	}
}
