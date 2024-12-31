package net.minecraft.entity;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3168;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EvocationIllagerEntity extends class_3168 {
	private SheepEntity field_15538;

	public EvocationIllagerEntity(World world) {
		super(world);
		this.setBounds(0.6F, 1.95F);
		this.experiencePoints = 10;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EvocationIllagerEntity.class_3150());
		this.goals.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 0.6, 1.0));
		this.goals.add(4, new EvocationIllagerEntity.class_3151());
		this.goals.add(5, new EvocationIllagerEntity.class_3149());
		this.goals.add(6, new EvocationIllagerEntity.class_3153());
		this.goals.add(8, new WanderAroundGoal(this, 0.6));
		this.goals.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goals.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.attackGoals.add(1, new RevengeGoal(this, true, EvocationIllagerEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true).method_13955(300));
		this.attackGoals.add(3, new FollowTargetGoal(this, VillagerEntity.class, false).method_13955(300));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, false));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(12.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(24.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, EvocationIllagerEntity.class);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	protected Identifier getLootTableId() {
		return LootTables.EVOCATION_ILLAGER_ENTITIE;
	}

	@Override
	protected void mobTick() {
		super.mobTick();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean isTeammate(Entity other) {
		if (other == null) {
			return false;
		} else if (other == this) {
			return true;
		} else if (super.isTeammate(other)) {
			return true;
		} else if (other instanceof VexEntity) {
			return this.isTeammate(((VexEntity)other).method_13593());
		} else {
			return other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER
				? this.getScoreboardTeam() == null && other.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_EVOCATION_ILLAGER_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_EVOCATION_ILLAGER_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_EVOCATION_ILLAGER_HURT;
	}

	private void method_14067(@Nullable SheepEntity sheepEntity) {
		this.field_15538 = sheepEntity;
	}

	@Nullable
	private SheepEntity method_14076() {
		return this.field_15538;
	}

	@Override
	protected Sound method_14132() {
		return Sounds.ENTITY_EVOCATION_ILLAGER_CAST_SPELL;
	}

	class class_3149 extends class_3168.class_3152 {
		private class_3149() {
		}

		@Override
		protected int method_14084() {
			return 40;
		}

		@Override
		protected int method_14085() {
			return 100;
		}

		@Override
		protected void method_14086() {
			LivingEntity livingEntity = EvocationIllagerEntity.this.getTarget();
			double d = Math.min(livingEntity.y, EvocationIllagerEntity.this.y);
			double e = Math.max(livingEntity.y, EvocationIllagerEntity.this.y) + 1.0;
			float f = (float)MathHelper.atan2(livingEntity.z - EvocationIllagerEntity.this.z, livingEntity.x - EvocationIllagerEntity.this.x);
			if (EvocationIllagerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
				for (int i = 0; i < 5; i++) {
					float g = f + (float)i * (float) Math.PI * 0.4F;
					this.method_14083(
						EvocationIllagerEntity.this.x + (double)MathHelper.cos(g) * 1.5, EvocationIllagerEntity.this.z + (double)MathHelper.sin(g) * 1.5, d, e, g, 0
					);
				}

				for (int j = 0; j < 8; j++) {
					float h = f + (float)j * (float) Math.PI * 2.0F / 8.0F + (float) (Math.PI * 2.0 / 5.0);
					this.method_14083(
						EvocationIllagerEntity.this.x + (double)MathHelper.cos(h) * 2.5, EvocationIllagerEntity.this.z + (double)MathHelper.sin(h) * 2.5, d, e, h, 3
					);
				}
			} else {
				for (int k = 0; k < 16; k++) {
					double l = 1.25 * (double)(k + 1);
					int m = 1 * k;
					this.method_14083(EvocationIllagerEntity.this.x + (double)MathHelper.cos(f) * l, EvocationIllagerEntity.this.z + (double)MathHelper.sin(f) * l, d, e, f, m);
				}
			}
		}

		private void method_14083(double d, double e, double f, double g, float h, int i) {
			BlockPos blockPos = new BlockPos(d, g, e);
			boolean bl = false;
			double j = 0.0;

			do {
				if (!EvocationIllagerEntity.this.world.renderAsNormalBlock(blockPos, true) && EvocationIllagerEntity.this.world.renderAsNormalBlock(blockPos.down(), true)) {
					if (!EvocationIllagerEntity.this.world.isAir(blockPos)) {
						BlockState blockState = EvocationIllagerEntity.this.world.getBlockState(blockPos);
						Box box = blockState.method_11726(EvocationIllagerEntity.this.world, blockPos);
						if (box != null) {
							j = box.maxY;
						}
					}

					bl = true;
					break;
				}

				blockPos = blockPos.down();
			} while (blockPos.getY() >= MathHelper.floor(f) - 1);

			if (bl) {
				EvokerFangsEntity evokerFangsEntity = new EvokerFangsEntity(
					EvocationIllagerEntity.this.world, d, (double)blockPos.getY() + j, e, h, i, EvocationIllagerEntity.this
				);
				EvocationIllagerEntity.this.world.spawnEntity(evokerFangsEntity);
			}
		}

		@Override
		protected Sound method_14087() {
			return Sounds.ENTITY_EVOCATION_ILLAGER_PREPARE_ATTACK;
		}

		@Override
		protected class_3168.class_3169 method_14139() {
			return class_3168.class_3169.FANGS;
		}
	}

	class class_3150 extends class_3168.class_3170 {
		private class_3150() {
		}

		@Override
		public void tick() {
			if (EvocationIllagerEntity.this.getTarget() != null) {
				EvocationIllagerEntity.this.getLookControl()
					.lookAt(EvocationIllagerEntity.this.getTarget(), (float)EvocationIllagerEntity.this.method_13081(), (float)EvocationIllagerEntity.this.getLookPitchSpeed());
			} else if (EvocationIllagerEntity.this.method_14076() != null) {
				EvocationIllagerEntity.this.getLookControl()
					.lookAt(
						EvocationIllagerEntity.this.method_14076(), (float)EvocationIllagerEntity.this.method_13081(), (float)EvocationIllagerEntity.this.getLookPitchSpeed()
					);
			}
		}
	}

	class class_3151 extends class_3168.class_3152 {
		private class_3151() {
		}

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else {
				int i = EvocationIllagerEntity.this.world.getEntitiesInBox(VexEntity.class, EvocationIllagerEntity.this.getBoundingBox().expand(16.0)).size();
				return EvocationIllagerEntity.this.random.nextInt(8) + 1 > i;
			}
		}

		@Override
		protected int method_14084() {
			return 100;
		}

		@Override
		protected int method_14085() {
			return 340;
		}

		@Override
		protected void method_14086() {
			for (int i = 0; i < 3; i++) {
				BlockPos blockPos = new BlockPos(EvocationIllagerEntity.this)
					.add(-2 + EvocationIllagerEntity.this.random.nextInt(5), 1, -2 + EvocationIllagerEntity.this.random.nextInt(5));
				VexEntity vexEntity = new VexEntity(EvocationIllagerEntity.this.world);
				vexEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
				vexEntity.initialize(EvocationIllagerEntity.this.world.getLocalDifficulty(blockPos), null);
				vexEntity.method_13579(EvocationIllagerEntity.this);
				vexEntity.method_13590(blockPos);
				vexEntity.method_13575(20 * (30 + EvocationIllagerEntity.this.random.nextInt(90)));
				EvocationIllagerEntity.this.world.spawnEntity(vexEntity);
			}
		}

		@Override
		protected Sound method_14087() {
			return Sounds.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON;
		}

		@Override
		protected class_3168.class_3169 method_14139() {
			return class_3168.class_3169.SUMMON_VEX;
		}
	}

	public class class_3153 extends class_3168.class_3152 {
		final Predicate<SheepEntity> field_15546 = new Predicate<SheepEntity>() {
			public boolean apply(SheepEntity sheepEntity) {
				return sheepEntity.getColor() == DyeColor.BLUE;
			}
		};

		@Override
		public boolean canStart() {
			if (EvocationIllagerEntity.this.getTarget() != null) {
				return false;
			} else if (EvocationIllagerEntity.this.method_14133()) {
				return false;
			} else if (EvocationIllagerEntity.this.ticksAlive < this.field_15544) {
				return false;
			} else if (!EvocationIllagerEntity.this.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			} else {
				List<SheepEntity> list = EvocationIllagerEntity.this.world
					.getEntitiesInBox(SheepEntity.class, EvocationIllagerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0), this.field_15546);
				if (list.isEmpty()) {
					return false;
				} else {
					EvocationIllagerEntity.this.method_14067((SheepEntity)list.get(EvocationIllagerEntity.this.random.nextInt(list.size())));
					return true;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			return EvocationIllagerEntity.this.method_14076() != null && this.field_15543 > 0;
		}

		@Override
		public void stop() {
			super.stop();
			EvocationIllagerEntity.this.method_14067(null);
		}

		@Override
		protected void method_14086() {
			SheepEntity sheepEntity = EvocationIllagerEntity.this.method_14076();
			if (sheepEntity != null && sheepEntity.isAlive()) {
				sheepEntity.setColor(DyeColor.RED);
			}
		}

		@Override
		protected int method_14089() {
			return 40;
		}

		@Override
		protected int method_14084() {
			return 60;
		}

		@Override
		protected int method_14085() {
			return 140;
		}

		@Override
		protected Sound method_14087() {
			return Sounds.ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO;
		}

		@Override
		protected class_3168.class_3169 method_14139() {
			return class_3168.class_3169.WOLOLO;
		}
	}
}
