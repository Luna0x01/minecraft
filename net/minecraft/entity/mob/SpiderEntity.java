package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.block.Block;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class SpiderEntity extends HostileEntity {
	private static final TrackedData<Byte> field_14782 = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BYTE);

	public SpiderEntity(World world) {
		super(world);
		this.setBounds(1.4F, 0.9F);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, SpiderEntity.class);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.goals.add(4, new SpiderEntity.AttackGoal(this));
		this.goals.add(5, new class_3133(this, 0.8));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(6, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new SpiderEntity.FollowTargetGoal(this, PlayerEntity.class));
		this.attackGoals.add(3, new SpiderEntity.FollowTargetGoal(this, IronGolemEntity.class));
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)(this.height * 0.5F);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SpiderNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14782, (byte)0);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient) {
			this.setCanClimb(this.horizontalCollision);
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(16.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SPIDER_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_SPIDER_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SPIDER_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(Sounds.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SPIDER_ENTITIE;
	}

	@Override
	public boolean isClimbing() {
		return this.getCanClimb();
	}

	@Override
	public void setInLava() {
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	@Override
	public boolean method_2658(StatusEffectInstance instance) {
		return instance.getStatusEffect() == StatusEffects.POISON ? false : super.method_2658(instance);
	}

	public boolean getCanClimb() {
		return (this.dataTracker.get(field_14782) & 1) != 0;
	}

	public void setCanClimb(boolean bl) {
		byte b = this.dataTracker.get(field_14782);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.set(field_14782, b);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		if (this.world.random.nextInt(100) == 0) {
			SkeletonEntity skeletonEntity = new SkeletonEntity(this.world);
			skeletonEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			skeletonEntity.initialize(difficulty, null);
			this.world.spawnEntity(skeletonEntity);
			skeletonEntity.ride(this);
		}

		if (data == null) {
			data = new SpiderEntity.Data();
			if (this.world.getGlobalDifficulty() == Difficulty.HARD && this.world.random.nextFloat() < 0.1F * difficulty.getClampedLocalDifficulty()) {
				((SpiderEntity.Data)data).setEffect(this.world.random);
			}
		}

		if (data instanceof SpiderEntity.Data) {
			StatusEffect statusEffect = ((SpiderEntity.Data)data).field_14783;
			if (statusEffect != null) {
				this.addStatusEffect(new StatusEffectInstance(statusEffect, Integer.MAX_VALUE));
			}
		}

		return data;
	}

	@Override
	public float getEyeHeight() {
		return 0.65F;
	}

	static class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(SpiderEntity spiderEntity) {
			super(spiderEntity, 1.0, true);
		}

		@Override
		public boolean shouldContinue() {
			float f = this.mob.getBrightnessAtEyes();
			if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
				this.mob.setTarget(null);
				return false;
			} else {
				return super.shouldContinue();
			}
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
			return (double)(4.0F + entity.width);
		}
	}

	public static class Data implements EntityData {
		public StatusEffect field_14783;

		public void setEffect(Random random) {
			int i = random.nextInt(5);
			if (i <= 1) {
				this.field_14783 = StatusEffects.SPEED;
			} else if (i <= 2) {
				this.field_14783 = StatusEffects.STRENGTH;
			} else if (i <= 3) {
				this.field_14783 = StatusEffects.REGENERATION;
			} else if (i <= 4) {
				this.field_14783 = StatusEffects.INVISIBILITY;
			}
		}
	}

	static class FollowTargetGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.FollowTargetGoal<T> {
		public FollowTargetGoal(SpiderEntity spiderEntity, Class<T> class_) {
			super(spiderEntity, class_, true);
		}

		@Override
		public boolean canStart() {
			float f = this.mob.getBrightnessAtEyes();
			return f >= 0.5F ? false : super.canStart();
		}
	}
}
