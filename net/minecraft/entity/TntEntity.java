package net.minecraft.entity;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class TntEntity extends Entity {
	public int fuseTimer;
	private LivingEntity igniter;

	public TntEntity(World world) {
		super(world);
		this.inanimate = true;
		this.setBounds(0.98F, 0.98F);
	}

	public TntEntity(World world, double d, double e, double f, LivingEntity livingEntity) {
		this(world);
		this.updatePosition(d, e, f);
		float g = (float)(Math.random() * (float) Math.PI * 2.0);
		this.velocityX = (double)(-((float)Math.sin((double)g)) * 0.02F);
		this.velocityY = 0.2F;
		this.velocityZ = (double)(-((float)Math.cos((double)g)) * 0.02F);
		this.fuseTimer = 80;
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.igniter = livingEntity;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.velocityY -= 0.04F;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
			this.velocityY *= -0.5;
		}

		if (this.fuseTimer-- <= 0) {
			this.remove();
			if (!this.world.isClient) {
				this.explode();
			}
		} else {
			this.updateWaterState();
			this.world.addParticle(ParticleType.SMOKE, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
		}
	}

	private void explode() {
		float f = 4.0F;
		this.world.createExplosion(this, this.x, this.y + (double)(this.height / 16.0F), this.z, f, true);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putByte("Fuse", (byte)this.fuseTimer);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.fuseTimer = nbt.getByte("Fuse");
	}

	public LivingEntity getIgniter() {
		return this.igniter;
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}
}
