package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;

public class BannerBlockEntityModel extends EntityModel {
	public ModelPart banner;
	public ModelPart pillar;
	public ModelPart crossbar;

	public BannerBlockEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.banner = new ModelPart(this, 0, 0);
		this.banner.addCuboid(-10.0F, 0.0F, -2.0F, 20, 40, 1, 0.0F);
		this.pillar = new ModelPart(this, 44, 0);
		this.pillar.addCuboid(-1.0F, -30.0F, -1.0F, 2, 42, 2, 0.0F);
		this.crossbar = new ModelPart(this, 0, 42);
		this.crossbar.addCuboid(-10.0F, -32.0F, -1.0F, 20, 2, 2, 0.0F);
	}

	public void render() {
		this.banner.pivotY = -32.0F;
		this.banner.render(0.0625F);
		this.pillar.render(0.0625F);
		this.crossbar.render(0.0625F);
	}
}
