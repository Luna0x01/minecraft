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
	private boolean field_16688;

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
		this(statusEffect, i, j, bl, bl2, bl2);
	}

	public StatusEffectInstance(StatusEffect statusEffect, int i, int j, boolean bl, boolean bl2, boolean bl3) {
		this.statusEffect = statusEffect;
		this.duration = i;
		this.amplifier = j;
		this.ambient = bl;
		this.showParticles = bl2;
		this.field_16688 = bl3;
	}

	public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
		this.statusEffect = statusEffectInstance.statusEffect;
		this.duration = statusEffectInstance.duration;
		this.amplifier = statusEffectInstance.amplifier;
		this.ambient = statusEffectInstance.ambient;
		this.showParticles = statusEffectInstance.showParticles;
		this.field_16688 = statusEffectInstance.field_16688;
	}

	public boolean method_15551(StatusEffectInstance statusEffectInstance) {
		if (this.statusEffect != statusEffectInstance.statusEffect) {
			LOGGER.warn("This method should only be called for matching effects!");
		}

		boolean bl = false;
		if (statusEffectInstance.amplifier > this.amplifier) {
			this.amplifier = statusEffectInstance.amplifier;
			this.duration = statusEffectInstance.duration;
			bl = true;
		} else if (statusEffectInstance.amplifier == this.amplifier && this.duration < statusEffectInstance.duration) {
			this.duration = statusEffectInstance.duration;
			bl = true;
		}

		if (!statusEffectInstance.ambient && this.ambient || bl) {
			this.ambient = statusEffectInstance.ambient;
			bl = true;
		}

		if (statusEffectInstance.showParticles != this.showParticles) {
			this.showParticles = statusEffectInstance.showParticles;
			bl = true;
		}

		if (statusEffectInstance.field_16688 != this.field_16688) {
			this.field_16688 = statusEffectInstance.field_16688;
			bl = true;
		}

		return bl;
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

	public boolean method_15552() {
		return this.field_16688;
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

		if (!this.field_16688) {
			string = string + ", Show Icon: false";
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
		nbt.putBoolean("ShowIcon", this.method_15552());
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

			boolean bl3 = bl2;
			if (nbt.contains("ShowIcon", 1)) {
				bl3 = nbt.getBoolean("ShowIcon");
			}

			return new StatusEffectInstance(statusEffect, k, j < 0 ? 0 : j, bl, bl2, bl3);
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
