package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance {
	private static final Logger LOGGER = LogManager.getLogger();
	private int effectId;
	private int duration;
	private int amplifier;
	private boolean splash;
	private boolean ambient;
	private boolean permanent;
	private boolean showParticles;

	public StatusEffectInstance(int i, int j) {
		this(i, j, 0);
	}

	public StatusEffectInstance(int i, int j, int k) {
		this(i, j, k, false, true);
	}

	public StatusEffectInstance(int i, int j, int k, boolean bl, boolean bl2) {
		this.effectId = i;
		this.duration = j;
		this.amplifier = k;
		this.ambient = bl;
		this.showParticles = bl2;
	}

	public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
		this.effectId = statusEffectInstance.effectId;
		this.duration = statusEffectInstance.duration;
		this.amplifier = statusEffectInstance.amplifier;
		this.ambient = statusEffectInstance.ambient;
		this.showParticles = statusEffectInstance.showParticles;
	}

	public void setFrom(StatusEffectInstance instance) {
		if (this.effectId != instance.effectId) {
			LOGGER.warn("This method should only be called for matching effects!");
		}

		if (instance.amplifier > this.amplifier) {
			this.amplifier = instance.amplifier;
			this.duration = instance.duration;
		} else if (instance.amplifier == this.amplifier && this.duration < instance.duration) {
			this.duration = instance.duration;
		} else if (!instance.ambient && this.ambient) {
			this.ambient = instance.ambient;
		}

		this.showParticles = instance.showParticles;
	}

	public int getEffectId() {
		return this.effectId;
	}

	public int getDuration() {
		return this.duration;
	}

	public int getAmplifier() {
		return this.amplifier;
	}

	public void setSplash(boolean splash) {
		this.splash = splash;
	}

	public boolean isAmbient() {
		return this.ambient;
	}

	public boolean shouldShowParticles() {
		return this.showParticles;
	}

	public boolean method_6093(LivingEntity livingEntity) {
		if (this.duration > 0) {
			if (StatusEffect.STATUS_EFFECTS[this.effectId].canApplyUpdateEffect(this.duration, this.amplifier)) {
				this.method_6094(livingEntity);
			}

			this.updateDuration();
		}

		return this.duration > 0;
	}

	private int updateDuration() {
		return --this.duration;
	}

	public void method_6094(LivingEntity livingEntity) {
		if (this.duration > 0) {
			StatusEffect.STATUS_EFFECTS[this.effectId].method_6087(livingEntity, this.amplifier);
		}
	}

	public String getTranslationKey() {
		return StatusEffect.STATUS_EFFECTS[this.effectId].getTranslationKey();
	}

	public int hashCode() {
		return this.effectId;
	}

	public String toString() {
		String string = "";
		if (this.getAmplifier() > 0) {
			string = this.getTranslationKey() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
		} else {
			string = this.getTranslationKey() + ", Duration: " + this.getDuration();
		}

		if (this.splash) {
			string = string + ", Splash: true";
		}

		if (!this.showParticles) {
			string = string + ", Particles: false";
		}

		return StatusEffect.STATUS_EFFECTS[this.effectId].method_2448() ? "(" + string + ")" : string;
	}

	public boolean equals(Object object) {
		if (!(object instanceof StatusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)object;
			return this.effectId == statusEffectInstance.effectId
				&& this.amplifier == statusEffectInstance.amplifier
				&& this.duration == statusEffectInstance.duration
				&& this.splash == statusEffectInstance.splash
				&& this.ambient == statusEffectInstance.ambient;
		}
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putByte("Id", (byte)this.getEffectId());
		nbt.putByte("Amplifier", (byte)this.getAmplifier());
		nbt.putInt("Duration", this.getDuration());
		nbt.putBoolean("Ambient", this.isAmbient());
		nbt.putBoolean("ShowParticles", this.shouldShowParticles());
		return nbt;
	}

	public static StatusEffectInstance fromNbt(NbtCompound nbt) {
		int i = nbt.getByte("Id");
		if (i >= 0 && i < StatusEffect.STATUS_EFFECTS.length && StatusEffect.STATUS_EFFECTS[i] != null) {
			int j = nbt.getByte("Amplifier");
			int k = nbt.getInt("Duration");
			boolean bl = nbt.getBoolean("Ambient");
			boolean bl2 = true;
			if (nbt.contains("ShowParticles", 1)) {
				bl2 = nbt.getBoolean("ShowParticles");
			}

			return new StatusEffectInstance(i, k, j, bl, bl2);
		} else {
			return null;
		}
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public boolean isPermanent() {
		return this.permanent;
	}
}
