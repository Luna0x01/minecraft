package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class SpiderEntity extends HostileEntity {
	public SpiderEntity(World world) {
		super(world);
		this.setBounds(1.4F, 0.9F);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.goals.add(4, new SpiderEntity.AttackGoal(this, PlayerEntity.class));
		this.goals.add(4, new SpiderEntity.AttackGoal(this, IronGolemEntity.class));
		this.goals.add(5, new WanderAroundGoal(this, 0.8));
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
		this.dataTracker.track(16, new Byte((byte)0));
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
	protected String getAmbientSound() {
		return "mob.spider.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.spider.say";
	}

	@Override
	protected String getDeathSound() {
		return "mob.spider.death";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.STRING;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		super.dropLoot(allowDrops, lootingMultiplier);
		if (allowDrops && (this.random.nextInt(3) == 0 || this.random.nextInt(1 + lootingMultiplier) > 0)) {
			this.dropItem(Items.SPIDER_EYE, 1);
		}
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
		return instance.getEffectId() == StatusEffect.POISON.id ? false : super.method_2658(instance);
	}

	public boolean getCanClimb() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	public void setCanClimb(boolean bl) {
		byte b = this.dataTracker.getByte(16);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.setProperty(16, b);
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		data = super.initialize(difficulty, data);
		if (this.world.random.nextInt(100) == 0) {
			SkeletonEntity skeletonEntity = new SkeletonEntity(this.world);
			skeletonEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			skeletonEntity.initialize(difficulty, null);
			this.world.spawnEntity(skeletonEntity);
			skeletonEntity.startRiding(this);
		}

		if (data == null) {
			data = new SpiderEntity.Data();
			if (this.world.getGlobalDifficulty() == Difficulty.HARD && this.world.random.nextFloat() < 0.1F * difficulty.getClampedLocalDifficulty()) {
				((SpiderEntity.Data)data).setEffect(this.world.random);
			}
		}

		if (data instanceof SpiderEntity.Data) {
			int i = ((SpiderEntity.Data)data).effect;
			if (i > 0 && StatusEffect.STATUS_EFFECTS[i] != null) {
				this.addStatusEffect(new StatusEffectInstance(i, Integer.MAX_VALUE));
			}
		}

		return data;
	}

	@Override
	public float getEyeHeight() {
		return 0.65F;
	}

	static class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(SpiderEntity spiderEntity, Class<? extends Entity> class_) {
			super(spiderEntity, class_, 1.0, true);
		}

		@Override
		public boolean shouldContinue() {
			float f = this.mob.getBrightnessAtEyes(1.0F);
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
		public int effect;

		public void setEffect(Random random) {
			int i = random.nextInt(5);
			if (i <= 1) {
				this.effect = StatusEffect.SPEED.id;
			} else if (i <= 2) {
				this.effect = StatusEffect.STRENGTH.id;
			} else if (i <= 3) {
				this.effect = StatusEffect.REGENERATION.id;
			} else if (i <= 4) {
				this.effect = StatusEffect.INVISIBILITY.id;
			}
		}
	}

	static class FollowTargetGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.FollowTargetGoal {
		public FollowTargetGoal(SpiderEntity spiderEntity, Class<T> class_) {
			super(spiderEntity, class_, true);
		}

		@Override
		public boolean canStart() {
			float f = this.mob.getBrightnessAtEyes(1.0F);
			return f >= 0.5F ? false : super.canStart();
		}
	}
}
