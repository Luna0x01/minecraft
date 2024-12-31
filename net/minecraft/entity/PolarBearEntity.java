package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class PolarBearEntity extends AnimalEntity {
	private static final TrackedData<Boolean> field_15032 = DataTracker.registerData(PolarBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private float field_15033;
	private float field_15034;
	private int field_15031;

	public PolarBearEntity(World world) {
		super(EntityType.POLAR_BEAR, world);
		this.setBounds(1.3F, 1.4F);
	}

	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		return new PolarBearEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new PolarBearEntity.class_3037());
		this.goals.add(1, new PolarBearEntity.class_3038());
		this.goals.add(4, new FollowParentGoal(this, 1.25));
		this.goals.add(5, new WanderAroundGoal(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(7, new LookAroundGoal(this));
		this.attackGoals.add(1, new PolarBearEntity.class_3036());
		this.attackGoals.add(2, new PolarBearEntity.class_3034());
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(30.0);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(20.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6.0);
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Biome biome = iWorld.method_8577(blockPos);
		return biome != Biomes.FROZEN_OCEAN && biome != Biomes.DEEP_FROZEN_OCEAN
			? super.method_15652(iWorld, bl)
			: iWorld.method_16379(blockPos, 0) > 8 && iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.ICE;
	}

	@Override
	protected Sound ambientSound() {
		return this.isBaby() ? Sounds.ENTITY_POLAR_BEAR_AMBIENT_BABY : Sounds.ENTITY_POLAR_BEAR_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_POLAR_BEAR_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_POLAR_BEAR_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
	}

	protected void method_13505() {
		if (this.field_15031 <= 0) {
			this.playSound(Sounds.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
			this.field_15031 = 40;
		}
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.POLAR_BEAR_ENTITIE;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15032, false);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			this.field_15033 = this.field_15034;
			if (this.method_13506()) {
				this.field_15034 = MathHelper.clamp(this.field_15034 + 1.0F, 0.0F, 6.0F);
			} else {
				this.field_15034 = MathHelper.clamp(this.field_15034 - 1.0F, 0.0F, 6.0F);
			}
		}

		if (this.field_15031 > 0) {
			this.field_15031--;
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, target);
		}

		return bl;
	}

	public boolean method_13506() {
		return this.dataTracker.get(field_15032);
	}

	public void method_13507(boolean bl) {
		this.dataTracker.set(field_15032, bl);
	}

	public float method_13508(float f) {
		return (this.field_15033 + (this.field_15034 - this.field_15033) * f) / 6.0F;
	}

	@Override
	protected float method_13494() {
		return 0.98F;
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		if (entityData instanceof PolarBearEntity.class_3035) {
			if (((PolarBearEntity.class_3035)entityData).field_15036) {
				this.setAge(-24000);
			}
		} else {
			PolarBearEntity.class_3035 lv = new PolarBearEntity.class_3035();
			lv.field_15036 = true;
			entityData = lv;
		}

		return entityData;
	}

	class class_3034 extends FollowTargetGoal<PlayerEntity> {
		public class_3034() {
			super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
		}

		@Override
		public boolean canStart() {
			if (PolarBearEntity.this.isBaby()) {
				return false;
			} else {
				if (super.canStart()) {
					for (PolarBearEntity polarBearEntity : PolarBearEntity.this.world
						.getEntitiesInBox(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().expand(8.0, 4.0, 8.0))) {
						if (polarBearEntity.isBaby()) {
							return true;
						}
					}
				}

				PolarBearEntity.this.setTarget(null);
				return false;
			}
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.5;
		}
	}

	static class class_3035 implements EntityData {
		public boolean field_15036;

		private class_3035() {
		}
	}

	class class_3036 extends RevengeGoal {
		public class_3036() {
			super(PolarBearEntity.this, false);
		}

		@Override
		public void start() {
			super.start();
			if (PolarBearEntity.this.isBaby()) {
				this.method_13498();
				this.stop();
			}
		}

		@Override
		protected void setMobEntityTarget(PathAwareEntity mob, LivingEntity target) {
			if (mob instanceof PolarBearEntity && !mob.isBaby()) {
				super.setMobEntityTarget(mob, target);
			}
		}
	}

	class class_3037 extends MeleeAttackGoal {
		public class_3037() {
			super(PolarBearEntity.this, 1.25, true);
		}

		@Override
		protected void method_13497(LivingEntity livingEntity, double d) {
			double e = this.getSquaredMaxAttackDistance(livingEntity);
			if (d <= e && this.field_3534 <= 0) {
				this.field_3534 = 20;
				this.mob.tryAttack(livingEntity);
				PolarBearEntity.this.method_13507(false);
			} else if (d <= e * 2.0) {
				if (this.field_3534 <= 0) {
					PolarBearEntity.this.method_13507(false);
					this.field_3534 = 20;
				}

				if (this.field_3534 <= 10) {
					PolarBearEntity.this.method_13507(true);
					PolarBearEntity.this.method_13505();
				}
			} else {
				this.field_3534 = 20;
				PolarBearEntity.this.method_13507(false);
			}
		}

		@Override
		public void stop() {
			PolarBearEntity.this.method_13507(false);
			super.stop();
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
			return (double)(4.0F + entity.width);
		}
	}

	class class_3038 extends EscapeDangerGoal {
		public class_3038() {
			super(PolarBearEntity.this, 2.0);
		}

		@Override
		public boolean canStart() {
			return !PolarBearEntity.this.isBaby() && !PolarBearEntity.this.isOnFire() ? false : super.canStart();
		}
	}
}
