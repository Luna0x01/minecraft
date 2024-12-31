package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.class_4489;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.World;

public class Particle {
	private static final Box field_13423 = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	protected World field_13424;
	protected double field_13425;
	protected double field_13426;
	protected double field_13427;
	protected double field_13428;
	protected double field_13429;
	protected double field_13430;
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	private Box field_13422 = field_13423;
	protected boolean field_13434;
	protected boolean field_14950;
	protected boolean field_13435;
	protected float field_13436 = 0.6F;
	protected float field_13437 = 1.8F;
	protected Random field_13438 = new Random();
	protected int field_5935;
	protected int field_5936;
	protected float field_1725;
	protected float field_1726;
	protected int age;
	protected int maxAge;
	protected float scale;
	protected float gravityStrength;
	protected float red;
	protected float green;
	protected float blue;
	protected float field_13421 = 1.0F;
	protected Sprite sprite;
	protected float field_14947;
	protected float field_14948;
	public static double field_1722;
	public static double field_1723;
	public static double field_1724;
	public static Vec3d field_14949;

	protected Particle(World world, double d, double e, double f) {
		this.field_13424 = world;
		this.method_12244(0.2F, 0.2F);
		this.method_12247(d, e, f);
		this.field_13425 = d;
		this.field_13426 = e;
		this.field_13427 = f;
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.field_1725 = this.field_13438.nextFloat() * 3.0F;
		this.field_1726 = this.field_13438.nextFloat() * 3.0F;
		this.scale = (this.field_13438.nextFloat() * 0.5F + 0.5F) * 2.0F;
		this.maxAge = (int)(4.0F / (this.field_13438.nextFloat() * 0.9F + 0.1F));
		this.age = 0;
		this.field_14950 = true;
	}

	public Particle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f);
		this.velocityX = g + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.velocityY = h + (Math.random() * 2.0 - 1.0) * 0.4F;
		this.velocityZ = i + (Math.random() * 2.0 - 1.0) * 0.4F;
		float j = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
		float k = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		this.velocityX = this.velocityX / (double)k * (double)j * 0.4F;
		this.velocityY = this.velocityY / (double)k * (double)j * 0.4F + 0.1F;
		this.velocityZ = this.velocityZ / (double)k * (double)j * 0.4F;
	}

	public Particle method_12249(float f) {
		this.velocityX *= (double)f;
		this.velocityY = (this.velocityY - 0.1F) * (double)f + 0.1F;
		this.velocityZ *= (double)f;
		return this;
	}

	public Particle scale(float f) {
		this.method_12244(0.2F * f, 0.2F * f);
		this.scale *= f;
		return this;
	}

	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void method_12250(float f) {
		this.field_13421 = f;
	}

	public boolean method_12248() {
		return false;
	}

	public float getRed() {
		return this.red;
	}

	public float getGreen() {
		return this.green;
	}

	public float getBlue() {
		return this.blue;
	}

	public void method_12245(int i) {
		this.maxAge = i;
	}

	public int method_19013() {
		return this.maxAge;
	}

	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		this.velocityY = this.velocityY - 0.04 * (double)this.gravityStrength;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = (float)this.field_5935 / 32.0F;
		float l = f + 0.03121875F;
		float m = (float)this.field_5936 / 32.0F;
		float n = m + 0.03121875F;
		float o = 0.1F * this.scale;
		if (this.sprite != null) {
			f = this.sprite.getMinU();
			l = this.sprite.getMaxU();
			m = this.sprite.getMinV();
			n = this.sprite.getMaxV();
		}

		float p = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
		float q = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
		float r = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
		int s = this.method_12243(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * o - j * o), (double)(-h * o), (double)(-i * o - k * o)),
			new Vec3d((double)(-g * o + j * o), (double)(h * o), (double)(-i * o + k * o)),
			new Vec3d((double)(g * o + j * o), (double)(h * o), (double)(i * o + k * o)),
			new Vec3d((double)(g * o - j * o), (double)(-h * o), (double)(i * o - k * o))
		};
		if (this.field_14947 != 0.0F) {
			float v = this.field_14947 + (this.field_14947 - this.field_14948) * tickDelta;
			float w = MathHelper.cos(v * 0.5F);
			float x = MathHelper.sin(v * 0.5F) * (float)field_14949.x;
			float y = MathHelper.sin(v * 0.5F) * (float)field_14949.y;
			float z = MathHelper.sin(v * 0.5F) * (float)field_14949.z;
			Vec3d vec3d = new Vec3d((double)x, (double)y, (double)z);

			for (int aa = 0; aa < 4; aa++) {
				vec3ds[aa] = vec3d.multiply(2.0 * vec3ds[aa].dotProduct(vec3d))
					.add(vec3ds[aa].multiply((double)(w * w) - vec3d.dotProduct(vec3d)))
					.add(vec3d.crossProduct(vec3ds[aa]).multiply((double)(2.0F * w)));
			}
		}

		builder.vertex((double)p + vec3ds[0].x, (double)q + vec3ds[0].y, (double)r + vec3ds[0].z)
			.texture((double)l, (double)n)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
		builder.vertex((double)p + vec3ds[1].x, (double)q + vec3ds[1].y, (double)r + vec3ds[1].z)
			.texture((double)l, (double)m)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
		builder.vertex((double)p + vec3ds[2].x, (double)q + vec3ds[2].y, (double)r + vec3ds[2].z)
			.texture((double)f, (double)m)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
		builder.vertex((double)p + vec3ds[3].x, (double)q + vec3ds[3].y, (double)r + vec3ds[3].z)
			.texture((double)f, (double)n)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
	}

	public int getLayer() {
		return 0;
	}

	public void setTexture(Sprite sprite) {
		int i = this.getLayer();
		if (i == 1) {
			this.sprite = sprite;
		} else {
			throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
		}
	}

	public void setMiscTexture(int i) {
		if (this.getLayer() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.field_5935 = i % 16;
			this.field_5936 = i / 16;
		}
	}

	public void method_5133() {
		this.field_5935++;
	}

	public String toString() {
		return this.getClass().getSimpleName()
			+ ", Pos ("
			+ this.field_13428
			+ ","
			+ this.field_13429
			+ ","
			+ this.field_13430
			+ "), RGBA ("
			+ this.red
			+ ","
			+ this.green
			+ ","
			+ this.blue
			+ ","
			+ this.field_13421
			+ "), Age "
			+ this.age;
	}

	public void method_12251() {
		this.field_13435 = true;
	}

	protected void method_12244(float f, float g) {
		if (f != this.field_13436 || g != this.field_13437) {
			this.field_13436 = f;
			this.field_13437 = g;
			Box box = this.method_12254();
			double d = (box.minX + box.maxX - (double)f) / 2.0;
			double e = (box.minZ + box.maxZ - (double)f) / 2.0;
			this.method_12246(new Box(d, box.minY, e, d + (double)this.field_13436, box.minY + (double)this.field_13437, e + (double)this.field_13436));
		}
	}

	public void method_12247(double d, double e, double f) {
		this.field_13428 = d;
		this.field_13429 = e;
		this.field_13430 = f;
		float g = this.field_13436 / 2.0F;
		float h = this.field_13437;
		this.method_12246(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	public void method_12242(double d, double e, double f) {
		double g = d;
		double h = e;
		double i = f;
		if (this.field_14950 && (d != 0.0 || e != 0.0 || f != 0.0)) {
			class_4489<VoxelShape> lv = new class_4489<>(this.field_13424.method_16365(null, this.method_12254(), d, e, f));
			e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, this.method_12254(), lv.method_21527(), e);
			this.method_12246(this.method_12254().offset(0.0, e, 0.0));
			d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, this.method_12254(), lv.method_21527(), d);
			if (d != 0.0) {
				this.method_12246(this.method_12254().offset(d, 0.0, 0.0));
			}

			f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, this.method_12254(), lv.method_21527(), f);
			if (f != 0.0) {
				this.method_12246(this.method_12254().offset(0.0, 0.0, f));
			}
		} else {
			this.method_12246(this.method_12254().offset(d, e, f));
		}

		this.method_12252();
		this.field_13434 = h != e && h < 0.0;
		if (g != d) {
			this.velocityX = 0.0;
		}

		if (i != f) {
			this.velocityZ = 0.0;
		}
	}

	protected void method_12252() {
		Box box = this.method_12254();
		this.field_13428 = (box.minX + box.maxX) / 2.0;
		this.field_13429 = box.minY;
		this.field_13430 = (box.minZ + box.maxZ) / 2.0;
	}

	public int method_12243(float f) {
		BlockPos blockPos = new BlockPos(this.field_13428, this.field_13429, this.field_13430);
		return this.field_13424.method_16359(blockPos) ? this.field_13424.method_8578(blockPos, 0) : 0;
	}

	public boolean method_12253() {
		return !this.field_13435;
	}

	public Box method_12254() {
		return this.field_13422;
	}

	public void method_12246(Box box) {
		this.field_13422 = box;
	}
}
