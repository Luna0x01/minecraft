package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FollowOwnerGoal extends Goal {
	private TameableEntity tameable;
	private LivingEntity owner;
	World world;
	private double speed;
	private EntityNavigation navigation;
	private int updateCountdownTicks;
	float maxDistance;
	float minDistance;
	private boolean field_3500;

	public FollowOwnerGoal(TameableEntity tameableEntity, double d, float f, float g) {
		this.tameable = tameableEntity;
		this.world = tameableEntity.world;
		this.speed = d;
		this.navigation = tameableEntity.getNavigation();
		this.minDistance = f;
		this.maxDistance = g;
		this.setCategoryBits(3);
		if (!(tameableEntity.getNavigation() instanceof MobNavigation)) {
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
		this.field_3500 = ((MobNavigation)this.tameable.getNavigation()).method_11032();
		((MobNavigation)this.tameable.getNavigation()).method_11027(false);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		((MobNavigation)this.tameable.getNavigation()).method_11027(true);
	}

	private boolean canTeleportTo(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		Block block = blockState.getBlock();
		return block == Blocks.AIR ? true : !block.renderAsNormalBlock();
	}

	@Override
	public void tick() {
		this.tameable.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameable.getLookPitchSpeed());
		if (!this.tameable.isSitting()) {
			if (--this.updateCountdownTicks <= 0) {
				this.updateCountdownTicks = 10;
				if (!this.navigation.startMovingTo(this.owner, this.speed)) {
					if (!this.tameable.isLeashed()) {
						if (!(this.tameable.squaredDistanceTo(this.owner) < 144.0)) {
							int i = MathHelper.floor(this.owner.x) - 2;
							int j = MathHelper.floor(this.owner.z) - 2;
							int k = MathHelper.floor(this.owner.getBoundingBox().minY);

							for (int l = 0; l <= 4; l++) {
								for (int m = 0; m <= 4; m++) {
									if ((l < 1 || m < 1 || l > 3 || m > 3)
										&& World.isOpaque(this.world, new BlockPos(i + l, k - 1, j + m))
										&& this.canTeleportTo(new BlockPos(i + l, k, j + m))
										&& this.canTeleportTo(new BlockPos(i + l, k + 1, j + m))) {
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
}
