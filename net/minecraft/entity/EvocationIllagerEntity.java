package net.minecraft.entity;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.HostileEntity;
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

public class EvocationIllagerEntity extends HostileEntity {
	protected static final TrackedData<Byte> field_15536 = DataTracker.registerData(EvocationIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int field_15537;
	private int field_15539;
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
		this.dataTracker.startTracking(field_15536, (byte)0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, EvocationIllagerEntity.class);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.field_15537 = nbt.getInt("SpellTicks");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("SpellTicks", this.field_15537);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ILLAGER;
	}

	@Override
	protected Identifier getLootTableId() {
		return LootTables.EVOCATION_ILLAGER_ENTITIE;
	}

	public boolean method_14082() {
		return this.world.isClient ? this.dataTracker.get(field_15536) > 0 : this.field_15537 > 0;
	}

	public void method_14065(int i) {
		this.dataTracker.set(field_15536, (byte)i);
	}

	private int method_14075() {
		return this.field_15537;
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if (this.field_15537 > 0) {
			this.field_15537--;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && this.method_14082()) {
			int i = this.dataTracker.get(field_15536);
			double d = 0.7;
			double e = 0.5;
			double f = 0.2;
			if (i == 2) {
				d = 0.4;
				e = 0.3;
				f = 0.35;
			} else if (i == 1) {
				d = 0.7;
				e = 0.7;
				f = 0.8;
			}

			float g = this.bodyYaw * (float) (Math.PI / 180.0) + MathHelper.cos((float)this.ticksAlive * 0.6662F) * 0.25F;
			float h = MathHelper.cos(g);
			float j = MathHelper.sin(g);
			this.world.addParticle(ParticleType.MOB_SPELL, this.x + (double)h * 0.6, this.y + 1.8, this.z + (double)j * 0.6, d, e, f);
			this.world.addParticle(ParticleType.MOB_SPELL, this.x - (double)h * 0.6, this.y + 1.8, this.z - (double)j * 0.6, d, e, f);
		}
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
	protected Sound method_13048() {
		return Sounds.ENTITY_EVOCATION_ILLAGER_HURT;
	}

	private void method_14067(@Nullable SheepEntity sheepEntity) {
		this.field_15538 = sheepEntity;
	}

	@Nullable
	private SheepEntity method_14076() {
		return this.field_15538;
	}

	class class_3149 extends EvocationIllagerEntity.class_3152 {
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
		protected int method_14088() {
			return 2;
		}
	}

	class class_3150 extends Goal {
		public class_3150() {
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			return EvocationIllagerEntity.this.method_14075() > 0;
		}

		@Override
		public void start() {
			super.start();
			EvocationIllagerEntity.this.method_14065(EvocationIllagerEntity.this.field_15539);
			EvocationIllagerEntity.this.navigation.stop();
		}

		@Override
		public void stop() {
			super.stop();
			EvocationIllagerEntity.this.method_14065(0);
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

	class class_3151 extends EvocationIllagerEntity.class_3152 {
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
		protected int method_14088() {
			return 1;
		}
	}

	abstract class class_3152 extends Goal {
		protected int field_15543;
		protected int field_15544;

		private class_3152() {
		}

		@Override
		public boolean canStart() {
			if (EvocationIllagerEntity.this.getTarget() == null) {
				return false;
			} else {
				return EvocationIllagerEntity.this.method_14082() ? false : EvocationIllagerEntity.this.ticksAlive >= this.field_15544;
			}
		}

		@Override
		public boolean shouldContinue() {
			return EvocationIllagerEntity.this.getTarget() != null && this.field_15543 > 0;
		}

		@Override
		public void start() {
			this.field_15543 = this.method_14089();
			EvocationIllagerEntity.this.field_15537 = this.method_14084();
			this.field_15544 = EvocationIllagerEntity.this.ticksAlive + this.method_14085();
			EvocationIllagerEntity.this.playSound(this.method_14087(), 1.0F, 1.0F);
			EvocationIllagerEntity.this.field_15539 = this.method_14088();
		}

		@Override
		public void tick() {
			this.field_15543--;
			if (this.field_15543 == 0) {
				this.method_14086();
				EvocationIllagerEntity.this.playSound(Sounds.ENTITY_EVOCATION_ILLAGER_CAST_SPELL, 1.0F, 1.0F);
			}
		}

		protected abstract void method_14086();

		protected int method_14089() {
			return 20;
		}

		protected abstract int method_14084();

		protected abstract int method_14085();

		protected abstract Sound method_14087();

		protected abstract int method_14088();
	}

	public class class_3153 extends EvocationIllagerEntity.class_3152 {
		final Predicate<SheepEntity> field_15546 = new Predicate<SheepEntity>() {
			public boolean apply(SheepEntity sheepEntity) {
				return sheepEntity.getColor() == DyeColor.BLUE;
			}
		};

		@Override
		public boolean canStart() {
			if (EvocationIllagerEntity.this.getTarget() != null) {
				return false;
			} else if (EvocationIllagerEntity.this.method_14082()) {
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
		protected int method_14088() {
			return 3;
		}
	}
}
