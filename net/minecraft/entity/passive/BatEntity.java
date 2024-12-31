package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BatEntity extends AmbientEntity {
	private BlockPos field_5365;

	public BatEntity(World world) {
		super(world);
		this.setBounds(0.5F, 0.9F);
		this.setRoosting(true);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, new Byte((byte)0));
	}

	@Override
	protected float getSoundVolume() {
		return 0.1F;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 0.95F;
	}

	@Override
	protected String getAmbientSound() {
		return this.isRoosting() && this.random.nextInt(4) != 0 ? null : "mob.bat.idle";
	}

	@Override
	protected String getHurtSound() {
		return "mob.bat.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.bat.death";
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushAway(Entity entity) {
	}

	@Override
	protected void tickCramming() {
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(6.0);
	}

	public boolean isRoosting() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	public void setRoosting(boolean bl) {
		byte b = this.dataTracker.getByte(16);
		if (bl) {
			this.dataTracker.setProperty(16, (byte)(b | 1));
		} else {
			this.dataTracker.setProperty(16, (byte)(b & -2));
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isRoosting()) {
			this.velocityX = this.velocityY = this.velocityZ = 0.0;
			this.y = (double)MathHelper.floor(this.y) + 1.0 - (double)this.height;
		} else {
			this.velocityY *= 0.6F;
		}
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		BlockPos blockPos = new BlockPos(this);
		BlockPos blockPos2 = blockPos.up();
		if (this.isRoosting()) {
			if (!this.world.getBlockState(blockPos2).getBlock().isFullCube()) {
				this.setRoosting(false);
				this.world.syncWorldEvent(null, 1015, blockPos, 0);
			} else {
				if (this.random.nextInt(200) == 0) {
					this.headYaw = (float)this.random.nextInt(360);
				}

				if (this.world.getClosestPlayer(this, 4.0) != null) {
					this.setRoosting(false);
					this.world.syncWorldEvent(null, 1015, blockPos, 0);
				}
			}
		} else {
			if (this.field_5365 != null && (!this.world.isAir(this.field_5365) || this.field_5365.getY() < 1)) {
				this.field_5365 = null;
			}

			if (this.field_5365 == null
				|| this.random.nextInt(30) == 0
				|| this.field_5365.squaredDistanceTo((double)((int)this.x), (double)((int)this.y), (double)((int)this.z)) < 4.0) {
				this.field_5365 = new BlockPos(
					(int)this.x + this.random.nextInt(7) - this.random.nextInt(7),
					(int)this.y + this.random.nextInt(6) - 2,
					(int)this.z + this.random.nextInt(7) - this.random.nextInt(7)
				);
			}

			double d = (double)this.field_5365.getX() + 0.5 - this.x;
			double e = (double)this.field_5365.getY() + 0.1 - this.y;
			double f = (double)this.field_5365.getZ() + 0.5 - this.z;
			this.velocityX = this.velocityX + (Math.signum(d) * 0.5 - this.velocityX) * 0.1F;
			this.velocityY = this.velocityY + (Math.signum(e) * 0.7F - this.velocityY) * 0.1F;
			this.velocityZ = this.velocityZ + (Math.signum(f) * 0.5 - this.velocityZ) * 0.1F;
			float g = (float)(MathHelper.atan2(this.velocityZ, this.velocityX) * 180.0 / (float) Math.PI) - 90.0F;
			float h = MathHelper.wrapDegrees(g - this.yaw);
			this.forwardSpeed = 0.5F;
			this.yaw += h;
			if (this.random.nextInt(100) == 0 && this.world.getBlockState(blockPos2).getBlock().isFullCube()) {
				this.setRoosting(true);
			}
		}
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, Block landedBlock, BlockPos landedPosition) {
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.world.isClient && this.isRoosting()) {
				this.setRoosting(false);
			}

			return super.damage(source, amount);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.setProperty(16, nbt.getByte("BatFlags"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("BatFlags", this.dataTracker.getByte(16));
	}

	@Override
	public boolean canSpawn() {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		if (blockPos.getY() >= this.world.getSeaLevel()) {
			return false;
		} else {
			int i = this.world.getLightLevelWithNeighbours(blockPos);
			int j = 4;
			if (this.method_11069(this.world.getCalenderInstance())) {
				j = 7;
			} else if (this.random.nextBoolean()) {
				return false;
			}

			return i > this.random.nextInt(j) ? false : super.canSpawn();
		}
	}

	private boolean method_11069(Calendar calendar) {
		return calendar.get(2) + 1 == 10 && calendar.get(5) >= 20 || calendar.get(2) + 1 == 11 && calendar.get(5) <= 3;
	}

	@Override
	public float getEyeHeight() {
		return this.height / 2.0F;
	}
}
