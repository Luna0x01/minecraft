package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class TrackTargetGoal extends Goal {
	protected final PathAwareEntity mob;
	protected boolean checkVisibility;
	private final boolean checkNavigable;
	private int canNavigateFlag;
	private int checkCanNavigateCooldown;
	private int timeWithoutVisibility;
	protected LivingEntity field_14597;
	protected int field_14598 = 60;

	public TrackTargetGoal(PathAwareEntity pathAwareEntity, boolean bl) {
		this(pathAwareEntity, bl, false);
	}

	public TrackTargetGoal(PathAwareEntity pathAwareEntity, boolean bl, boolean bl2) {
		this.mob = pathAwareEntity;
		this.checkVisibility = bl;
		this.checkNavigable = bl2;
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			livingEntity = this.field_14597;
		}

		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.mob.getScoreboardTeam();
			AbstractTeam abstractTeam2 = livingEntity.getScoreboardTeam();
			if (abstractTeam != null && abstractTeam2 == abstractTeam) {
				return false;
			} else {
				double d = this.getFollowRange();
				if (this.mob.squaredDistanceTo(livingEntity) > d * d) {
					return false;
				} else {
					if (this.checkVisibility) {
						if (this.mob.getVisibilityCache().canSee(livingEntity)) {
							this.timeWithoutVisibility = 0;
						} else if (++this.timeWithoutVisibility > this.field_14598) {
							return false;
						}
					}

					if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable) {
						return false;
					} else {
						this.mob.setTarget(livingEntity);
						return true;
					}
				}
			}
		}
	}

	protected double getFollowRange() {
		EntityAttributeInstance entityAttributeInstance = this.mob.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE);
		return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
	}

	@Override
	public void start() {
		this.canNavigateFlag = 0;
		this.checkCanNavigateCooldown = 0;
		this.timeWithoutVisibility = 0;
	}

	@Override
	public void stop() {
		this.mob.setTarget(null);
		this.field_14597 = null;
	}

	public static boolean method_11025(MobEntity mob, LivingEntity target, boolean bl, boolean bl2) {
		if (target == null) {
			return false;
		} else if (target == mob) {
			return false;
		} else if (!target.isAlive()) {
			return false;
		} else if (!mob.canAttackEntity(target.getClass())) {
			return false;
		} else if (mob.isTeammate(target)) {
			return false;
		} else {
			if (mob instanceof Tameable && ((Tameable)mob).method_2719() != null) {
				if (target instanceof Tameable && ((Tameable)mob).method_2719().equals(target.getUuid())) {
					return false;
				}

				if (target == ((Tameable)mob).getOwner()) {
					return false;
				}
			} else if (target instanceof PlayerEntity && !bl && ((PlayerEntity)target).abilities.invulnerable) {
				return false;
			}

			return !bl2 || mob.getVisibilityCache().canSee(target);
		}
	}

	protected boolean canTrack(LivingEntity target, boolean bl) {
		if (!method_11025(this.mob, target, bl, this.checkVisibility)) {
			return false;
		} else if (!this.mob.isInWalkTargetRange(new BlockPos(target))) {
			return false;
		} else {
			if (this.checkNavigable) {
				if (--this.checkCanNavigateCooldown <= 0) {
					this.canNavigateFlag = 0;
				}

				if (this.canNavigateFlag == 0) {
					this.canNavigateFlag = this.canNavigateToEntity(target) ? 1 : 2;
				}

				if (this.canNavigateFlag == 2) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean canNavigateToEntity(LivingEntity entity) {
		this.checkCanNavigateCooldown = 10 + this.mob.getRandom().nextInt(5);
		PathMinHeap pathMinHeap = this.mob.getNavigation().method_13109(entity);
		if (pathMinHeap == null) {
			return false;
		} else {
			PathNode pathNode = pathMinHeap.method_11934();
			if (pathNode == null) {
				return false;
			} else {
				int i = pathNode.posX - MathHelper.floor(entity.x);
				int j = pathNode.posZ - MathHelper.floor(entity.z);
				return (double)(i * i + j * j) <= 2.25;
			}
		}
	}
}
