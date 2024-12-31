package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class class_3168 extends class_3162 {
	private static final TrackedData<Byte> field_15606 = DataTracker.registerData(class_3168.class, TrackedDataHandlerRegistry.BYTE);
	protected int field_15604;
	private class_3168.class_3169 field_15605 = class_3168.class_3169.NONE;

	public class_3168(World world) {
		super(world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15606, (byte)0);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.field_15604 = nbt.getInt("SpellTicks");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("SpellTicks", this.field_15604);
	}

	@Override
	public class_3162.class_3163 method_14123() {
		return this.method_14133() ? class_3162.class_3163.SPELLCASTING : class_3162.class_3163.CROSSED;
	}

	public boolean method_14133() {
		return this.world.isClient ? this.dataTracker.get(field_15606) > 0 : this.field_15604 > 0;
	}

	public void method_14130(class_3168.class_3169 arg) {
		this.field_15605 = arg;
		this.dataTracker.set(field_15606, (byte)arg.field_15613);
	}

	protected class_3168.class_3169 method_14134() {
		return !this.world.isClient ? this.field_15605 : class_3168.class_3169.method_14136(this.dataTracker.get(field_15606));
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if (this.field_15604 > 0) {
			this.field_15604--;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && this.method_14133()) {
			class_3168.class_3169 lv = this.method_14134();
			double d = lv.field_15614[0];
			double e = lv.field_15614[1];
			double f = lv.field_15614[2];
			float g = this.bodyYaw * (float) (Math.PI / 180.0) + MathHelper.cos((float)this.ticksAlive * 0.6662F) * 0.25F;
			float h = MathHelper.cos(g);
			float i = MathHelper.sin(g);
			this.world.addParticle(ParticleType.MOB_SPELL, this.x + (double)h * 0.6, this.y + 1.8, this.z + (double)i * 0.6, d, e, f);
			this.world.addParticle(ParticleType.MOB_SPELL, this.x - (double)h * 0.6, this.y + 1.8, this.z - (double)i * 0.6, d, e, f);
		}
	}

	protected int method_14135() {
		return this.field_15604;
	}

	protected abstract Sound method_14132();

	public abstract class class_3152 extends Goal {
		protected int field_15543;
		protected int field_15544;

		protected class_3152() {
		}

		@Override
		public boolean canStart() {
			if (class_3168.this.getTarget() == null) {
				return false;
			} else {
				return class_3168.this.method_14133() ? false : class_3168.this.ticksAlive >= this.field_15544;
			}
		}

		@Override
		public boolean shouldContinue() {
			return class_3168.this.getTarget() != null && this.field_15543 > 0;
		}

		@Override
		public void start() {
			this.field_15543 = this.method_14089();
			class_3168.this.field_15604 = this.method_14084();
			this.field_15544 = class_3168.this.ticksAlive + this.method_14085();
			Sound sound = this.method_14087();
			if (sound != null) {
				class_3168.this.playSound(sound, 1.0F, 1.0F);
			}

			class_3168.this.method_14130(this.method_14139());
		}

		@Override
		public void tick() {
			this.field_15543--;
			if (this.field_15543 == 0) {
				this.method_14086();
				class_3168.this.playSound(class_3168.this.method_14132(), 1.0F, 1.0F);
			}
		}

		protected abstract void method_14086();

		protected int method_14089() {
			return 20;
		}

		protected abstract int method_14084();

		protected abstract int method_14085();

		@Nullable
		protected abstract Sound method_14087();

		protected abstract class_3168.class_3169 method_14139();
	}

	public static enum class_3169 {
		NONE(0, 0.0, 0.0, 0.0),
		SUMMON_VEX(1, 0.7, 0.7, 0.8),
		FANGS(2, 0.4, 0.3, 0.35),
		WOLOLO(3, 0.7, 0.5, 0.2),
		DISAPPEAR(4, 0.3, 0.3, 0.8),
		BLINDNESS(5, 0.1, 0.1, 0.2);

		private final int field_15613;
		private final double[] field_15614;

		private class_3169(int j, double d, double e, double f) {
			this.field_15613 = j;
			this.field_15614 = new double[]{d, e, f};
		}

		public static class_3168.class_3169 method_14136(int i) {
			for (class_3168.class_3169 lv : values()) {
				if (i == lv.field_15613) {
					return lv;
				}
			}

			return NONE;
		}
	}

	public class class_3170 extends Goal {
		public class_3170() {
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			return class_3168.this.method_14135() > 0;
		}

		@Override
		public void start() {
			super.start();
			class_3168.this.navigation.stop();
		}

		@Override
		public void stop() {
			super.stop();
			class_3168.this.method_14130(class_3168.class_3169.NONE);
		}

		@Override
		public void tick() {
			if (class_3168.this.getTarget() != null) {
				class_3168.this.getLookControl().lookAt(class_3168.this.getTarget(), (float)class_3168.this.method_13081(), (float)class_3168.this.getLookPitchSpeed());
			}
		}
	}
}
