package net.minecraft.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Particle extends Entity {
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
	protected float alpha = 1.0F;
	protected Sprite sprite;
	public static double field_1722;
	public static double field_1723;
	public static double field_1724;

	protected Particle(World world, double d, double e, double f) {
		super(world);
		this.setBounds(0.2F, 0.2F);
		this.updatePosition(d, e, f);
		this.prevTickX = this.prevX = d;
		this.prevTickY = this.prevY = e;
		this.prevTickZ = this.prevZ = f;
		this.red = this.green = this.blue = 1.0F;
		this.field_1725 = this.random.nextFloat() * 3.0F;
		this.field_1726 = this.random.nextFloat() * 3.0F;
		this.scale = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
		this.maxAge = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
		this.age = 0;
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

	public Particle move(float f) {
		this.velocityX *= (double)f;
		this.velocityY = (this.velocityY - 0.1F) * (double)f + 0.1F;
		this.velocityZ *= (double)f;
		return this;
	}

	public Particle scale(float f) {
		this.setBounds(0.2F * f, 0.2F * f);
		this.scale *= f;
		return this;
	}

	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setColorAlpha(float alpha) {
		if (this.alpha == 1.0F && alpha < 1.0F) {
			MinecraftClient.getInstance().particleManager.moveToAlphaLayer(this);
		} else if (this.alpha < 1.0F && alpha == 1.0F) {
			MinecraftClient.getInstance().particleManager.moveToNoAlphaLayer(this);
		}

		this.alpha = alpha;
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

	public float getAlpha() {
		return this.alpha;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.remove();
		}

		this.velocityY = this.velocityY - 0.04 * (double)this.gravityStrength;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = (float)this.field_5935 / 16.0F;
		float l = f + 0.0624375F;
		float m = (float)this.field_5936 / 16.0F;
		float n = m + 0.0624375F;
		float o = 0.1F * this.scale;
		if (this.sprite != null) {
			f = this.sprite.getMinU();
			l = this.sprite.getMaxU();
			m = this.sprite.getMinV();
			n = this.sprite.getMaxV();
		}

		float p = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - field_1722);
		float q = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - field_1723);
		float r = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - field_1724);
		int s = this.getLightmapCoordinates(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		builder.vertex((double)(p - g * o - j * o), (double)(q - h * o), (double)(r - i * o - k * o))
			.texture((double)l, (double)n)
			.color(this.red, this.green, this.blue, this.alpha)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p - g * o + j * o), (double)(q + h * o), (double)(r - i * o + k * o))
			.texture((double)l, (double)m)
			.color(this.red, this.green, this.blue, this.alpha)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * o + j * o), (double)(q + h * o), (double)(r + i * o + k * o))
			.texture((double)f, (double)m)
			.color(this.red, this.green, this.blue, this.alpha)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * o - j * o), (double)(q - h * o), (double)(r + i * o - k * o))
			.texture((double)f, (double)n)
			.color(this.red, this.green, this.blue, this.alpha)
			.texture2(t, u)
			.next();
	}

	public int getLayer() {
		return 0;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
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

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()
			+ ", Pos ("
			+ this.x
			+ ","
			+ this.y
			+ ","
			+ this.z
			+ "), RGBA ("
			+ this.red
			+ ","
			+ this.green
			+ ","
			+ this.blue
			+ ","
			+ this.alpha
			+ "), Age "
			+ this.age;
	}
}
