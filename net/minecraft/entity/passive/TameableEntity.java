package net.minecraft.entity.passive;

import com.google.common.base.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.world.World;

public abstract class TameableEntity extends AnimalEntity implements Tameable {
	protected static final TrackedData<Byte> field_14566 = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> field_14567 = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	protected SitGoal sitGoal;

	public TameableEntity(World world) {
		super(world);
		this.onTamedChanged();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14566, (byte)0);
		this.dataTracker.startTracking(field_14567, Optional.absent());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.method_2719() == null) {
			nbt.putString("OwnerUUID", "");
		} else {
			nbt.putString("OwnerUUID", this.method_2719().toString());
		}

		nbt.putBoolean("Sitting", this.isSitting());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		String string;
		if (nbt.contains("OwnerUUID", 8)) {
			string = nbt.getString("OwnerUUID");
		} else {
			String string2 = nbt.getString("Owner");
			string = ServerConfigHandler.method_8204(this.getMinecraftServer(), string2);
		}

		if (!string.isEmpty()) {
			try {
				this.method_13092(UUID.fromString(string));
				this.setTamed(true);
			} catch (Throwable var4) {
				this.setTamed(false);
			}
		}

		if (this.sitGoal != null) {
			this.sitGoal.setEnabledWithOwner(nbt.getBoolean("Sitting"));
		}

		this.setSitting(nbt.getBoolean("Sitting"));
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return this.isTamed() && this.isOwner(playerEntity);
	}

	protected void showEmoteParticle(boolean positive) {
		ParticleType particleType = ParticleType.HEART;
		if (!positive) {
			particleType = ParticleType.SMOKE;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleType,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 7) {
			this.showEmoteParticle(true);
		} else if (status == 6) {
			this.showEmoteParticle(false);
		} else {
			super.handleStatus(status);
		}
	}

	public boolean isTamed() {
		return (this.dataTracker.get(field_14566) & 4) != 0;
	}

	public void setTamed(boolean tamed) {
		byte b = this.dataTracker.get(field_14566);
		if (tamed) {
			this.dataTracker.set(field_14566, (byte)(b | 4));
		} else {
			this.dataTracker.set(field_14566, (byte)(b & -5));
		}

		this.onTamedChanged();
	}

	protected void onTamedChanged() {
	}

	public boolean isSitting() {
		return (this.dataTracker.get(field_14566) & 1) != 0;
	}

	public void setSitting(boolean sitting) {
		byte b = this.dataTracker.get(field_14566);
		if (sitting) {
			this.dataTracker.set(field_14566, (byte)(b | 1));
		} else {
			this.dataTracker.set(field_14566, (byte)(b & -2));
		}
	}

	@Nullable
	@Override
	public UUID method_2719() {
		return (UUID)this.dataTracker.get(field_14567).orNull();
	}

	public void method_13092(@Nullable UUID uUID) {
		this.dataTracker.set(field_14567, Optional.fromNullable(uUID));
	}

	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uUID = this.method_2719();
			return uUID == null ? null : this.world.getPlayerByUuid(uUID);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean isOwner(LivingEntity entity) {
		return entity == this.getOwner();
	}

	public SitGoal getSitGoal() {
		return this.sitGoal;
	}

	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		return true;
	}

	@Override
	public AbstractTeam getScoreboardTeam() {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (livingEntity != null) {
				return livingEntity.getScoreboardTeam();
			}
		}

		return super.getScoreboardTeam();
	}

	@Override
	public boolean isTeammate(Entity other) {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (other == livingEntity) {
				return true;
			}

			if (livingEntity != null) {
				return livingEntity.isTeammate(other);
			}
		}

		return super.isTeammate(other);
	}

	@Override
	public void onKilled(DamageSource source) {
		if (!this.world.isClient && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof ServerPlayerEntity) {
			this.getOwner().sendMessage(this.getDamageTracker().getDeathMessage());
		}

		super.onKilled(source);
	}
}
