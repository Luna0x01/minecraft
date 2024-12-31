package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AbstractArrowEntity extends Entity implements Projectile {
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	private Block block;
	private int blockData;
	private boolean inGround;
	public int pickup;
	public int shake;
	public Entity owner;
	private int life;
	private int field_4022;
	private double damage = 2.0;
	private int punch;

	public AbstractArrowEntity(World world) {
		super(world);
		this.renderDistanceMultiplier = 10.0;
		this.setBounds(0.5F, 0.5F);
	}

	public AbstractArrowEntity(World world, double d, double e, double f) {
		super(world);
		this.renderDistanceMultiplier = 10.0;
		this.setBounds(0.5F, 0.5F);
		this.updatePosition(d, e, f);
	}

	public AbstractArrowEntity(World world, LivingEntity livingEntity, LivingEntity livingEntity2, float f, float g) {
		super(world);
		this.renderDistanceMultiplier = 10.0;
		this.owner = livingEntity;
		if (livingEntity instanceof PlayerEntity) {
			this.pickup = 1;
		}

		this.y = livingEntity.y + (double)livingEntity.getEyeHeight() - 0.1F;
		double d = livingEntity2.x - livingEntity.x;
		double e = livingEntity2.getBoundingBox().minY + (double)(livingEntity2.height / 3.0F) - this.y;
		double h = livingEntity2.z - livingEntity.z;
		double i = (double)MathHelper.sqrt(d * d + h * h);
		if (!(i < 1.0E-7)) {
			float j = (float)(MathHelper.atan2(h, d) * 180.0 / (float) Math.PI) - 90.0F;
			float k = (float)(-(MathHelper.atan2(e, i) * 180.0 / (float) Math.PI));
			double l = d / i;
			double m = h / i;
			this.refreshPositionAndAngles(livingEntity.x + l, this.y, livingEntity.z + m, j, k);
			float n = (float)(i * 0.2F);
			this.setVelocity(d, e + (double)n, h, f, g);
		}
	}

	public AbstractArrowEntity(World world, LivingEntity livingEntity, float f) {
		super(world);
		this.renderDistanceMultiplier = 10.0;
		this.owner = livingEntity;
		if (livingEntity instanceof PlayerEntity) {
			this.pickup = 1;
		}

		this.setBounds(0.5F, 0.5F);
		this.refreshPositionAndAngles(livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight(), livingEntity.z, livingEntity.yaw, livingEntity.pitch);
		this.x = this.x - (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
		this.y -= 0.1F;
		this.z = this.z - (double)(MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
		this.updatePosition(this.x, this.y, this.z);
		this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));
		this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));
		this.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float) Math.PI));
		this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, f * 1.5F, 1.0F);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.track(16, (byte)0);
	}

	@Override
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x /= (double)f;
		y /= (double)f;
		z /= (double)f;
		x += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.0075F * (double)divergence;
		y += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.0075F * (double)divergence;
		z += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.0075F * (double)divergence;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		float g = MathHelper.sqrt(x * x + z * z);
		this.prevYaw = this.yaw = (float)(MathHelper.atan2(x, z) * 180.0 / (float) Math.PI);
		this.prevPitch = this.pitch = (float)(MathHelper.atan2(y, (double)g) * 180.0 / (float) Math.PI);
		this.life = 0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.prevYaw = this.yaw = (float)(MathHelper.atan2(x, z) * 180.0 / (float) Math.PI);
			this.prevPitch = this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0 / (float) Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			this.life = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.prevYaw = this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
			this.prevPitch = this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0 / (float) Math.PI);
		}

		BlockPos blockPos = new BlockPos(this.blockX, this.blockY, this.blockZ);
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block.getMaterial() != Material.AIR) {
			block.setBoundingBox(this.world, blockPos);
			Box box = block.getCollisionBox(this.world, blockPos, blockState);
			if (box != null && box.contains(new Vec3d(this.x, this.y, this.z))) {
				this.inGround = true;
			}
		}

		if (this.shake > 0) {
			this.shake--;
		}

		if (this.inGround) {
			int i = block.getData(blockState);
			if (block == this.block && i == this.blockData) {
				this.life++;
				if (this.life >= 1200) {
					this.remove();
				}
			} else {
				this.inGround = false;
				this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
				this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
				this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
				this.life = 0;
				this.field_4022 = 0;
			}
		} else {
			this.field_4022++;
			Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
			Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			BlockHitResult blockHitResult = this.world.rayTrace(vec3d, vec3d2, false, true, false);
			vec3d = new Vec3d(this.x, this.y, this.z);
			vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			if (blockHitResult != null) {
				vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
			}

			Entity entity = null;
			List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
			double d = 0.0;

			for (int j = 0; j < list.size(); j++) {
				Entity entity2 = (Entity)list.get(j);
				if (entity2.collides() && (entity2 != this.owner || this.field_4022 >= 5)) {
					float g = 0.3F;
					Box box2 = entity2.getBoundingBox().expand((double)g, (double)g, (double)g);
					BlockHitResult blockHitResult2 = box2.method_585(vec3d, vec3d2);
					if (blockHitResult2 != null) {
						double e = vec3d.squaredDistanceTo(blockHitResult2.pos);
						if (e < d || d == 0.0) {
							entity = entity2;
							d = e;
						}
					}
				}
			}

			if (entity != null) {
				blockHitResult = new BlockHitResult(entity);
			}

			if (blockHitResult != null && blockHitResult.entity != null && blockHitResult.entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)blockHitResult.entity;
				if (playerEntity.abilities.invulnerable || this.owner instanceof PlayerEntity && !((PlayerEntity)this.owner).shouldDamagePlayer(playerEntity)) {
					blockHitResult = null;
				}
			}

			if (blockHitResult != null) {
				if (blockHitResult.entity != null) {
					float h = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
					int k = MathHelper.ceil((double)h * this.damage);
					if (this.isCritical()) {
						k += this.random.nextInt(k / 2 + 2);
					}

					DamageSource damageSource;
					if (this.owner == null) {
						damageSource = DamageSource.arrow(this, this);
					} else {
						damageSource = DamageSource.arrow(this, this.owner);
					}

					if (this.isOnFire() && !(blockHitResult.entity instanceof EndermanEntity)) {
						blockHitResult.entity.setOnFireFor(5);
					}

					if (blockHitResult.entity.damage(damageSource, (float)k)) {
						if (blockHitResult.entity instanceof LivingEntity) {
							LivingEntity livingEntity = (LivingEntity)blockHitResult.entity;
							if (!this.world.isClient) {
								livingEntity.setStuckArrows(livingEntity.getStuckArrows() + 1);
							}

							if (this.punch > 0) {
								float l = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
								if (l > 0.0F) {
									blockHitResult.entity.addVelocity(this.velocityX * (double)this.punch * 0.6F / (double)l, 0.1, this.velocityZ * (double)this.punch * 0.6F / (double)l);
								}
							}

							if (this.owner instanceof LivingEntity) {
								EnchantmentHelper.onUserDamaged(livingEntity, this.owner);
								EnchantmentHelper.onTargetDamaged((LivingEntity)this.owner, livingEntity);
							}

							if (this.owner != null
								&& blockHitResult.entity != this.owner
								&& blockHitResult.entity instanceof PlayerEntity
								&& this.owner instanceof ServerPlayerEntity) {
								((ServerPlayerEntity)this.owner).networkHandler.sendPacket(new GameStateChangeS2CPacket(6, 0.0F));
							}
						}

						this.playSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
						if (!(blockHitResult.entity instanceof EndermanEntity)) {
							this.remove();
						}
					} else {
						this.velocityX *= -0.1F;
						this.velocityY *= -0.1F;
						this.velocityZ *= -0.1F;
						this.yaw += 180.0F;
						this.prevYaw += 180.0F;
						this.field_4022 = 0;
					}
				} else {
					BlockPos blockPos2 = blockHitResult.getBlockPos();
					this.blockX = blockPos2.getX();
					this.blockY = blockPos2.getY();
					this.blockZ = blockPos2.getZ();
					BlockState blockState2 = this.world.getBlockState(blockPos2);
					this.block = blockState2.getBlock();
					this.blockData = this.block.getData(blockState2);
					this.velocityX = (double)((float)(blockHitResult.pos.x - this.x));
					this.velocityY = (double)((float)(blockHitResult.pos.y - this.y));
					this.velocityZ = (double)((float)(blockHitResult.pos.z - this.z));
					float m = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
					this.x = this.x - this.velocityX / (double)m * 0.05F;
					this.y = this.y - this.velocityY / (double)m * 0.05F;
					this.z = this.z - this.velocityZ / (double)m * 0.05F;
					this.playSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.shake = 7;
					this.setCritical(false);
					if (this.block.getMaterial() != Material.AIR) {
						this.block.onEntityCollision(this.world, blockPos2, blockState2, this);
					}
				}
			}

			if (this.isCritical()) {
				for (int n = 0; n < 4; n++) {
					this.world
						.addParticle(
							ParticleType.CRIT,
							this.x + this.velocityX * (double)n / 4.0,
							this.y + this.velocityY * (double)n / 4.0,
							this.z + this.velocityZ * (double)n / 4.0,
							-this.velocityX,
							-this.velocityY + 0.2,
							-this.velocityZ
						);
				}
			}

			this.x = this.x + this.velocityX;
			this.y = this.y + this.velocityY;
			this.z = this.z + this.velocityZ;
			float o = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
			this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)o) * 180.0 / (float) Math.PI);

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
			float p = 0.99F;
			float q = 0.05F;
			if (this.isTouchingWater()) {
				for (int r = 0; r < 4; r++) {
					float s = 0.25F;
					this.world
						.addParticle(
							ParticleType.BUBBLE,
							this.x - this.velocityX * (double)s,
							this.y - this.velocityY * (double)s,
							this.z - this.velocityZ * (double)s,
							this.velocityX,
							this.velocityY,
							this.velocityZ
						);
				}

				p = 0.6F;
			}

			if (this.tickFire()) {
				this.extinguish();
			}

			this.velocityX *= (double)p;
			this.velocityY *= (double)p;
			this.velocityZ *= (double)p;
			this.velocityY -= (double)q;
			this.updatePosition(this.x, this.y, this.z);
			this.checkBlockCollision();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("xTile", (short)this.blockX);
		nbt.putShort("yTile", (short)this.blockY);
		nbt.putShort("zTile", (short)this.blockZ);
		nbt.putShort("life", (short)this.life);
		Identifier identifier = Block.REGISTRY.getIdentifier(this.block);
		nbt.putString("inTile", identifier == null ? "" : identifier.toString());
		nbt.putByte("inData", (byte)this.blockData);
		nbt.putByte("shake", (byte)this.shake);
		nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
		nbt.putByte("pickup", (byte)this.pickup);
		nbt.putDouble("damage", this.damage);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.blockX = nbt.getShort("xTile");
		this.blockY = nbt.getShort("yTile");
		this.blockZ = nbt.getShort("zTile");
		this.life = nbt.getShort("life");
		if (nbt.contains("inTile", 8)) {
			this.block = Block.get(nbt.getString("inTile"));
		} else {
			this.block = Block.getById(nbt.getByte("inTile") & 255);
		}

		this.blockData = nbt.getByte("inData") & 255;
		this.shake = nbt.getByte("shake") & 255;
		this.inGround = nbt.getByte("inGround") == 1;
		if (nbt.contains("damage", 99)) {
			this.damage = nbt.getDouble("damage");
		}

		if (nbt.contains("pickup", 99)) {
			this.pickup = nbt.getByte("pickup");
		} else if (nbt.contains("player", 99)) {
			this.pickup = nbt.getBoolean("player") ? 1 : 0;
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient && this.inGround && this.shake <= 0) {
			boolean bl = this.pickup == 1 || this.pickup == 2 && player.abilities.creativeMode;
			if (this.pickup == 1 && !player.inventory.insertStack(new ItemStack(Items.ARROW, 1))) {
				bl = false;
			}

			if (bl) {
				this.playSound("random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.sendPickup(this, 1);
				this.remove();
			}
		}
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setPunch(int punch) {
		this.punch = punch;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	public void setCritical(boolean critical) {
		byte b = this.dataTracker.getByte(16);
		if (critical) {
			this.dataTracker.setProperty(16, (byte)(b | 1));
		} else {
			this.dataTracker.setProperty(16, (byte)(b & -2));
		}
	}

	public boolean isCritical() {
		byte b = this.dataTracker.getByte(16);
		return (b & 1) != 0;
	}
}
