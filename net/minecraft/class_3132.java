package net.minecraft;

import java.util.List;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

public class class_3132 extends Goal {
	public LlamaEntity llama;
	private double field_15479;
	private int field_15480;

	public class_3132(LlamaEntity llamaEntity, double d) {
		this.llama = llamaEntity;
		this.field_15479 = d;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.llama.isLeashed() && !this.llama.method_14030()) {
			List<LlamaEntity> list = this.llama.world.getEntitiesInBox(this.llama.getClass(), this.llama.getBoundingBox().expand(9.0, 4.0, 9.0));
			LlamaEntity llamaEntity = null;
			double d = Double.MAX_VALUE;

			for (LlamaEntity llamaEntity2 : list) {
				if (llamaEntity2.method_14030() && !llamaEntity2.method_14029()) {
					double e = this.llama.squaredDistanceTo(llamaEntity2);
					if (!(e > d)) {
						d = e;
						llamaEntity = llamaEntity2;
					}
				}
			}

			if (llamaEntity == null) {
				for (LlamaEntity llamaEntity3 : list) {
					if (llamaEntity3.isLeashed() && !llamaEntity3.method_14029()) {
						double f = this.llama.squaredDistanceTo(llamaEntity3);
						if (!(f > d)) {
							d = f;
							llamaEntity = llamaEntity3;
						}
					}
				}
			}

			if (llamaEntity == null) {
				return false;
			} else if (d < 4.0) {
				return false;
			} else if (!llamaEntity.isLeashed() && !this.method_13952(llamaEntity, 1)) {
				return false;
			} else {
				this.llama.method_14020(llamaEntity);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.llama.method_14030() && this.llama.method_14031().isAlive() && this.method_13952(this.llama, 0)) {
			double d = this.llama.squaredDistanceTo(this.llama.method_14031());
			if (d > 676.0) {
				if (this.field_15479 <= 3.0) {
					this.field_15479 *= 1.2;
					this.field_15480 = 40;
					return true;
				}

				if (this.field_15480 == 0) {
					return false;
				}
			}

			if (this.field_15480 > 0) {
				this.field_15480--;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void stop() {
		this.llama.method_14028();
		this.field_15479 = 2.1;
	}

	@Override
	public void tick() {
		if (this.llama.method_14030()) {
			LlamaEntity llamaEntity = this.llama.method_14031();
			double d = (double)this.llama.distanceTo(llamaEntity);
			float f = 2.0F;
			Vec3d vec3d = new Vec3d(llamaEntity.x - this.llama.x, llamaEntity.y - this.llama.y, llamaEntity.z - this.llama.z)
				.normalize()
				.multiply(Math.max(d - 2.0, 0.0));
			this.llama.getNavigation().startMovingTo(this.llama.x + vec3d.x, this.llama.y + vec3d.y, this.llama.z + vec3d.z, this.field_15479);
		}
	}

	private boolean method_13952(LlamaEntity llamaEntity, int i) {
		if (i > 8) {
			return false;
		} else if (llamaEntity.method_14030()) {
			return llamaEntity.method_14031().isLeashed() ? true : this.method_13952(llamaEntity.method_14031(), ++i);
		} else {
			return false;
		}
	}
}
