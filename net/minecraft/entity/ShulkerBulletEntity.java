package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class ShulkerBulletEntity extends Entity {
	private LivingEntity owner;
	private Entity target;
	@Nullable
	private Direction direction;
	private int stepCount;
	private double targetX;
	private double targetY;
	private double targetZ;
	@Nullable
	private UUID ownerUuid;
	private BlockPos ownerPos;
	@Nullable
	private UUID targetUuid;
	private BlockPos targetPos;

	public ShulkerBulletEntity(World world) {
		super(world);
		this.setBounds(0.3125F, 0.3125F);
		this.noClip = true;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	public ShulkerBulletEntity(World world, double d, double e, double f, double g, double h, double i) {
		this(world);
		this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	public ShulkerBulletEntity(World world, LivingEntity livingEntity, Entity entity, Direction.Axis axis) {
		this(world);
		this.owner = livingEntity;
		BlockPos blockPos = new BlockPos(livingEntity);
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.5;
		double f = (double)blockPos.getZ() + 0.5;
		this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.target = entity;
		this.direction = Direction.UP;
		this.method_13288(axis);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.owner != null) {
			BlockPos blockPos = new BlockPos(this.owner);
			NbtCompound nbtCompound = NbtHelper.fromUuid(this.owner.getUuid());
			nbtCompound.putInt("X", blockPos.getX());
			nbtCompound.putInt("Y", blockPos.getY());
			nbtCompound.putInt("Z", blockPos.getZ());
			nbt.put("Owner", nbtCompound);
		}

		if (this.target != null) {
			BlockPos blockPos2 = new BlockPos(this.target);
			NbtCompound nbtCompound2 = NbtHelper.fromUuid(this.target.getUuid());
			nbtCompound2.putInt("X", blockPos2.getX());
			nbtCompound2.putInt("Y", blockPos2.getY());
			nbtCompound2.putInt("Z", blockPos2.getZ());
			nbt.put("Target", nbtCompound2);
		}

		if (this.direction != null) {
			nbt.putInt("Dir", this.direction.getId());
		}

		nbt.putInt("Steps", this.stepCount);
		nbt.putDouble("TXD", this.targetX);
		nbt.putDouble("TYD", this.targetY);
		nbt.putDouble("TZD", this.targetZ);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.stepCount = nbt.getInt("Steps");
		this.targetX = nbt.getDouble("TXD");
		this.targetY = nbt.getDouble("TYD");
		this.targetZ = nbt.getDouble("TZD");
		if (nbt.contains("Dir", 99)) {
			this.direction = Direction.getById(nbt.getInt("Dir"));
		}

		if (nbt.contains("Owner", 10)) {
			NbtCompound nbtCompound = nbt.getCompound("Owner");
			this.ownerUuid = NbtHelper.toUuid(nbtCompound);
			this.ownerPos = new BlockPos(nbtCompound.getInt("X"), nbtCompound.getInt("Y"), nbtCompound.getInt("Z"));
		}

		if (nbt.contains("Target", 10)) {
			NbtCompound nbtCompound2 = nbt.getCompound("Target");
			this.targetUuid = NbtHelper.toUuid(nbtCompound2);
			this.targetPos = new BlockPos(nbtCompound2.getInt("X"), nbtCompound2.getInt("Y"), nbtCompound2.getInt("Z"));
		}
	}

	@Override
	protected void initDataTracker() {
	}

	private void setDirection(@Nullable Direction direction) {
		this.direction = direction;
	}

	private void method_13288(@Nullable Direction.Axis axis) {
		double d = 0.5;
		BlockPos blockPos;
		if (this.target == null) {
			blockPos = new BlockPos(this).down();
		} else {
			d = (double)this.target.height * 0.5;
			blockPos = new BlockPos(this.target.x, this.target.y + d, this.target.z);
		}

		double e = (double)blockPos.getX() + 0.5;
		double f = (double)blockPos.getY() + d;
		double g = (double)blockPos.getZ() + 0.5;
		Direction direction = null;
		if (blockPos.squaredDistanceToCenter(this.x, this.y, this.z) >= 4.0) {
			BlockPos blockPos3 = new BlockPos(this);
			List<Direction> list = Lists.newArrayList();
			if (axis != Direction.Axis.X) {
				if (blockPos3.getX() < blockPos.getX() && this.world.isAir(blockPos3.east())) {
					list.add(Direction.EAST);
				} else if (blockPos3.getX() > blockPos.getX() && this.world.isAir(blockPos3.west())) {
					list.add(Direction.WEST);
				}
			}

			if (axis != Direction.Axis.Y) {
				if (blockPos3.getY() < blockPos.getY() && this.world.isAir(blockPos3.up())) {
					list.add(Direction.UP);
				} else if (blockPos3.getY() > blockPos.getY() && this.world.isAir(blockPos3.down())) {
					list.add(Direction.DOWN);
				}
			}

			if (axis != Direction.Axis.Z) {
				if (blockPos3.getZ() < blockPos.getZ() && this.world.isAir(blockPos3.south())) {
					list.add(Direction.SOUTH);
				} else if (blockPos3.getZ() > blockPos.getZ() && this.world.isAir(blockPos3.north())) {
					list.add(Direction.NORTH);
				}
			}

			direction = Direction.random(this.random);
			if (list.isEmpty()) {
				for (int i = 5; !this.world.isAir(blockPos3.offset(direction)) && i > 0; i--) {
					direction = Direction.random(this.random);
				}
			} else {
				direction = (Direction)list.get(this.random.nextInt(list.size()));
			}

			e = this.x + (double)direction.getOffsetX();
			f = this.y + (double)direction.getOffsetY();
			g = this.z + (double)direction.getOffsetZ();
		}

		this.setDirection(direction);
		double h = e - this.x;
		double j = f - this.y;
		double k = g - this.z;
		double l = (double)MathHelper.sqrt(h * h + j * j + k * k);
		if (l == 0.0) {
			this.targetX = 0.0;
			this.targetY = 0.0;
			this.targetZ = 0.0;
		} else {
			this.targetX = h / l * 0.15;
			this.targetY = j / l * 0.15;
			this.targetZ = k / l * 0.15;
		}

		this.velocityDirty = true;
		this.stepCount = 10 + this.random.nextInt(5) * 10;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.world.getGlobalDifficulty() == Difficulty.PEACEFUL) {
			this.remove();
		} else {
			super.tick();
			if (!this.world.isClient) {
				if (this.target == null && this.targetUuid != null) {
					for (LivingEntity livingEntity : this.world.getEntitiesInBox(LivingEntity.class, new Box(this.targetPos.add(-2, -2, -2), this.targetPos.add(2, 2, 2)))) {
						if (livingEntity.getUuid().equals(this.targetUuid)) {
							this.target = livingEntity;
							break;
						}
					}

					this.targetUuid = null;
				}

				if (this.owner == null && this.ownerUuid != null) {
					for (LivingEntity livingEntity2 : this.world.getEntitiesInBox(LivingEntity.class, new Box(this.ownerPos.add(-2, -2, -2), this.ownerPos.add(2, 2, 2)))) {
						if (livingEntity2.getUuid().equals(this.ownerUuid)) {
							this.owner = livingEntity2;
							break;
						}
					}

					this.ownerUuid = null;
				}

				if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator()) {
					this.velocityY -= 0.04;
				} else {
					this.targetX = MathHelper.clamp(this.targetX * 1.025, -1.0, 1.0);
					this.targetY = MathHelper.clamp(this.targetY * 1.025, -1.0, 1.0);
					this.targetZ = MathHelper.clamp(this.targetZ * 1.025, -1.0, 1.0);
					this.velocityX = this.velocityX + (this.targetX - this.velocityX) * 0.2;
					this.velocityY = this.velocityY + (this.targetY - this.velocityY) * 0.2;
					this.velocityZ = this.velocityZ + (this.targetZ - this.velocityZ) * 0.2;
				}

				BlockHitResult blockHitResult = ProjectileUtil.method_13286(this, true, false, this.owner);
				if (blockHitResult != null) {
					this.onCollision(blockHitResult);
				}
			}

			this.updatePosition(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			ProjectileUtil.setRotationFromVelocity(this, 0.5F);
			if (this.world.isClient) {
				this.world.addParticle(ParticleType.END_ROD, this.x - this.velocityX, this.y - this.velocityY + 0.15, this.z - this.velocityZ, 0.0, 0.0, 0.0);
			} else if (this.target != null && !this.target.removed) {
				if (this.stepCount > 0) {
					this.stepCount--;
					if (this.stepCount == 0) {
						this.method_13288(this.direction == null ? null : this.direction.getAxis());
					}
				}

				if (this.direction != null) {
					BlockPos blockPos = new BlockPos(this);
					Direction.Axis axis = this.direction.getAxis();
					if (this.world.renderAsNormalBlock(blockPos.offset(this.direction), false)) {
						this.method_13288(axis);
					} else {
						BlockPos blockPos2 = new BlockPos(this.target);
						if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX()
							|| axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ()
							|| axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
							this.method_13288(axis);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < 16384.0;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 15728880;
	}

	protected void onCollision(BlockHitResult result) {
		if (result.entity == null) {
			((ServerWorld)this.world).addParticle(ParticleType.LARGE_EXPLOSION, this.x, this.y, this.z, 2, 0.2, 0.2, 0.2, 0.0);
			this.playSound(Sounds.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
		} else {
			boolean bl = result.entity.damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 4.0F);
			if (bl) {
				this.dealDamage(this.owner, result.entity);
				if (result.entity instanceof LivingEntity) {
					((LivingEntity)result.entity).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 200));
				}
			}
		}

		this.remove();
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (!this.world.isClient) {
			this.playSound(Sounds.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
			((ServerWorld)this.world).addParticle(ParticleType.CRIT, this.x, this.y, this.z, 15, 0.2, 0.2, 0.2, 0.0);
			this.remove();
		}

		return true;
	}
}
