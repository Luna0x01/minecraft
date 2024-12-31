package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3135;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class HorseBaseEntityModel extends EntityModel {
	private final ModelPart field_6415;
	private final ModelPart field_6416;
	private final ModelPart field_6417;
	private final ModelPart field_6418;
	private final ModelPart field_6419;
	private final ModelPart field_6420;
	private final ModelPart field_6421;
	private final ModelPart field_6422;
	private final ModelPart field_6423;
	private final ModelPart field_6424;
	private final ModelPart torso;
	private final ModelPart head;
	private final ModelPart field_6427;
	private final ModelPart field_6428;
	private final ModelPart field_6429;
	private final ModelPart field_6430;
	private final ModelPart field_6431;
	private final ModelPart field_6432;
	private final ModelPart field_6433;
	private final ModelPart field_6434;
	private final ModelPart field_6396;
	private final ModelPart field_6397;
	private final ModelPart field_6398;
	private final ModelPart field_6399;
	private final ModelPart field_6400;
	private final ModelPart field_6401;
	private final ModelPart field_6402;
	private final ModelPart field_6403;
	private final ModelPart field_6404;
	private final ModelPart field_6405;
	private final ModelPart field_6406;
	private final ModelPart field_6407;
	private final ModelPart field_6408;
	private final ModelPart field_6409;
	private final ModelPart field_6410;
	private final ModelPart field_6411;
	private final ModelPart field_6412;
	private final ModelPart field_6413;
	private final ModelPart field_6414;

	public HorseBaseEntityModel() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.torso = new ModelPart(this, 0, 34);
		this.torso.addCuboid(-5.0F, -8.0F, -19.0F, 10, 10, 24);
		this.torso.setPivot(0.0F, 11.0F, 9.0F);
		this.head = new ModelPart(this, 44, 0);
		this.head.addCuboid(-1.0F, -1.0F, 0.0F, 2, 2, 3);
		this.head.setPivot(0.0F, 3.0F, 14.0F);
		this.head.posX = -1.134464F;
		this.field_6427 = new ModelPart(this, 38, 7);
		this.field_6427.addCuboid(-1.5F, -2.0F, 3.0F, 3, 4, 7);
		this.field_6427.setPivot(0.0F, 3.0F, 14.0F);
		this.field_6427.posX = -1.134464F;
		this.field_6428 = new ModelPart(this, 24, 3);
		this.field_6428.addCuboid(-1.5F, -4.5F, 9.0F, 3, 4, 7);
		this.field_6428.setPivot(0.0F, 3.0F, 14.0F);
		this.field_6428.posX = (float) (-Math.PI * 4.0 / 9.0);
		this.field_6429 = new ModelPart(this, 78, 29);
		this.field_6429.addCuboid(-2.5F, -2.0F, -2.5F, 4, 9, 5);
		this.field_6429.setPivot(4.0F, 9.0F, 11.0F);
		this.field_6430 = new ModelPart(this, 78, 43);
		this.field_6430.addCuboid(-2.0F, 0.0F, -1.5F, 3, 5, 3);
		this.field_6430.setPivot(4.0F, 16.0F, 11.0F);
		this.field_6431 = new ModelPart(this, 78, 51);
		this.field_6431.addCuboid(-2.5F, 5.1F, -2.0F, 4, 3, 4);
		this.field_6431.setPivot(4.0F, 16.0F, 11.0F);
		this.field_6432 = new ModelPart(this, 96, 29);
		this.field_6432.addCuboid(-1.5F, -2.0F, -2.5F, 4, 9, 5);
		this.field_6432.setPivot(-4.0F, 9.0F, 11.0F);
		this.field_6433 = new ModelPart(this, 96, 43);
		this.field_6433.addCuboid(-1.0F, 0.0F, -1.5F, 3, 5, 3);
		this.field_6433.setPivot(-4.0F, 16.0F, 11.0F);
		this.field_6434 = new ModelPart(this, 96, 51);
		this.field_6434.addCuboid(-1.5F, 5.1F, -2.0F, 4, 3, 4);
		this.field_6434.setPivot(-4.0F, 16.0F, 11.0F);
		this.field_6396 = new ModelPart(this, 44, 29);
		this.field_6396.addCuboid(-1.9F, -1.0F, -2.1F, 3, 8, 4);
		this.field_6396.setPivot(4.0F, 9.0F, -8.0F);
		this.field_6397 = new ModelPart(this, 44, 41);
		this.field_6397.addCuboid(-1.9F, 0.0F, -1.6F, 3, 5, 3);
		this.field_6397.setPivot(4.0F, 16.0F, -8.0F);
		this.field_6398 = new ModelPart(this, 44, 51);
		this.field_6398.addCuboid(-2.4F, 5.1F, -2.1F, 4, 3, 4);
		this.field_6398.setPivot(4.0F, 16.0F, -8.0F);
		this.field_6399 = new ModelPart(this, 60, 29);
		this.field_6399.addCuboid(-1.1F, -1.0F, -2.1F, 3, 8, 4);
		this.field_6399.setPivot(-4.0F, 9.0F, -8.0F);
		this.field_6400 = new ModelPart(this, 60, 41);
		this.field_6400.addCuboid(-1.1F, 0.0F, -1.6F, 3, 5, 3);
		this.field_6400.setPivot(-4.0F, 16.0F, -8.0F);
		this.field_6401 = new ModelPart(this, 60, 51);
		this.field_6401.addCuboid(-1.6F, 5.1F, -2.1F, 4, 3, 4);
		this.field_6401.setPivot(-4.0F, 16.0F, -8.0F);
		this.field_6415 = new ModelPart(this, 0, 0);
		this.field_6415.addCuboid(-2.5F, -10.0F, -1.5F, 5, 5, 7);
		this.field_6415.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6415.posX = (float) (Math.PI / 6);
		this.field_6416 = new ModelPart(this, 24, 18);
		this.field_6416.addCuboid(-2.0F, -10.0F, -7.0F, 4, 3, 6);
		this.field_6416.setPivot(0.0F, 3.95F, -10.0F);
		this.field_6416.posX = (float) (Math.PI / 6);
		this.field_6417 = new ModelPart(this, 24, 27);
		this.field_6417.addCuboid(-2.0F, -7.0F, -6.5F, 4, 2, 5);
		this.field_6417.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6417.posX = (float) (Math.PI / 6);
		this.field_6415.add(this.field_6416);
		this.field_6415.add(this.field_6417);
		this.field_6418 = new ModelPart(this, 0, 0);
		this.field_6418.addCuboid(0.45F, -12.0F, 4.0F, 2, 3, 1);
		this.field_6418.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6418.posX = (float) (Math.PI / 6);
		this.field_6419 = new ModelPart(this, 0, 0);
		this.field_6419.addCuboid(-2.45F, -12.0F, 4.0F, 2, 3, 1);
		this.field_6419.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6419.posX = (float) (Math.PI / 6);
		this.field_6420 = new ModelPart(this, 0, 12);
		this.field_6420.addCuboid(-2.0F, -16.0F, 4.0F, 2, 7, 1);
		this.field_6420.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6420.posX = (float) (Math.PI / 6);
		this.field_6420.posZ = (float) (Math.PI / 12);
		this.field_6421 = new ModelPart(this, 0, 12);
		this.field_6421.addCuboid(0.0F, -16.0F, 4.0F, 2, 7, 1);
		this.field_6421.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6421.posX = (float) (Math.PI / 6);
		this.field_6421.posZ = (float) (-Math.PI / 12);
		this.field_6422 = new ModelPart(this, 0, 12);
		this.field_6422.addCuboid(-2.05F, -9.8F, -2.0F, 4, 14, 8);
		this.field_6422.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6422.posX = (float) (Math.PI / 6);
		this.field_6402 = new ModelPart(this, 0, 34);
		this.field_6402.addCuboid(-3.0F, 0.0F, 0.0F, 8, 8, 3);
		this.field_6402.setPivot(-7.5F, 3.0F, 10.0F);
		this.field_6402.posY = (float) (Math.PI / 2);
		this.field_6403 = new ModelPart(this, 0, 47);
		this.field_6403.addCuboid(-3.0F, 0.0F, 0.0F, 8, 8, 3);
		this.field_6403.setPivot(4.5F, 3.0F, 10.0F);
		this.field_6403.posY = (float) (Math.PI / 2);
		this.field_6404 = new ModelPart(this, 80, 0);
		this.field_6404.addCuboid(-5.0F, 0.0F, -3.0F, 10, 1, 8);
		this.field_6404.setPivot(0.0F, 2.0F, 2.0F);
		this.field_6405 = new ModelPart(this, 106, 9);
		this.field_6405.addCuboid(-1.5F, -1.0F, -3.0F, 3, 1, 2);
		this.field_6405.setPivot(0.0F, 2.0F, 2.0F);
		this.field_6406 = new ModelPart(this, 80, 9);
		this.field_6406.addCuboid(-4.0F, -1.0F, 3.0F, 8, 1, 2);
		this.field_6406.setPivot(0.0F, 2.0F, 2.0F);
		this.field_6408 = new ModelPart(this, 74, 0);
		this.field_6408.addCuboid(-0.5F, 6.0F, -1.0F, 1, 2, 2);
		this.field_6408.setPivot(5.0F, 3.0F, 2.0F);
		this.field_6407 = new ModelPart(this, 70, 0);
		this.field_6407.addCuboid(-0.5F, 0.0F, -0.5F, 1, 6, 1);
		this.field_6407.setPivot(5.0F, 3.0F, 2.0F);
		this.field_6410 = new ModelPart(this, 74, 4);
		this.field_6410.addCuboid(-0.5F, 6.0F, -1.0F, 1, 2, 2);
		this.field_6410.setPivot(-5.0F, 3.0F, 2.0F);
		this.field_6409 = new ModelPart(this, 80, 0);
		this.field_6409.addCuboid(-0.5F, 0.0F, -0.5F, 1, 6, 1);
		this.field_6409.setPivot(-5.0F, 3.0F, 2.0F);
		this.field_6411 = new ModelPart(this, 74, 13);
		this.field_6411.addCuboid(1.5F, -8.0F, -4.0F, 1, 2, 2);
		this.field_6411.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6411.posX = (float) (Math.PI / 6);
		this.field_6412 = new ModelPart(this, 74, 13);
		this.field_6412.addCuboid(-2.5F, -8.0F, -4.0F, 1, 2, 2);
		this.field_6412.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6412.posX = (float) (Math.PI / 6);
		this.field_6413 = new ModelPart(this, 44, 10);
		this.field_6413.addCuboid(2.6F, -6.0F, -6.0F, 0, 3, 16);
		this.field_6413.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6414 = new ModelPart(this, 44, 5);
		this.field_6414.addCuboid(-2.6F, -6.0F, -6.0F, 0, 3, 16);
		this.field_6414.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6424 = new ModelPart(this, 58, 0);
		this.field_6424.addCuboid(-1.0F, -11.5F, 5.0F, 2, 16, 4);
		this.field_6424.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6424.posX = (float) (Math.PI / 6);
		this.field_6423 = new ModelPart(this, 80, 12);
		this.field_6423.addCuboid(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
		this.field_6423.setPivot(0.0F, 4.0F, -10.0F);
		this.field_6423.posX = (float) (Math.PI / 6);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entity;
		float f = abstractHorseEntity.method_14008(0.0F);
		boolean bl = abstractHorseEntity.isBaby();
		boolean bl2 = !bl && abstractHorseEntity.method_13975();
		boolean bl3 = abstractHorseEntity instanceof class_3135;
		boolean bl4 = !bl && bl3 && ((class_3135)abstractHorseEntity).method_13963();
		float g = abstractHorseEntity.method_13992();
		boolean bl5 = abstractHorseEntity.hasPassengers();
		if (bl2) {
			this.field_6423.render(scale);
			this.field_6404.render(scale);
			this.field_6405.render(scale);
			this.field_6406.render(scale);
			this.field_6407.render(scale);
			this.field_6408.render(scale);
			this.field_6409.render(scale);
			this.field_6410.render(scale);
			this.field_6411.render(scale);
			this.field_6412.render(scale);
			if (bl5) {
				this.field_6413.render(scale);
				this.field_6414.render(scale);
			}
		}

		if (bl) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(g, 0.5F + g * 0.5F, g);
			GlStateManager.translate(0.0F, 0.95F * (1.0F - g), 0.0F);
		}

		this.field_6429.render(scale);
		this.field_6430.render(scale);
		this.field_6431.render(scale);
		this.field_6432.render(scale);
		this.field_6433.render(scale);
		this.field_6434.render(scale);
		this.field_6396.render(scale);
		this.field_6397.render(scale);
		this.field_6398.render(scale);
		this.field_6399.render(scale);
		this.field_6400.render(scale);
		this.field_6401.render(scale);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(g, g, g);
			GlStateManager.translate(0.0F, 1.35F * (1.0F - g), 0.0F);
		}

		this.torso.render(scale);
		this.head.render(scale);
		this.field_6427.render(scale);
		this.field_6428.render(scale);
		this.field_6422.render(scale);
		this.field_6424.render(scale);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float h = 0.5F + g * g * 0.5F;
			GlStateManager.scale(h, h, h);
			if (f <= 0.0F) {
				GlStateManager.translate(0.0F, 1.35F * (1.0F - g), 0.0F);
			} else {
				GlStateManager.translate(0.0F, 0.9F * (1.0F - g) * f + 1.35F * (1.0F - g) * (1.0F - f), 0.15F * (1.0F - g) * f);
			}
		}

		if (bl3) {
			this.field_6420.render(scale);
			this.field_6421.render(scale);
		} else {
			this.field_6418.render(scale);
			this.field_6419.render(scale);
		}

		this.field_6415.render(scale);
		if (bl) {
			GlStateManager.popMatrix();
		}

		if (bl4) {
			this.field_6402.render(scale);
			this.field_6403.render(scale);
		}
	}

	private float method_5705(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
		float f = this.method_5705(entity.prevBodyYaw, entity.bodyYaw, tickDelta);
		float g = this.method_5705(entity.prevHeadYaw, entity.headYaw, tickDelta);
		float h = entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta;
		float i = g - f;
		float j = h * (float) (Math.PI / 180.0);
		if (i > 20.0F) {
			i = 20.0F;
		}

		if (i < -20.0F) {
			i = -20.0F;
		}

		if (limbDistance > 0.2F) {
			j += MathHelper.cos(limbAngle * 0.4F) * 0.15F * limbDistance;
		}

		AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entity;
		float k = abstractHorseEntity.method_14008(tickDelta);
		float l = abstractHorseEntity.method_14010(tickDelta);
		float m = 1.0F - l;
		float n = abstractHorseEntity.method_14012(tickDelta);
		boolean bl = abstractHorseEntity.field_15509 != 0;
		boolean bl2 = abstractHorseEntity.method_13975();
		boolean bl3 = abstractHorseEntity.hasPassengers();
		float o = (float)entity.ticksAlive + tickDelta;
		float p = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI);
		float q = p * 0.8F * limbDistance;
		this.field_6415.pivotY = 4.0F;
		this.field_6415.pivotZ = -10.0F;
		this.head.pivotY = 3.0F;
		this.field_6427.pivotZ = 14.0F;
		this.field_6403.pivotY = 3.0F;
		this.field_6403.pivotZ = 10.0F;
		this.torso.posX = 0.0F;
		this.field_6415.posX = (float) (Math.PI / 6) + j;
		this.field_6415.posY = i * (float) (Math.PI / 180.0);
		this.field_6415.posX = l * ((float) (Math.PI / 12) + j) + k * 2.1816616F + (1.0F - Math.max(l, k)) * this.field_6415.posX;
		this.field_6415.posY = l * i * (float) (Math.PI / 180.0) + (1.0F - Math.max(l, k)) * this.field_6415.posY;
		this.field_6415.pivotY = l * -6.0F + k * 11.0F + (1.0F - Math.max(l, k)) * this.field_6415.pivotY;
		this.field_6415.pivotZ = l * -1.0F + k * -10.0F + (1.0F - Math.max(l, k)) * this.field_6415.pivotZ;
		this.head.pivotY = l * 9.0F + m * this.head.pivotY;
		this.field_6427.pivotZ = l * 18.0F + m * this.field_6427.pivotZ;
		this.field_6403.pivotY = l * 5.5F + m * this.field_6403.pivotY;
		this.field_6403.pivotZ = l * 15.0F + m * this.field_6403.pivotZ;
		this.torso.posX = l * (float) (-Math.PI / 4) + m * this.torso.posX;
		this.field_6418.pivotY = this.field_6415.pivotY;
		this.field_6419.pivotY = this.field_6415.pivotY;
		this.field_6420.pivotY = this.field_6415.pivotY;
		this.field_6421.pivotY = this.field_6415.pivotY;
		this.field_6422.pivotY = this.field_6415.pivotY;
		this.field_6416.pivotY = 0.02F;
		this.field_6417.pivotY = 0.0F;
		this.field_6424.pivotY = this.field_6415.pivotY;
		this.field_6418.pivotZ = this.field_6415.pivotZ;
		this.field_6419.pivotZ = this.field_6415.pivotZ;
		this.field_6420.pivotZ = this.field_6415.pivotZ;
		this.field_6421.pivotZ = this.field_6415.pivotZ;
		this.field_6422.pivotZ = this.field_6415.pivotZ;
		this.field_6416.pivotZ = 0.02F - n;
		this.field_6417.pivotZ = n;
		this.field_6424.pivotZ = this.field_6415.pivotZ;
		this.field_6418.posX = this.field_6415.posX;
		this.field_6419.posX = this.field_6415.posX;
		this.field_6420.posX = this.field_6415.posX;
		this.field_6421.posX = this.field_6415.posX;
		this.field_6422.posX = this.field_6415.posX;
		this.field_6416.posX = -0.09424778F * n;
		this.field_6417.posX = (float) (Math.PI / 20) * n;
		this.field_6424.posX = this.field_6415.posX;
		this.field_6418.posY = this.field_6415.posY;
		this.field_6419.posY = this.field_6415.posY;
		this.field_6420.posY = this.field_6415.posY;
		this.field_6421.posY = this.field_6415.posY;
		this.field_6422.posY = this.field_6415.posY;
		this.field_6416.posY = 0.0F;
		this.field_6417.posY = 0.0F;
		this.field_6424.posY = this.field_6415.posY;
		this.field_6402.posX = q / 5.0F;
		this.field_6403.posX = -q / 5.0F;
		float r = (float) (Math.PI / 12) * l;
		float s = MathHelper.cos(o * 0.6F + (float) Math.PI);
		this.field_6396.pivotY = -2.0F * l + 9.0F * m;
		this.field_6396.pivotZ = -2.0F * l + -8.0F * m;
		this.field_6399.pivotY = this.field_6396.pivotY;
		this.field_6399.pivotZ = this.field_6396.pivotZ;
		this.field_6430.pivotY = this.field_6429.pivotY + MathHelper.sin((float) (Math.PI / 2) + r + m * -p * 0.5F * limbDistance) * 7.0F;
		this.field_6430.pivotZ = this.field_6429.pivotZ + MathHelper.cos((float) (-Math.PI / 2) + r + m * -p * 0.5F * limbDistance) * 7.0F;
		this.field_6433.pivotY = this.field_6432.pivotY + MathHelper.sin((float) (Math.PI / 2) + r + m * p * 0.5F * limbDistance) * 7.0F;
		this.field_6433.pivotZ = this.field_6432.pivotZ + MathHelper.cos((float) (-Math.PI / 2) + r + m * p * 0.5F * limbDistance) * 7.0F;
		float t = ((float) (-Math.PI / 3) + s) * l + q * m;
		float u = ((float) (-Math.PI / 3) - s) * l + -q * m;
		this.field_6397.pivotY = this.field_6396.pivotY + MathHelper.sin((float) (Math.PI / 2) + t) * 7.0F;
		this.field_6397.pivotZ = this.field_6396.pivotZ + MathHelper.cos((float) (-Math.PI / 2) + t) * 7.0F;
		this.field_6400.pivotY = this.field_6399.pivotY + MathHelper.sin((float) (Math.PI / 2) + u) * 7.0F;
		this.field_6400.pivotZ = this.field_6399.pivotZ + MathHelper.cos((float) (-Math.PI / 2) + u) * 7.0F;
		this.field_6429.posX = r + -p * 0.5F * limbDistance * m;
		this.field_6430.posX = -0.08726646F * l + (-p * 0.5F * limbDistance - Math.max(0.0F, p * 0.5F * limbDistance)) * m;
		this.field_6431.posX = this.field_6430.posX;
		this.field_6432.posX = r + p * 0.5F * limbDistance * m;
		this.field_6433.posX = -0.08726646F * l + (p * 0.5F * limbDistance - Math.max(0.0F, -p * 0.5F * limbDistance)) * m;
		this.field_6434.posX = this.field_6433.posX;
		this.field_6396.posX = t;
		this.field_6397.posX = (this.field_6396.posX + (float) Math.PI * Math.max(0.0F, 0.2F + s * 0.2F)) * l + (q + Math.max(0.0F, p * 0.5F * limbDistance)) * m;
		this.field_6398.posX = this.field_6397.posX;
		this.field_6399.posX = u;
		this.field_6400.posX = (this.field_6399.posX + (float) Math.PI * Math.max(0.0F, 0.2F - s * 0.2F)) * l + (-q + Math.max(0.0F, -p * 0.5F * limbDistance)) * m;
		this.field_6401.posX = this.field_6400.posX;
		this.field_6431.pivotY = this.field_6430.pivotY;
		this.field_6431.pivotZ = this.field_6430.pivotZ;
		this.field_6434.pivotY = this.field_6433.pivotY;
		this.field_6434.pivotZ = this.field_6433.pivotZ;
		this.field_6398.pivotY = this.field_6397.pivotY;
		this.field_6398.pivotZ = this.field_6397.pivotZ;
		this.field_6401.pivotY = this.field_6400.pivotY;
		this.field_6401.pivotZ = this.field_6400.pivotZ;
		if (bl2) {
			this.field_6404.pivotY = l * 0.5F + m * 2.0F;
			this.field_6404.pivotZ = l * 11.0F + m * 2.0F;
			this.field_6405.pivotY = this.field_6404.pivotY;
			this.field_6406.pivotY = this.field_6404.pivotY;
			this.field_6407.pivotY = this.field_6404.pivotY;
			this.field_6409.pivotY = this.field_6404.pivotY;
			this.field_6408.pivotY = this.field_6404.pivotY;
			this.field_6410.pivotY = this.field_6404.pivotY;
			this.field_6402.pivotY = this.field_6403.pivotY;
			this.field_6405.pivotZ = this.field_6404.pivotZ;
			this.field_6406.pivotZ = this.field_6404.pivotZ;
			this.field_6407.pivotZ = this.field_6404.pivotZ;
			this.field_6409.pivotZ = this.field_6404.pivotZ;
			this.field_6408.pivotZ = this.field_6404.pivotZ;
			this.field_6410.pivotZ = this.field_6404.pivotZ;
			this.field_6402.pivotZ = this.field_6403.pivotZ;
			this.field_6404.posX = this.torso.posX;
			this.field_6405.posX = this.torso.posX;
			this.field_6406.posX = this.torso.posX;
			this.field_6413.pivotY = this.field_6415.pivotY;
			this.field_6414.pivotY = this.field_6415.pivotY;
			this.field_6423.pivotY = this.field_6415.pivotY;
			this.field_6411.pivotY = this.field_6415.pivotY;
			this.field_6412.pivotY = this.field_6415.pivotY;
			this.field_6413.pivotZ = this.field_6415.pivotZ;
			this.field_6414.pivotZ = this.field_6415.pivotZ;
			this.field_6423.pivotZ = this.field_6415.pivotZ;
			this.field_6411.pivotZ = this.field_6415.pivotZ;
			this.field_6412.pivotZ = this.field_6415.pivotZ;
			this.field_6413.posX = j;
			this.field_6414.posX = j;
			this.field_6423.posX = this.field_6415.posX;
			this.field_6411.posX = this.field_6415.posX;
			this.field_6412.posX = this.field_6415.posX;
			this.field_6423.posY = this.field_6415.posY;
			this.field_6411.posY = this.field_6415.posY;
			this.field_6413.posY = this.field_6415.posY;
			this.field_6412.posY = this.field_6415.posY;
			this.field_6414.posY = this.field_6415.posY;
			if (bl3) {
				this.field_6407.posX = (float) (-Math.PI / 3);
				this.field_6408.posX = (float) (-Math.PI / 3);
				this.field_6409.posX = (float) (-Math.PI / 3);
				this.field_6410.posX = (float) (-Math.PI / 3);
				this.field_6407.posZ = 0.0F;
				this.field_6408.posZ = 0.0F;
				this.field_6409.posZ = 0.0F;
				this.field_6410.posZ = 0.0F;
			} else {
				this.field_6407.posX = q / 3.0F;
				this.field_6408.posX = q / 3.0F;
				this.field_6409.posX = q / 3.0F;
				this.field_6410.posX = q / 3.0F;
				this.field_6407.posZ = q / 5.0F;
				this.field_6408.posZ = q / 5.0F;
				this.field_6409.posZ = -q / 5.0F;
				this.field_6410.posZ = -q / 5.0F;
			}
		}

		r = (float) (-Math.PI * 5.0 / 12.0) + limbDistance * 1.5F;
		if (r > 0.0F) {
			r = 0.0F;
		}

		if (bl) {
			this.head.posY = MathHelper.cos(o * 0.7F);
			r = 0.0F;
		} else {
			this.head.posY = 0.0F;
		}

		this.field_6427.posY = this.head.posY;
		this.field_6428.posY = this.head.posY;
		this.field_6427.pivotY = this.head.pivotY;
		this.field_6428.pivotY = this.head.pivotY;
		this.field_6427.pivotZ = this.head.pivotZ;
		this.field_6428.pivotZ = this.head.pivotZ;
		this.head.posX = r;
		this.field_6427.posX = r;
		this.field_6428.posX = (float) (-Math.PI / 12) + r;
	}
}
