package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.class_3383;
import net.minecraft.entity.mob.MobEntity;

public class class_3373 extends Goal {
	private final MobEntity field_16525;
	private final Predicate<MobEntity> field_16855;
	private MobEntity field_16527;
	private final double field_16528;
	private final EntityNavigation field_16529;
	private int field_16530;
	private final float field_16531;
	private float field_16532;
	private final float field_16533;

	public class_3373(MobEntity mobEntity, double d, float f, float g) {
		this.field_16525 = mobEntity;
		this.field_16855 = mobEntity2 -> mobEntity2 != null && mobEntity.getClass() != mobEntity2.getClass();
		this.field_16528 = d;
		this.field_16529 = mobEntity.getNavigation();
		this.field_16531 = f;
		this.field_16533 = g;
		this.setCategoryBits(3);
		if (!(mobEntity.getNavigation() instanceof MobNavigation) && !(mobEntity.getNavigation() instanceof class_3383)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
		}
	}

	@Override
	public boolean canStart() {
		List<MobEntity> list = this.field_16525
			.world
			.method_16325(MobEntity.class, this.field_16525.getBoundingBox().expand((double)this.field_16533), this.field_16855);
		if (!list.isEmpty()) {
			for (MobEntity mobEntity : list) {
				if (!mobEntity.isInvisible()) {
					this.field_16527 = mobEntity;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {
		return this.field_16527 != null
			&& !this.field_16529.isIdle()
			&& this.field_16525.squaredDistanceTo(this.field_16527) > (double)(this.field_16531 * this.field_16531);
	}

	@Override
	public void start() {
		this.field_16530 = 0;
		this.field_16532 = this.field_16525.method_13075(LandType.WATER);
		this.field_16525.method_13076(LandType.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.field_16527 = null;
		this.field_16529.stop();
		this.field_16525.method_13076(LandType.WATER, this.field_16532);
	}

	@Override
	public void tick() {
		if (this.field_16527 != null && !this.field_16525.isLeashed()) {
			this.field_16525.getLookControl().lookAt(this.field_16527, 10.0F, (float)this.field_16525.getLookPitchSpeed());
			if (--this.field_16530 <= 0) {
				this.field_16530 = 10;
				double d = this.field_16525.x - this.field_16527.x;
				double e = this.field_16525.y - this.field_16527.y;
				double f = this.field_16525.z - this.field_16527.z;
				double g = d * d + e * e + f * f;
				if (!(g <= (double)(this.field_16531 * this.field_16531))) {
					this.field_16529.startMovingTo(this.field_16527, this.field_16528);
				} else {
					this.field_16529.stop();
					LookControl lookControl = this.field_16527.getLookControl();
					if (g <= (double)this.field_16531
						|| lookControl.getLookX() == this.field_16525.x && lookControl.getLookY() == this.field_16525.y && lookControl.getLookZ() == this.field_16525.z) {
						double h = this.field_16527.x - this.field_16525.x;
						double i = this.field_16527.z - this.field_16525.z;
						this.field_16529.startMovingTo(this.field_16525.x - h, this.field_16525.y, this.field_16525.z - i, this.field_16528);
					}
				}
			}
		}
	}
}
