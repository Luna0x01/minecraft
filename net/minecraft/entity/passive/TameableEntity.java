package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.world.World;

public abstract class TameableEntity extends AnimalEntity implements Tameable {
	protected SitGoal sitGoal = new SitGoal(this);

	public TameableEntity(World world) {
		super(world);
		this.onTamedChanged();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, (byte)0);
		this.dataTracker.track(17, "");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.getOwnerId() == null) {
			nbt.putString("OwnerUUID", "");
		} else {
			nbt.putString("OwnerUUID", this.getOwnerId());
		}

		nbt.putBoolean("Sitting", this.isSitting());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		String string = "";
		if (nbt.contains("OwnerUUID", 8)) {
			string = nbt.getString("OwnerUUID");
		} else {
			String string2 = nbt.getString("Owner");
			string = ServerConfigHandler.getPlayerUuidByName(string2);
		}

		if (string.length() > 0) {
			this.method_2713(string);
			this.setTamed(true);
		}

		this.sitGoal.setEnabledWithOwner(nbt.getBoolean("Sitting"));
		this.setSitting(nbt.getBoolean("Sitting"));
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
		return (this.dataTracker.getByte(16) & 4) != 0;
	}

	public void setTamed(boolean tamed) {
		byte b = this.dataTracker.getByte(16);
		if (tamed) {
			this.dataTracker.setProperty(16, (byte)(b | 4));
		} else {
			this.dataTracker.setProperty(16, (byte)(b & -5));
		}

		this.onTamedChanged();
	}

	protected void onTamedChanged() {
	}

	public boolean isSitting() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	public void setSitting(boolean sitting) {
		byte b = this.dataTracker.getByte(16);
		if (sitting) {
			this.dataTracker.setProperty(16, (byte)(b | 1));
		} else {
			this.dataTracker.setProperty(16, (byte)(b & -2));
		}
	}

	@Override
	public String getOwnerId() {
		return this.dataTracker.getString(17);
	}

	public void method_2713(String string) {
		this.dataTracker.setProperty(17, string);
	}

	public LivingEntity getOwner() {
		try {
			UUID uUID = UUID.fromString(this.getOwnerId());
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
	public boolean isInSameTeam(LivingEntity entity) {
		if (this.isTamed()) {
			LivingEntity livingEntity = this.getOwner();
			if (entity == livingEntity) {
				return true;
			}

			if (livingEntity != null) {
				return livingEntity.isInSameTeam(entity);
			}
		}

		return super.isInSameTeam(entity);
	}

	@Override
	public void onKilled(DamageSource source) {
		if (!this.world.isClient
			&& this.world.getGameRules().getBoolean("showDeathMessages")
			&& this.hasCustomName()
			&& this.getOwner() instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)this.getOwner()).sendMessage(this.getDamageTracker().getDeathMessage());
		}

		super.onKilled(source);
	}
}
