package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.EntityGroup;
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
import net.minecraft.world.World;

public class SilverfishEntity extends HostileEntity {
	private SilverfishEntity.CallForHelpGoal callForHelpGoal;

	public SilverfishEntity(World world) {
		super(world);
		this.setBounds(0.4F, 0.3F);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Silverfish");
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
		return 0.2;
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
	protected Sound method_13048() {
		return Sounds.ENTITY_SILVERFISH_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SILVERFISH_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
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
	public float getPathfindingFavor(BlockPos pos) {
		return this.world.getBlockState(pos.down()).getBlock() == Blocks.STONE ? 10.0F : super.getPathfindingFavor(pos);
	}

	@Override
	protected boolean method_3087() {
		return true;
	}

	@Override
	public boolean canSpawn() {
		if (super.canSpawn()) {
			PlayerEntity playerEntity = this.world.method_11490(this, 5.0);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
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

				for (int i = 0; i <= 5 && i >= -5; i = i <= 0 ? 1 - i : 0 - i) {
					for (int j = 0; j <= 10 && j >= -10; j = j <= 0 ? 1 - j : 0 - j) {
						for (int k = 0; k <= 10 && k >= -10; k = k <= 0 ? 1 - k : 0 - k) {
							BlockPos blockPos2 = blockPos.add(j, i, k);
							BlockState blockState = world.getBlockState(blockPos2);
							if (blockState.getBlock() == Blocks.MONSTER_EGG) {
								if (world.getGameRules().getBoolean("mobGriefing")) {
									world.removeBlock(blockPos2, true);
								} else {
									world.setBlockState(blockPos2, ((InfestedBlock.Variants)blockState.get(InfestedBlock.VARIANT)).getBlockState(), 3);
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
		private final SilverfishEntity silverfish;
		private Direction direction;
		private boolean canInfest;

		public WanderAndInfestGoal(SilverfishEntity silverfishEntity) {
			super(silverfishEntity, 1.0, 10);
			this.silverfish = silverfishEntity;
			this.setCategoryBits(1);
		}

		@Override
		public boolean canStart() {
			if (!this.silverfish.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			} else if (this.silverfish.getTarget() != null) {
				return false;
			} else if (!this.silverfish.getNavigation().isIdle()) {
				return false;
			} else {
				Random random = this.silverfish.getRandom();
				if (random.nextInt(10) == 0) {
					this.direction = Direction.random(random);
					BlockPos blockPos = new BlockPos(this.silverfish.x, this.silverfish.y + 0.5, this.silverfish.z).offset(this.direction);
					BlockState blockState = this.silverfish.world.getBlockState(blockPos);
					if (InfestedBlock.isInfestable(blockState)) {
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
				World world = this.silverfish.world;
				BlockPos blockPos = new BlockPos(this.silverfish.x, this.silverfish.y + 0.5, this.silverfish.z).offset(this.direction);
				BlockState blockState = world.getBlockState(blockPos);
				if (InfestedBlock.isInfestable(blockState)) {
					world.setBlockState(blockPos, Blocks.MONSTER_EGG.getDefaultState().with(InfestedBlock.VARIANT, InfestedBlock.Variants.getByBlockState(blockState)), 3);
					this.silverfish.playSpawnEffects();
					this.silverfish.remove();
				}
			}
		}
	}
}
