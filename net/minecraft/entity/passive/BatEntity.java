package net.minecraft.entity.passive;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BatEntity extends AmbientEntity {
	private static final TrackedData<Byte> field_14610 = DataTracker.registerData(BatEntity.class, TrackedDataHandlerRegistry.BYTE);
	private BlockPos field_5365;

	public BatEntity(World world) {
		super(world);
		this.setBounds(0.5F, 0.9F);
		this.setRoosting(true);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14610, (byte)0);
	}

	@Override
	protected float getSoundVolume() {
		return 0.1F;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 0.95F;
	}

	@Nullable
	@Override
	protected Sound ambientSound() {
		return this.isRoosting() && this.random.nextInt(4) != 0 ? null : Sounds.ENTITY_BAT_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_BAT_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_BAT_DEATH;
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
		return (this.dataTracker.get(field_14610) & 1) != 0;
	}

	public void setRoosting(boolean bl) {
		byte b = this.dataTracker.get(field_14610);
		if (bl) {
			this.dataTracker.set(field_14610, (byte)(b | 1));
		} else {
			this.dataTracker.set(field_14610, (byte)(b & -2));
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isRoosting()) {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
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
			if (this.world.getBlockState(blockPos2).method_11734()) {
				if (this.random.nextInt(200) == 0) {
					this.headYaw = (float)this.random.nextInt(360);
				}

				if (this.world.method_11490(this, 4.0) != null) {
					this.setRoosting(false);
					this.world.syncWorldEvent(null, 1025, blockPos, 0);
				}
			} else {
				this.setRoosting(false);
				this.world.syncWorldEvent(null, 1025, blockPos, 0);
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
			float g = (float)(MathHelper.atan2(this.velocityZ, this.velocityX) * 180.0F / (float)Math.PI) - 90.0F;
			float h = MathHelper.wrapDegrees(g - this.yaw);
			this.forwardSpeed = 0.5F;
			this.yaw += h;
			if (this.random.nextInt(100) == 0 && this.world.getBlockState(blockPos2).method_11734()) {
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
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Bat");
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.set(field_14610, nbt.getByte("BatFlags"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("BatFlags", this.dataTracker.get(field_14610));
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

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.BAT_ENTITIE;
	}
}
