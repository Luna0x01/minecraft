package net.minecraft.client.render.entity.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GuardianEntityModel extends EntityModel {
	private ModelPart body;
	private ModelPart eye;
	private ModelPart[] spikes;
	private ModelPart[] tail;

	public GuardianEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.spikes = new ModelPart[12];
		this.body = new ModelPart(this);
		this.body.setTextureOffset(0, 0).addCuboid(-6.0F, 10.0F, -8.0F, 12, 12, 16);
		this.body.setTextureOffset(0, 28).addCuboid(-8.0F, 10.0F, -6.0F, 2, 12, 12);
		this.body.setTextureOffset(0, 28).addCuboid(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
		this.body.setTextureOffset(16, 40).addCuboid(-6.0F, 8.0F, -6.0F, 12, 2, 12);
		this.body.setTextureOffset(16, 40).addCuboid(-6.0F, 22.0F, -6.0F, 12, 2, 12);

		for (int i = 0; i < this.spikes.length; i++) {
			this.spikes[i] = new ModelPart(this, 0, 0);
			this.spikes[i].addCuboid(-1.0F, -4.5F, -1.0F, 2, 9, 2);
			this.body.add(this.spikes[i]);
		}

		this.eye = new ModelPart(this, 8, 0);
		this.eye.addCuboid(-1.0F, 15.0F, 0.0F, 2, 2, 1);
		this.body.add(this.eye);
		this.tail = new ModelPart[3];
		this.tail[0] = new ModelPart(this, 40, 0);
		this.tail[0].addCuboid(-2.0F, 14.0F, 7.0F, 4, 4, 8);
		this.tail[1] = new ModelPart(this, 0, 54);
		this.tail[1].addCuboid(0.0F, 14.0F, 0.0F, 3, 3, 7);
		this.tail[2] = new ModelPart(this);
		this.tail[2].setTextureOffset(41, 32).addCuboid(0.0F, 14.0F, 0.0F, 2, 2, 6);
		this.tail[2].setTextureOffset(25, 19).addCuboid(1.0F, 10.5F, 3.0F, 1, 9, 9);
		this.body.add(this.tail[0]);
		this.tail[0].add(this.tail[1]);
		this.tail[1].add(this.tail[2]);
	}

	public int method_9635() {
		return 54;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.body.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		GuardianEntity guardianEntity = (GuardianEntity)entity;
		float f = tickDelta - (float)guardianEntity.ticksAlive;
		this.body.posY = age * (float) (Math.PI / 180.0);
		this.body.posX = headPitch * (float) (Math.PI / 180.0);
		float[] fs = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
		float[] gs = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
		float[] hs = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
		float[] is = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
		float[] js = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
		float[] ks = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
		float g = (1.0F - guardianEntity.getTailAngle(f)) * 0.55F;

		for (int i = 0; i < 12; i++) {
			this.spikes[i].posX = (float) Math.PI * fs[i];
			this.spikes[i].posY = (float) Math.PI * gs[i];
			this.spikes[i].posZ = (float) Math.PI * hs[i];
			this.spikes[i].pivotX = is[i] * (1.0F + MathHelper.cos(tickDelta * 1.5F + (float)i) * 0.01F - g);
			this.spikes[i].pivotY = 16.0F + js[i] * (1.0F + MathHelper.cos(tickDelta * 1.5F + (float)i) * 0.01F - g);
			this.spikes[i].pivotZ = ks[i] * (1.0F + MathHelper.cos(tickDelta * 1.5F + (float)i) * 0.01F - g);
		}

		this.eye.pivotZ = -8.25F;
		Entity entity2 = MinecraftClient.getInstance().getCameraEntity();
		if (guardianEntity.hasBeamTarget()) {
			entity2 = guardianEntity.getBeamTarget();
		}

		if (entity2 != null) {
			Vec3d vec3d = entity2.getCameraPosVec(0.0F);
			Vec3d vec3d2 = entity.getCameraPosVec(0.0F);
			double d = vec3d.y - vec3d2.y;
			if (d > 0.0) {
				this.eye.pivotY = 0.0F;
			} else {
				this.eye.pivotY = 1.0F;
			}

			Vec3d vec3d3 = entity.getRotationVector(0.0F);
			vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
			Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY((float) (Math.PI / 2));
			double e = vec3d3.dotProduct(vec3d4);
			this.eye.pivotX = MathHelper.sqrt((float)Math.abs(e)) * 2.0F * (float)Math.signum(e);
		}

		this.eye.visible = true;
		float h = guardianEntity.getSpikesExtension(f);
		this.tail[0].posY = MathHelper.sin(h) * (float) Math.PI * 0.05F;
		this.tail[1].posY = MathHelper.sin(h) * (float) Math.PI * 0.1F;
		this.tail[1].pivotX = -1.5F;
		this.tail[1].pivotY = 0.5F;
		this.tail[1].pivotZ = 14.0F;
		this.tail[2].posY = MathHelper.sin(h) * (float) Math.PI * 0.15F;
		this.tail[2].pivotX = 0.5F;
		this.tail[2].pivotY = 0.5F;
		this.tail[2].pivotZ = 6.0F;
	}
}
