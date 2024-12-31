package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.world.World;

public class EvokerFangsEntity extends Entity {
	private int warmup;
	private boolean startedAttack;
	private int ticksLeft = 22;
	private boolean playingAnimation;
	private LivingEntity owner;
	private UUID ownerUuid;

	public EvokerFangsEntity(World world) {
		super(EntityType.EVOKER_FANGS, world);
		this.setBounds(0.5F, 0.8F);
	}

	public EvokerFangsEntity(World world, double d, double e, double f, float g, int i, LivingEntity livingEntity) {
		this(world);
		this.warmup = i;
		this.setOwner(livingEntity);
		this.yaw = g * (180.0F / (float)Math.PI);
		this.updatePosition(d, e, f);
	}

	@Override
	protected void initDataTracker() {
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUuid = owner == null ? null : owner.getUuid();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			}
		}

		return this.owner;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.warmup = nbt.getInt("Warmup");
		if (nbt.containsUuid("OwnerUUID")) {
			this.ownerUuid = nbt.getUuid("OwnerUUID");
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("Warmup", this.warmup);
		if (this.ownerUuid != null) {
			nbt.putUuid("OwnerUUID", this.ownerUuid);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			if (this.playingAnimation) {
				this.ticksLeft--;
				if (this.ticksLeft == 14) {
					for (int i = 0; i < 12; i++) {
						double d = this.x + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.width * 0.5;
						double e = this.y + 0.05 + this.random.nextDouble();
						double f = this.z + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.width * 0.5;
						double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						double h = 0.3 + this.random.nextDouble() * 0.3;
						double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
						this.world.method_16343(class_4342.field_21382, d, e + 1.0, f, g, h, j);
					}
				}
			}
		} else if (--this.warmup < 0) {
			if (this.warmup == -8) {
				for (LivingEntity livingEntity : this.world.getEntitiesInBox(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2))) {
					this.damage(livingEntity);
				}
			}

			if (!this.startedAttack) {
				this.world.sendEntityStatus(this, (byte)4);
				this.startedAttack = true;
			}

			if (--this.ticksLeft < 0) {
				this.remove();
			}
		}
	}

	private void damage(LivingEntity target) {
		LivingEntity livingEntity = this.getOwner();
		if (target.isAlive() && !target.isInvulnerable() && target != livingEntity) {
			if (livingEntity == null) {
				target.damage(DamageSource.MAGIC, 6.0F);
			} else {
				if (livingEntity.isTeammate(target)) {
					return;
				}

				target.damage(DamageSource.magic(this, livingEntity), 6.0F);
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		super.handleStatus(status);
		if (status == 4) {
			this.playingAnimation = true;
			if (!this.isSilent()) {
				this.world
					.playSound(this.x, this.y, this.z, Sounds.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
			}
		}
	}

	public float getAnimationProgress(float tickDelta) {
		if (!this.playingAnimation) {
			return 0.0F;
		} else {
			int i = this.ticksLeft - 2;
			return i <= 0 ? 1.0F : 1.0F - ((float)i - tickDelta) / 20.0F;
		}
	}
}
