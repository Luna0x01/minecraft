package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class EnderDragonModel extends EntityModel {
	private ModelPart head;
	private ModelPart neck;
	private ModelPart jaw;
	private ModelPart body;
	private ModelPart rearLeg;
	private ModelPart frontLeg;
	private ModelPart rearLegTip;
	private ModelPart frontLegTip;
	private ModelPart rearFoot;
	private ModelPart frontFoot;
	private ModelPart wing;
	private ModelPart wingTip;
	private float tickDelta;

	public EnderDragonModel(float f) {
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
		this.neck = new ModelPart(this, "neck");
		this.neck.addCuboid("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
		this.neck.addCuboid("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
		this.body = new ModelPart(this, "body");
		this.body.setPivot(0.0F, 4.0F, 8.0F);
		this.body.addCuboid("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
		this.body.addCuboid("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
		this.body.addCuboid("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
		this.body.addCuboid("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
		this.wing = new ModelPart(this, "wing");
		this.wing.setPivot(-12.0F, 5.0F, 2.0F);
		this.wing.addCuboid("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
		this.wing.addCuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
		this.wingTip = new ModelPart(this, "wingtip");
		this.wingTip.setPivot(-56.0F, 0.0F, 0.0F);
		this.wingTip.addCuboid("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
		this.wingTip.addCuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
		this.wing.add(this.wingTip);
		this.frontLeg = new ModelPart(this, "frontleg");
		this.frontLeg.setPivot(-12.0F, 20.0F, 2.0F);
		this.frontLeg.addCuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
		this.frontLegTip = new ModelPart(this, "frontlegtip");
		this.frontLegTip.setPivot(0.0F, 20.0F, -1.0F);
		this.frontLegTip.addCuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
		this.frontLeg.add(this.frontLegTip);
		this.frontFoot = new ModelPart(this, "frontfoot");
		this.frontFoot.setPivot(0.0F, 23.0F, 0.0F);
		this.frontFoot.addCuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
		this.frontLegTip.add(this.frontFoot);
		this.rearLeg = new ModelPart(this, "rearleg");
		this.rearLeg.setPivot(-16.0F, 16.0F, 42.0F);
		this.rearLeg.addCuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
		this.rearLegTip = new ModelPart(this, "rearlegtip");
		this.rearLegTip.setPivot(0.0F, 32.0F, -4.0F);
		this.rearLegTip.addCuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
		this.rearLeg.add(this.rearLegTip);
		this.rearFoot = new ModelPart(this, "rearfoot");
		this.rearFoot.setPivot(0.0F, 31.0F, 4.0F);
		this.rearFoot.addCuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
		this.rearLegTip.add(this.rearFoot);
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		this.tickDelta = tickDelta;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		GlStateManager.pushMatrix();
		EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
		float f = enderDragonEntity.prevWingPosition + (enderDragonEntity.wingPosition - enderDragonEntity.prevWingPosition) * this.tickDelta;
		this.jaw.posX = (float)(Math.sin((double)(f * (float) (Math.PI * 2))) + 1.0) * 0.2F;
		float g = (float)(Math.sin((double)(f * (float) (Math.PI * 2) - 1.0F)) + 1.0);
		g = (g * g + g * 2.0F) * 0.05F;
		GlStateManager.translate(0.0F, g - 2.0F, -3.0F);
		GlStateManager.rotate(g * 2.0F, 1.0F, 0.0F, 0.0F);
		float h = -30.0F;
		float i = 0.0F;
		float j = 1.5F;
		double[] ds = enderDragonEntity.getSegmentProperties(6, this.tickDelta);
		float k = this.clampAngle(enderDragonEntity.getSegmentProperties(5, this.tickDelta)[0] - enderDragonEntity.getSegmentProperties(10, this.tickDelta)[0]);
		float l = this.clampAngle(enderDragonEntity.getSegmentProperties(5, this.tickDelta)[0] + (double)(k / 2.0F));
		h += 2.0F;
		float m = f * (float) (Math.PI * 2);
		h = 20.0F;
		float n = -12.0F;

		for (int o = 0; o < 5; o++) {
			double[] es = enderDragonEntity.getSegmentProperties(5 - o, this.tickDelta);
			float p = (float)Math.cos((double)((float)o * 0.45F + m)) * 0.15F;
			this.neck.posY = this.clampAngle(es[0] - ds[0]) * (float) (Math.PI / 180.0) * j;
			this.neck.posX = p + enderDragonEntity.method_13165(o, ds, es) * (float) (Math.PI / 180.0) * j * 5.0F;
			this.neck.posZ = -this.clampAngle(es[0] - (double)l) * (float) (Math.PI / 180.0) * j;
			this.neck.pivotY = h;
			this.neck.pivotZ = n;
			this.neck.pivotX = i;
			h = (float)((double)h + Math.sin((double)this.neck.posX) * 10.0);
			n = (float)((double)n - Math.cos((double)this.neck.posY) * Math.cos((double)this.neck.posX) * 10.0);
			i = (float)((double)i - Math.sin((double)this.neck.posY) * Math.cos((double)this.neck.posX) * 10.0);
			this.neck.render(scale);
		}

		this.head.pivotY = h;
		this.head.pivotZ = n;
		this.head.pivotX = i;
		double[] fs = enderDragonEntity.getSegmentProperties(0, this.tickDelta);
		this.head.posY = this.clampAngle(fs[0] - ds[0]) * (float) (Math.PI / 180.0);
		this.head.posX = this.clampAngle((double)enderDragonEntity.method_13165(6, ds, fs)) * (float) (Math.PI / 180.0) * j * 5.0F;
		this.head.posZ = -this.clampAngle(fs[0] - (double)l) * (float) (Math.PI / 180.0);
		this.head.render(scale);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-k * j, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0.0F, -1.0F, 0.0F);
		this.body.posZ = 0.0F;
		this.body.render(scale);

		for (int q = 0; q < 2; q++) {
			GlStateManager.enableCull();
			float r = f * (float) (Math.PI * 2);
			this.wing.posX = 0.125F - (float)Math.cos((double)r) * 0.2F;
			this.wing.posY = 0.25F;
			this.wing.posZ = (float)(Math.sin((double)r) + 0.125) * 0.8F;
			this.wingTip.posZ = -((float)(Math.sin((double)(r + 2.0F)) + 0.5)) * 0.75F;
			this.rearLeg.posX = 1.0F + g * 0.1F;
			this.rearLegTip.posX = 0.5F + g * 0.1F;
			this.rearFoot.posX = 0.75F + g * 0.1F;
			this.frontLeg.posX = 1.3F + g * 0.1F;
			this.frontLegTip.posX = -0.5F - g * 0.1F;
			this.frontFoot.posX = 0.75F + g * 0.1F;
			this.wing.render(scale);
			this.frontLeg.render(scale);
			this.rearLeg.render(scale);
			GlStateManager.scale(-1.0F, 1.0F, 1.0F);
			if (q == 0) {
				GlStateManager.method_12284(GlStateManager.class_2865.FRONT);
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.method_12284(GlStateManager.class_2865.BACK);
		GlStateManager.disableCull();
		float s = -((float)Math.sin((double)(f * (float) (Math.PI * 2)))) * 0.0F;
		m = f * (float) (Math.PI * 2);
		h = 10.0F;
		n = 60.0F;
		i = 0.0F;
		ds = enderDragonEntity.getSegmentProperties(11, this.tickDelta);

		for (int t = 0; t < 12; t++) {
			fs = enderDragonEntity.getSegmentProperties(12 + t, this.tickDelta);
			s = (float)((double)s + Math.sin((double)((float)t * 0.45F + m)) * 0.05F);
			this.neck.posY = (this.clampAngle(fs[0] - ds[0]) * j + 180.0F) * (float) (Math.PI / 180.0);
			this.neck.posX = s + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * j * 5.0F;
			this.neck.posZ = this.clampAngle(fs[0] - (double)l) * (float) (Math.PI / 180.0) * j;
			this.neck.pivotY = h;
			this.neck.pivotZ = n;
			this.neck.pivotX = i;
			h = (float)((double)h + Math.sin((double)this.neck.posX) * 10.0);
			n = (float)((double)n - Math.cos((double)this.neck.posY) * Math.cos((double)this.neck.posX) * 10.0);
			i = (float)((double)i - Math.sin((double)this.neck.posY) * Math.cos((double)this.neck.posX) * 10.0);
			this.neck.render(scale);
		}

		GlStateManager.popMatrix();
	}

	private float clampAngle(double angle) {
		while (angle >= 180.0) {
			angle -= 360.0;
		}

		while (angle < -180.0) {
			angle += 360.0;
		}

		return (float)angle;
	}
}
