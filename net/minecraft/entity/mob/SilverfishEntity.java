package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class SilverfishEntity extends HostileEntity {
	private SilverfishEntity.CallForHelpGoal callForHelpGoal;

	public SilverfishEntity(World world) {
		super(EntityType.SILVERFISH, world);
		this.setBounds(0.4F, 0.3F);
	}

	@Override
	protected void initGoals() {
		this.callForHelpGoal = new SilverfishEntity.CallForHelpGoal(this);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(3, this.callForHelpGoal);
		this.goals.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goals.add(5, new SilverfishEntity.WanderAndInfestGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, true));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	public double getHeightOffset() {
		return 0.1;
	}

	@Override
	public float getEyeHeight() {
		return 0.1F;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SILVERFISH_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_SILVERFISH_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SILVERFISH_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.callForHelpGoal != null) {
				this.callForHelpGoal.onHurt();
			}

			return super.damage(source, amount);
		}
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SILVERFISH_ENTITIE;
	}

	@Override
	public void tick() {
		this.bodyYaw = this.yaw;
		super.tick();
	}

	@Override
	public void setYaw(float yaw) {
		this.yaw = yaw;
		super.setYaw(yaw);
	}

	@Override
	public float method_15657(BlockPos blockPos, RenderBlockView renderBlockView) {
		return InfestedBlock.method_16687(renderBlockView.getBlockState(blockPos.down())) ? 10.0F : super.method_15657(blockPos, renderBlockView);
	}

	@Override
	protected boolean method_3087() {
		return true;
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		if (super.method_15652(iWorld, bl)) {
			PlayerEntity playerEntity = iWorld.method_16383(this, 5.0);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16820;
	}

	static class CallForHelpGoal extends Goal {
		private final SilverfishEntity silverfish;
		private int delay;

		public CallForHelpGoal(SilverfishEntity silverfishEntity) {
			this.silverfish = silverfishEntity;
		}

		public void onHurt() {
			if (this.delay == 0) {
				this.delay = 20;
			}
		}

		@Override
		public boolean canStart() {
			return this.delay > 0;
		}

		@Override
		public void tick() {
			this.delay--;
			if (this.delay <= 0) {
				World world = this.silverfish.world;
				Random random = this.silverfish.getRandom();
				BlockPos blockPos = new BlockPos(this.silverfish);

				for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
					for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
						for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
							BlockPos blockPos2 = blockPos.add(j, i, k);
							BlockState blockState = world.getBlockState(blockPos2);
							Block block = blockState.getBlock();
							if (block instanceof InfestedBlock) {
								if (world.getGameRules().getBoolean("mobGriefing")) {
									world.method_8535(blockPos2, true);
								} else {
									world.setBlockState(blockPos2, ((InfestedBlock)block).method_16685().getDefaultState(), 3);
								}

								if (random.nextBoolean()) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	static class WanderAndInfestGoal extends WanderAroundGoal {
		private Direction direction;
		private boolean canInfest;

		public WanderAndInfestGoal(SilverfishEntity silverfishEntity) {
			super(silverfishEntity, 1.0, 10);
			this.setCategoryBits(1);
		}

		@Override
		public boolean canStart() {
			if (this.mob.getTarget() != null) {
				return false;
			} else if (!this.mob.getNavigation().isIdle()) {
				return false;
			} else {
				Random random = this.mob.getRandom();
				if (this.mob.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
					this.direction = Direction.random(random);
					BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).offset(this.direction);
					BlockState blockState = this.mob.world.getBlockState(blockPos);
					if (InfestedBlock.method_16687(blockState)) {
						this.canInfest = true;
						return true;
					}
				}

				this.canInfest = false;
				return super.canStart();
			}
		}

		@Override
		public boolean shouldContinue() {
			return this.canInfest ? false : super.shouldContinue();
		}

		@Override
		public void start() {
			if (!this.canInfest) {
				super.start();
			} else {
				IWorld iWorld = this.mob.world;
				BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).offset(this.direction);
				BlockState blockState = iWorld.getBlockState(blockPos);
				if (InfestedBlock.method_16687(blockState)) {
					iWorld.setBlockState(blockPos, InfestedBlock.method_16686(blockState.getBlock()), 3);
					this.mob.playSpawnEffects();
					this.mob.remove();
				}
			}
		}
	}
}
