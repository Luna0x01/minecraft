package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class SkullBlockModel extends EntityModel {
	private ModelPart head;
	private ModelPart jaw;

	public SkullBlockModel(float f) {
		this.textureWidth = 256;
		this.textureHeight = 256;
		this.putTexture("body.body", 0, 0);
		this.putTexture("wing.skin", -56, 88);
		this.putTexture("wingtip.skin", -56, 144);
		this.putTexture("rearleg.main", 0, 0);
		this.putTexture("rearfoot.main", 112, 0);
		this.putTexture("rearlegtip.main", 196, 0);
		this.putTexture("head.upperhead", 112, 30);
		this.putTexture("wing.bone", 112, 88);
		this.putTexture("head.upperlip", 176, 44);
		this.putTexture("jaw.jaw", 176, 65);
		this.putTexture("frontleg.main", 112, 104);
		this.putTexture("wingtip.bone", 112, 136);
		this.putTexture("frontfoot.main", 144, 104);
		this.putTexture("neck.box", 192, 104);
		this.putTexture("frontlegtip.main", 226, 138);
		this.putTexture("body.scale", 220, 53);
		this.putTexture("head.scale", 0, 0);
		this.putTexture("neck.scale", 48, 0);
		this.putTexture("head.nostril", 112, 0);
		float g = -16.0F;
		this.head = new ModelPart(this, "head");
		this.head.addCuboid("upperlip", -6.0F, -1.0F, -8.0F + g, 12, 5, 16);
		this.head.addCuboid("upperhead", -8.0F, -8.0F, 6.0F + g, 16, 16, 16);
		this.head.mirror = true;
		this.head.addCuboid("scale", -5.0F, -12.0F, 12.0F + g, 2, 4, 6);
		this.head.addCuboid("nostril", -5.0F, -3.0F, -6.0F + g, 2, 2, 4);
		this.head.mirror = false;
		this.head.addCuboid("scale", 3.0F, -12.0F, 12.0F + g, 2, 4, 6);
		this.head.addCuboid("nostril", 3.0F, -3.0F, -6.0F + g, 2, 2, 4);
		this.jaw = new ModelPart(this, "jaw");
		this.jaw.setPivot(0.0F, 4.0F, 8.0F + g);
		this.jaw.addCuboid("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
		this.head.add(this.jaw);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.jaw.posX = (float)(Math.sin((double)(handSwing * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.head.posY = age * (float) (Math.PI / 180.0);
		this.head.posX = headPitch * (float) (Math.PI / 180.0);
		GlStateManager.translate(0.0F, -0.374375F, 0.0F);
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		this.head.render(scale);
	}
}
