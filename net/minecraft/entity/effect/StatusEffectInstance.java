package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final StatusEffect statusEffect;
	private int duration;
	private int amplifier;
	private boolean splash;
	private boolean ambient;
	private boolean permanent;
	private boolean showParticles;

	public StatusEffectInstance(StatusEffect statusEffect) {
		this(statusEffect, 0, 0);
	}

	public StatusEffectInstance(StatusEffect statusEffect, int i) {
		this(statusEffect, i, 0);
	}

	public StatusEffectInstance(StatusEffect statusEffect, int i, int j) {
		this(statusEffect, i, j, false, true);
	}

	public StatusEffectInstance(StatusEffect statusEffect, int i, int j, boolean bl, boolean bl2) {
		this.statusEffect = statusEffect;
		this.duration = i;
		this.amplifier = j;
		this.ambient = bl;
		this.showParticles = bl2;
	}

	public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
		this.statusEffect = statusEffectInstance.statusEffect;
		this.duration = statusEffectInstance.duration;
		this.amplifier = statusEffectInstance.amplifier;
		this.ambient = statusEffectInstance.ambient;
		this.showParticles = statusEffectInstance.showParticles;
	}

	public void setFrom(StatusEffectInstance instance) {
		if (this.statusEffect != instance.statusEffect) {
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

	public StatusEffect getStatusEffect() {
		return this.statusEffect;
	}

	public int getDuration() {
		return this.duration;
	}

	public int getAmplifier() {
		return this.amplifier;
	}

	public boolean isAmbient() {
		return this.ambient;
	}

	public boolean shouldShowParticles() {
		return this.showParticles;
	}

	public boolean method_6093(LivingEntity livingEntity) {
		if (this.duration > 0) {
			if (this.statusEffect.canApplyUpdateEffect(this.duration, this.amplifier)) {
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
			this.statusEffect.method_6087(livingEntity, this.amplifier);
		}
	}

	public String getTranslationKey() {
		return this.statusEffect.getTranslationKey();
	}

	public String toString() {
		String string;
		if (this.amplifier > 0) {
			string = this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
		} else {
			string = this.getTranslationKey() + ", Duration: " + this.duration;
		}

		if (this.splash) {
			string = string + ", Splash: true";
		}

		if (!this.showParticles) {
			string = string + ", Particles: false";
		}

		return string;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof StatusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)object;
			return this.duration == statusEffectInstance.duration
				&& this.amplifier == statusEffectInstance.amplifier
				&& this.splash == statusEffectInstance.splash
				&& this.ambient == statusEffectInstance.ambient
				&& this.statusEffect.equals(statusEffectInstance.statusEffect);
		}
	}

	public int hashCode() {
		int i = this.statusEffect.hashCode();
		i = 31 * i + this.duration;
		i = 31 * i + this.amplifier;
		i = 31 * i + (this.splash ? 1 : 0);
		return 31 * i + (this.ambient ? 1 : 0);
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putByte("Id", (byte)StatusEffect.getIndex(this.getStatusEffect()));
		nbt.putByte("Amplifier", (byte)this.getAmplifier());
		nbt.putInt("Duration", this.getDuration());
		nbt.putBoolean("Ambient", this.isAmbient());
		nbt.putBoolean("ShowParticles", this.shouldShowParticles());
		return nbt;
	}

	public static StatusEffectInstance fromNbt(NbtCompound nbt) {
		int i = nbt.getByte("Id");
		StatusEffect statusEffect = StatusEffect.byIndex(i);
		if (statusEffect == null) {
			return null;
		} else {
			int j = nbt.getByte("Amplifier");
			int k = nbt.getInt("Duration");
			boolean bl = nbt.getBoolean("Ambient");
			boolean bl2 = true;
			if (nbt.contains("ShowParticles", 1)) {
				bl2 = nbt.getBoolean("ShowParticles");
			}

			return new StatusEffectInstance(statusEffect, k, j, bl, bl2);
		}
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public boolean isPermanent() {
		return this.permanent;
	}

	public int compareTo(StatusEffectInstance statusEffectInstance) {
		int i = 32147;
		return (this.getDuration() <= 32147 || statusEffectInstance.getDuration() <= 32147) && (!this.isAmbient() || !statusEffectInstance.isAmbient())
			? ComparisonChain.start()
				.compare(this.isAmbient(), statusEffectInstance.isAmbient())
				.compare(this.getDuration(), statusEffectInstance.getDuration())
				.compare(this.getStatusEffect().getColor(), statusEffectInstance.getStatusEffect().getColor())
				.result()
			: ComparisonChain.start()
				.compare(this.isAmbient(), statusEffectInstance.isAmbient())
				.compare(this.getStatusEffect().getColor(), statusEffectInstance.getStatusEffect().getColor())
				.result();
	}
}
