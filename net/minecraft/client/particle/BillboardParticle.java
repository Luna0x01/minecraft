package net.minecraft.client.particle;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class BillboardParticle extends Particle {
	protected float scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

	protected BillboardParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected BillboardParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
		Vec3d vec3d = camera.getPos();
		float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
		float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
		float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternion quaternion;
		if (this.angle == 0.0F) {
			quaternion = camera.getRotation();
		} else {
			quaternion = new Quaternion(camera.getRotation());
			float j = MathHelper.lerp(f, this.prevAngle, this.angle);
			quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getRadialQuaternion(j));
		}

		Vector3f vector3f = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f.rotate(quaternion);
		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float k = this.getSize(f);

		for (int l = 0; l < 4; l++) {
			Vector3f vector3f2 = vector3fs[l];
			vector3f2.rotate(quaternion);
			vector3f2.scale(k);
			vector3f2.add(g, h, i);
		}

		float m = this.getMinU();
		float n = this.getMaxU();
		float o = this.getMinV();
		float p = this.getMaxV();
		int q = this.getColorMultiplier(f);
		vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ())
			.texture(n, p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(q)
			.next();
		vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ())
			.texture(n, o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(q)
			.next();
		vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ())
			.texture(m, o)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(q)
			.next();
		vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ())
			.texture(m, p)
			.color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha)
			.light(q)
			.next();
	}

	public float getSize(float f) {
		return this.scale;
	}

	@Override
	public Particle scale(float f) {
		this.scale *= f;
		return super.scale(f);
	}

	protected abstract float getMinU();

	protected abstract float getMaxU();

	protected abstract float getMinV();

	protected abstract float getMaxV();
}
