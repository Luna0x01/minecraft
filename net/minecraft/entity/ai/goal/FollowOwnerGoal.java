package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.class_3383;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RenderBlockView;

public class FollowOwnerGoal extends Goal {
	private final TameableEntity tameable;
	private LivingEntity owner;
	protected final RenderBlockView field_16856;
	private final double speed;
	private final EntityNavigation navigation;
	private int updateCountdownTicks;
	private final float maxDistance;
	private final float minDistance;
	private float field_14576;

	public FollowOwnerGoal(TameableEntity tameableEntity, double d, float f, float g) {
		this.tameable = tameableEntity;
		this.field_16856 = tameableEntity.world;
		this.speed = d;
		this.navigation = tameableEntity.getNavigation();
		this.minDistance = f;
		this.maxDistance = g;
		this.setCategoryBits(3);
		if (!(tameableEntity.getNavigation() instanceof MobNavigation) && !(tameableEntity.getNavigation() instanceof class_3383)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.tameable.getOwner();
		if (livingEntity == null) {
			return false;
		} else if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).isSpectator()) {
			return false;
		} else if (this.tameable.isSitting()) {
			return false;
		} else if (this.tameable.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
			return false;
		} else {
			this.owner = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.navigation.isIdle() && this.tameable.squaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance) && !this.tameable.isSitting();
	}

	@Override
	public void start() {
		this.updateCountdownTicks = 0;
		this.field_14576 = this.tameable.method_13075(LandType.WATER);
		this.tameable.method_13076(LandType.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		this.tameable.method_13076(LandType.WATER, this.field_14576);
	}

	@Override
	public void tick() {
		this.tameable.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameable.getLookPitchSpeed());
		if (!this.tameable.isSitting()) {
			if (--this.updateCountdownTicks <= 0) {
				this.updateCountdownTicks = 10;
				if (!this.navigation.startMovingTo(this.owner, this.speed)) {
					if (!this.tameable.isLeashed() && !this.tameable.hasMount()) {
						if (!(this.tameable.squaredDistanceTo(this.owner) < 144.0)) {
							int i = MathHelper.floor(this.owner.x) - 2;
							int j = MathHelper.floor(this.owner.z) - 2;
							int k = MathHelper.floor(this.owner.getBoundingBox().minY);

							for (int l = 0; l <= 4; l++) {
								for (int m = 0; m <= 4; m++) {
									if ((l < 1 || m < 1 || l > 3 || m > 3) && this.method_15080(i, j, k, l, m)) {
										this.tameable
											.refreshPositionAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + m) + 0.5F), this.tameable.yaw, this.tameable.pitch);
										this.navigation.stop();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean method_15080(int i, int j, int k, int l, int m) {
		BlockPos blockPos = new BlockPos(i + l, k - 1, j + m);
		BlockState blockState = this.field_16856.getBlockState(blockPos);
		return blockState.getRenderLayer(this.field_16856, blockPos, Direction.DOWN) == BlockRenderLayer.SOLID
			&& blockState.method_16859(this.tameable)
			&& this.field_16856.method_8579(blockPos.up())
			&& this.field_16856.method_8579(blockPos.up(2));
	}
}
