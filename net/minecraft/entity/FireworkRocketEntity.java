package net.minecraft.entity;

import com.google.common.base.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class FireworkRocketEntity extends Entity {
	private static final TrackedData<Optional<ItemStack>> ITEM = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private int life;
	private int lifeTime;

	public FireworkRocketEntity(World world) {
		super(world);
		this.setBounds(0.25F, 0.25F);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(ITEM, Optional.absent());
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < 4096.0;
	}

	public FireworkRocketEntity(World world, double d, double e, double f, @Nullable ItemStack itemStack) {
		super(world);
		this.life = 0;
		this.setBounds(0.25F, 0.25F);
		this.updatePosition(d, e, f);
		int i = 1;
		if (itemStack != null && itemStack.hasNbt()) {
			this.dataTracker.set(ITEM, Optional.of(itemStack));
			NbtCompound nbtCompound = itemStack.getNbt();
			NbtCompound nbtCompound2 = nbtCompound.getCompound("Fireworks");
			i += nbtCompound2.getByte("Flight");
		}

		this.velocityX = this.random.nextGaussian() * 0.001;
		this.velocityZ = this.random.nextGaussian() * 0.001;
		this.velocityY = 0.05;
		this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	@Override
	public void tick() {
		this.prevTickX = this.x;
		this.prevTickY = this.y;
		this.prevTickZ = this.z;
		super.tick();
		this.velocityX *= 1.15;
		this.velocityZ *= 1.15;
		this.velocityY += 0.04;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
		this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
		if (this.life == 0 && !this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_FIREWORK_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
		}

		this.life++;
		if (this.world.isClient && this.life % 2 < 2) {
			this.world
				.addParticle(
					ParticleType.FIREWORK_SPARK, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.velocityY * 0.5, this.random.nextGaussian() * 0.05
				);
		}

		if (!this.world.isClient && this.life > this.lifeTime) {
			this.world.sendEntityStatus(this, (byte)17);
			this.remove();
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 17 && this.world.isClient) {
			ItemStack itemStack = (ItemStack)this.dataTracker.get(ITEM).orNull();
			NbtCompound nbtCompound = null;
			if (itemStack != null && itemStack.hasNbt()) {
				nbtCompound = itemStack.getNbt().getCompound("Fireworks");
			}

			this.world.addFireworkParticle(this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ, nbtCompound);
		}

		super.handleStatus(status);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema("FireworksRocketEntity", "FireworksItem"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("Life", this.life);
		nbt.putInt("LifeTime", this.lifeTime);
		ItemStack itemStack = (ItemStack)this.dataTracker.get(ITEM).orNull();
		if (itemStack != null) {
			nbt.put("FireworksItem", itemStack.toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.life = nbt.getInt("Life");
		this.lifeTime = nbt.getInt("LifeTime");
		NbtCompound nbtCompound = nbt.getCompound("FireworksItem");
		if (nbtCompound != null) {
			ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
			if (itemStack != null) {
				this.dataTracker.set(ITEM, Optional.of(itemStack));
			}
		}
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return super.getBrightnessAtEyes(f);
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return super.getLightmapCoordinates(f);
	}

	@Override
	public boolean isAttackable() {
		return false;
	}
}
