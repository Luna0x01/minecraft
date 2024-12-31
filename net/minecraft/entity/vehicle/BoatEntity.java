package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BoatEntity extends Entity {
	private boolean field_3877 = true;
	private double field_3879 = 0.07;
	private int field_3880;
	private double field_3881;
	private double field_3882;
	private double field_3883;
	private double boatYaw;
	private double boatX;
	private double field_3886;
	private double field_3887;
	private double field_3878;

	public BoatEntity(World world) {
		super(world);
		this.inanimate = true;
		this.setBounds(1.5F, 0.6F);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.track(17, new Integer(0));
		this.dataTracker.track(18, new Integer(1));
		this.dataTracker.track(19, new Float(0.0F));
	}

	@Override
	public Box getHardCollisionBox(Entity collidingEntity) {
		return collidingEntity.getBoundingBox();
	}

	@Override
	public Box getBox() {
		return this.getBoundingBox();
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	public BoatEntity(World world, double d, double e, double f) {
		this(world);
		this.updatePosition(d, e, f);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	@Override
	public double getMountedHeightOffset() {
		return -0.3;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!this.world.isClient && !this.removed) {
			if (this.rider != null && this.rider == source.getAttacker() && source instanceof ProjectileDamageSource) {
				return false;
			} else {
				this.setDamageWobbleSide(-this.getDamageWobbleSide());
				this.setBubbleWobbleTicks(10);
				this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
				this.scheduleVelocityUpdate();
				boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
				if (bl || this.getDamageWobbleStrength() > 40.0F) {
					if (this.rider != null) {
						this.rider.startRiding(this);
					}

					if (!bl && this.world.getGameRules().getBoolean("doEntityDrops")) {
						this.dropItem(Items.BOAT, 1, 0.0F);
					}

					this.remove();
				}

				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public void animateDamage() {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setBubbleWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0F);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		if (interpolate && this.rider != null) {
			this.prevX = this.x = x;
			this.prevY = this.y = y;
			this.prevZ = this.z = z;
			this.yaw = yaw;
			this.pitch = pitch;
			this.field_3880 = 0;
			this.updatePosition(x, y, z);
			this.velocityX = this.field_3886 = 0.0;
			this.velocityY = this.field_3887 = 0.0;
			this.velocityZ = this.field_3878 = 0.0;
		} else {
			if (this.field_3877) {
				this.field_3880 = interpolationSteps + 5;
			} else {
				double d = x - this.x;
				double e = y - this.y;
				double f = z - this.z;
				double g = d * d + e * e + f * f;
				if (!(g > 1.0)) {
					return;
				}

				this.field_3880 = 3;
			}

			this.field_3881 = x;
			this.field_3882 = y;
			this.field_3883 = z;
			this.boatYaw = (double)yaw;
			this.boatX = (double)pitch;
			this.velocityX = this.field_3886;
			this.velocityY = this.field_3887;
			this.velocityZ = this.field_3878;
		}
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.field_3886 = this.velocityX = x;
		this.field_3887 = this.velocityY = y;
		this.field_3878 = this.velocityZ = z;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getBubbleWobbleTicks() > 0) {
			this.setBubbleWobbleTicks(this.getBubbleWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		int i = 5;
		double d = 0.0;

		for (int j = 0; j < i; j++) {
			double e = this.getBoundingBox().minY + (this.getBoundingBox().maxY - this.getBoundingBox().minY) * (double)(j + 0) / (double)i - 0.125;
			double f = this.getBoundingBox().minY + (this.getBoundingBox().maxY - this.getBoundingBox().minY) * (double)(j + 1) / (double)i - 0.125;
			Box box = new Box(this.getBoundingBox().minX, e, this.getBoundingBox().minZ, this.getBoundingBox().maxX, f, this.getBoundingBox().maxZ);
			if (this.world.containsBlockWithMaterial(box, Material.WATER)) {
				d += 1.0 / (double)i;
			}
		}

		double g = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		if (g > 0.2975) {
			double h = Math.cos((double)this.yaw * Math.PI / 180.0);
			double k = Math.sin((double)this.yaw * Math.PI / 180.0);

			for (int l = 0; (double)l < 1.0 + g * 60.0; l++) {
				double m = (double)(this.random.nextFloat() * 2.0F - 1.0F);
				double n = (double)(this.random.nextInt(2) * 2 - 1) * 0.7;
				if (this.random.nextBoolean()) {
					double o = this.x - h * m * 0.8 + k * n;
					double p = this.z - k * m * 0.8 - h * n;
					this.world.addParticle(ParticleType.WATER, o, this.y - 0.125, p, this.velocityX, this.velocityY, this.velocityZ);
				} else {
					double q = this.x + h + k * m * 0.7;
					double r = this.z + k - h * m * 0.7;
					this.world.addParticle(ParticleType.WATER, q, this.y - 0.125, r, this.velocityX, this.velocityY, this.velocityZ);
				}
			}
		}

		if (this.world.isClient && this.field_3877) {
			if (this.field_3880 > 0) {
				double s = this.x + (this.field_3881 - this.x) / (double)this.field_3880;
				double t = this.y + (this.field_3882 - this.y) / (double)this.field_3880;
				double u = this.z + (this.field_3883 - this.z) / (double)this.field_3880;
				double v = MathHelper.wrapDegrees(this.boatYaw - (double)this.yaw);
				this.yaw = (float)((double)this.yaw + v / (double)this.field_3880);
				this.pitch = (float)((double)this.pitch + (this.boatX - (double)this.pitch) / (double)this.field_3880);
				this.field_3880--;
				this.updatePosition(s, t, u);
				this.setRotation(this.yaw, this.pitch);
			} else {
				double w = this.x + this.velocityX;
				double x = this.y + this.velocityY;
				double y = this.z + this.velocityZ;
				this.updatePosition(w, x, y);
				if (this.onGround) {
					this.velocityX *= 0.5;
					this.velocityY *= 0.5;
					this.velocityZ *= 0.5;
				}

				this.velocityX *= 0.99F;
				this.velocityY *= 0.95F;
				this.velocityZ *= 0.99F;
			}
		} else {
			if (d < 1.0) {
				double z = d * 2.0 - 1.0;
				this.velocityY += 0.04F * z;
			} else {
				if (this.velocityY < 0.0) {
					this.velocityY /= 2.0;
				}

				this.velocityY += 0.007F;
			}

			if (this.rider instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)this.rider;
				float aa = this.rider.yaw + -livingEntity.sidewaysSpeed * 90.0F;
				this.velocityX = this.velocityX + -Math.sin((double)(aa * (float) Math.PI / 180.0F)) * this.field_3879 * (double)livingEntity.forwardSpeed * 0.05F;
				this.velocityZ = this.velocityZ + Math.cos((double)(aa * (float) Math.PI / 180.0F)) * this.field_3879 * (double)livingEntity.forwardSpeed * 0.05F;
			}

			double ab = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (ab > 0.35) {
				double ac = 0.35 / ab;
				this.velocityX *= ac;
				this.velocityZ *= ac;
				ab = 0.35;
			}

			if (ab > g && this.field_3879 < 0.35) {
				this.field_3879 = this.field_3879 + (0.35 - this.field_3879) / 35.0;
				if (this.field_3879 > 0.35) {
					this.field_3879 = 0.35;
				}
			} else {
				this.field_3879 = this.field_3879 - (this.field_3879 - 0.07) / 35.0;
				if (this.field_3879 < 0.07) {
					this.field_3879 = 0.07;
				}
			}

			for (int ad = 0; ad < 4; ad++) {
				int ae = MathHelper.floor(this.x + ((double)(ad % 2) - 0.5) * 0.8);
				int af = MathHelper.floor(this.z + ((double)(ad / 2) - 0.5) * 0.8);

				for (int ag = 0; ag < 2; ag++) {
					int ah = MathHelper.floor(this.y) + ag;
					BlockPos blockPos = new BlockPos(ae, ah, af);
					Block block = this.world.getBlockState(blockPos).getBlock();
					if (block == Blocks.SNOW_LAYER) {
						this.world.setAir(blockPos);
						this.horizontalCollision = false;
					} else if (block == Blocks.LILY_PAD) {
						this.world.removeBlock(blockPos, true);
						this.horizontalCollision = false;
					}
				}
			}

			if (this.onGround) {
				this.velocityX *= 0.5;
				this.velocityY *= 0.5;
				this.velocityZ *= 0.5;
			}

			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (!this.horizontalCollision || !(g > 0.2975)) {
				this.velocityX *= 0.99F;
				this.velocityY *= 0.95F;
				this.velocityZ *= 0.99F;
			} else if (!this.world.isClient && !this.removed) {
				this.remove();
				if (this.world.getGameRules().getBoolean("doEntityDrops")) {
					for (int ai = 0; ai < 3; ai++) {
						this.dropItem(Item.fromBlock(Blocks.PLANKS), 1, 0.0F);
					}

					for (int aj = 0; aj < 2; aj++) {
						this.dropItem(Items.STICK, 1, 0.0F);
					}
				}
			}

			this.pitch = 0.0F;
			double ak = (double)this.yaw;
			double al = this.prevX - this.x;
			double am = this.prevZ - this.z;
			if (al * al + am * am > 0.001) {
				ak = (double)((float)(MathHelper.atan2(am, al) * 180.0 / Math.PI));
			}

			double an = MathHelper.wrapDegrees(ak - (double)this.yaw);
			if (an > 20.0) {
				an = 20.0;
			}

			if (an < -20.0) {
				an = -20.0;
			}

			this.yaw = (float)((double)this.yaw + an);
			this.setRotation(this.yaw, this.pitch);
			if (!this.world.isClient) {
				List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F));
				if (list != null && !list.isEmpty()) {
					for (int ao = 0; ao < list.size(); ao++) {
						Entity entity = (Entity)list.get(ao);
						if (entity != this.rider && entity.isPushable() && entity instanceof BoatEntity) {
							entity.pushAwayFrom(this);
						}
					}
				}

				if (this.rider != null && this.rider.removed) {
					this.rider = null;
				}
			}
		}
	}

	@Override
	public void updatePassengerPosition() {
		if (this.rider != null) {
			double d = Math.cos((double)this.yaw * Math.PI / 180.0) * 0.4;
			double e = Math.sin((double)this.yaw * Math.PI / 180.0) * 0.4;
			this.rider.updatePosition(this.x + d, this.y + this.getMountedHeightOffset() + this.rider.getHeightOffset(), this.z + e);
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		if (this.rider != null && this.rider instanceof PlayerEntity && this.rider != player) {
			return true;
		} else {
			if (!this.world.isClient) {
				player.startRiding(this);
			}

			return true;
		}
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, Block landedBlock, BlockPos landedPosition) {
		if (onGround) {
			if (this.fallDistance > 3.0F) {
				this.handleFallDamage(this.fallDistance, 1.0F);
				if (!this.world.isClient && !this.removed) {
					this.remove();
					if (this.world.getGameRules().getBoolean("doEntityDrops")) {
						for (int i = 0; i < 3; i++) {
							this.dropItem(Item.fromBlock(Blocks.PLANKS), 1, 0.0F);
						}

						for (int j = 0; j < 2; j++) {
							this.dropItem(Items.STICK, 1, 0.0F);
						}
					}
				}

				this.fallDistance = 0.0F;
			}
		} else if (this.world.getBlockState(new BlockPos(this).down()).getBlock().getMaterial() != Material.WATER && heightDifference < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - heightDifference);
		}
	}

	public void setDamageWobbleStrength(float wobbleStrength) {
		this.dataTracker.setProperty(19, wobbleStrength);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.getFloat(19);
	}

	public void setBubbleWobbleTicks(int wobbleTicks) {
		this.dataTracker.setProperty(17, wobbleTicks);
	}

	public int getBubbleWobbleTicks() {
		return this.dataTracker.getInt(17);
	}

	public void setDamageWobbleSide(int side) {
		this.dataTracker.setProperty(18, side);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.getInt(18);
	}

	public void method_3052(boolean bl) {
		this.field_3877 = bl;
	}
}
