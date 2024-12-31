package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class class_2973 extends Goal {
	private final SkeletonEntity field_14584;
	private final double field_14585;
	private int field_14586;
	private final float field_14587;
	private int field_14588 = -1;
	private int field_14589;
	private boolean field_14590;
	private boolean field_14591;
	private int field_14592 = -1;

	public class_2973(SkeletonEntity skeletonEntity, double d, int i, float f) {
		this.field_14584 = skeletonEntity;
		this.field_14585 = d;
		this.field_14586 = i;
		this.field_14587 = f * f;
		this.setCategoryBits(3);
	}

	public void method_13101(int i) {
		this.field_14586 = i;
	}

	@Override
	public boolean canStart() {
		return this.field_14584.getTarget() == null ? false : this.method_13102();
	}

	protected boolean method_13102() {
		return this.field_14584.getMainHandStack() != null && this.field_14584.getMainHandStack().getItem() == Items.BOW;
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.field_14584.getNavigation().isIdle()) && this.method_13102();
	}

	@Override
	public void start() {
		super.start();
		this.field_14584.method_13237(true);
	}

	@Override
	public void stop() {
		super.start();
		this.field_14584.method_13237(false);
		this.field_14589 = 0;
		this.field_14588 = -1;
		this.field_14584.method_13053();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.field_14584.getTarget();
		if (livingEntity != null) {
			double d = this.field_14584.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
			boolean bl = this.field_14584.getVisibilityCache().canSee(livingEntity);
			boolean bl2 = this.field_14589 > 0;
			if (bl != bl2) {
				this.field_14589 = 0;
			}

			if (bl) {
				this.field_14589++;
			} else {
				this.field_14589--;
			}

			if (!(d > (double)this.field_14587) && this.field_14589 >= 20) {
				this.field_14584.getNavigation().stop();
				this.field_14592++;
			} else {
				this.field_14584.getNavigation().startMovingTo(livingEntity, this.field_14585);
				this.field_14592 = -1;
			}

			if (this.field_14592 >= 20) {
				if ((double)this.field_14584.getRandom().nextFloat() < 0.3) {
					this.field_14590 = !this.field_14590;
				}

				if ((double)this.field_14584.getRandom().nextFloat() < 0.3) {
					this.field_14591 = !this.field_14591;
				}

				this.field_14592 = 0;
			}

			if (this.field_14592 > -1) {
				if (d > (double)(this.field_14587 * 0.75F)) {
					this.field_14591 = false;
				} else if (d < (double)(this.field_14587 * 0.25F)) {
					this.field_14591 = true;
				}

				this.field_14584.getMotionHelper().method_13094(this.field_14591 ? -0.5F : 0.5F, this.field_14590 ? 0.5F : -0.5F);
				this.field_14584.lookAtEntity(livingEntity, 30.0F, 30.0F);
			} else {
				this.field_14584.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			}

			if (this.field_14584.method_13061()) {
				if (!bl && this.field_14589 < -60) {
					this.field_14584.method_13053();
				} else if (bl) {
					int i = this.field_14584.method_13066();
					if (i >= 20) {
						this.field_14584.method_13053();
						this.field_14584.rangedAttack(livingEntity, BowItem.method_11363(i));
						this.field_14588 = this.field_14586;
					}
				}
			} else if (--this.field_14588 <= 0 && this.field_14589 >= -60) {
				this.field_14584.method_13050(Hand.MAIN_HAND);
			}
		}
	}
}
