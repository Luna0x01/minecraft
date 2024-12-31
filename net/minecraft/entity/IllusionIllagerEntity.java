package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3162;
import net.minecraft.class_3168;
import net.minecraft.class_3462;
import net.minecraft.class_4342;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.class_2973;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class IllusionIllagerEntity extends class_3168 implements RangedAttackMob {
	private int field_15599;
	private final Vec3d[][] field_15598;

	public IllusionIllagerEntity(World world) {
		super(EntityType.ILLUSIONER, world);
		this.setBounds(0.6F, 1.95F);
		this.experiencePoints = 5;
		this.field_15598 = new Vec3d[2][4];

		for (int i = 0; i < 4; i++) {
			this.field_15598[0][i] = new Vec3d(0.0, 0.0, 0.0);
			this.field_15598[1][i] = new Vec3d(0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new class_3168.class_3170());
		this.goals.add(4, new IllusionIllagerEntity.class_3167());
		this.goals.add(5, new IllusionIllagerEntity.class_3166());
		this.goals.add(6, new class_2973<>(this, 0.5, 20, 15.0F));
		this.goals.add(8, new WanderAroundGoal(this, 0.6));
		this.goals.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goals.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.attackGoals.add(1, new RevengeGoal(this, true, IllusionIllagerEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true).method_13955(300));
		this.attackGoals.add(3, new FollowTargetGoal(this, VillagerEntity.class, false).method_13955(300));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, false).method_13955(300));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(18.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(32.0);
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
		return super.initialize(difficulty, entityData, nbt);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	protected Identifier getLootTableId() {
		return LootTables.EMPTY;
	}

	@Override
	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox().expand(3.0, 0.0, 3.0);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient && this.isInvisible()) {
			this.field_15599--;
			if (this.field_15599 < 0) {
				this.field_15599 = 0;
			}

			if (this.hurtTime == 1 || this.ticksAlive % 1200 == 0) {
				this.field_15599 = 3;
				float f = -6.0F;
				int i = 13;

				for (int j = 0; j < 4; j++) {
					this.field_15598[0][j] = this.field_15598[1][j];
					this.field_15598[1][j] = new Vec3d(
						(double)(-6.0F + (float)this.random.nextInt(13)) * 0.5,
						(double)Math.max(0, this.random.nextInt(6) - 4),
						(double)(-6.0F + (float)this.random.nextInt(13)) * 0.5
					);
				}

				for (int k = 0; k < 16; k++) {
					this.world
						.method_16343(
							class_4342.field_21381,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
							this.y + this.random.nextDouble() * (double)this.height,
							this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
							0.0,
							0.0,
							0.0
						);
				}

				this.world.playSound(this.x, this.y, this.z, Sounds.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
			} else if (this.hurtTime == this.maxHurtTime - 1) {
				this.field_15599 = 3;

				for (int l = 0; l < 4; l++) {
					this.field_15598[0][l] = this.field_15598[1][l];
					this.field_15598[1][l] = new Vec3d(0.0, 0.0, 0.0);
				}
			}
		}
	}

	public Vec3d[] method_14126(float f) {
		if (this.field_15599 <= 0) {
			return this.field_15598[1];
		} else {
			double d = (double)(((float)this.field_15599 - f) / 3.0F);
			d = Math.pow(d, 0.25);
			Vec3d[] vec3ds = new Vec3d[4];

			for (int i = 0; i < 4; i++) {
				vec3ds[i] = this.field_15598[1][i].multiply(1.0 - d).add(this.field_15598[0][i].multiply(d));
			}

			return vec3ds;
		}
	}

	@Override
	public boolean isTeammate(Entity other) {
		if (super.isTeammate(other)) {
			return true;
		} else {
			return other instanceof LivingEntity && ((LivingEntity)other).method_2647() == class_3462.field_16821
				? this.getScoreboardTeam() == null && other.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_ILLUSIONER_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ILLUSIONER_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ILLUSIONER_HURT;
	}

	@Override
	protected Sound method_14132() {
		return Sounds.ENTITY_ILLUSIONER_CAST_SPELL;
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		AbstractArrowEntity abstractArrowEntity = this.method_14128(pullProgress);
		double d = target.x - this.x;
		double e = target.getBoundingBox().minY + (double)(target.height / 3.0F) - abstractArrowEntity.y;
		double f = target.z - this.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		abstractArrowEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.world.method_16346().getId() * 4));
		this.playSound(Sounds.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.method_3686(abstractArrowEntity);
	}

	protected AbstractArrowEntity method_14128(float f) {
		ArrowEntity arrowEntity = new ArrowEntity(this.world, this);
		arrowEntity.applyEnchantmentEffects(this, f);
		return arrowEntity;
	}

	public boolean method_14127() {
		return this.method_14121(1);
	}

	@Override
	public void method_13246(boolean bl) {
		this.method_14122(1, bl);
	}

	@Override
	public class_3162.class_3163 method_14123() {
		if (this.method_14133()) {
			return class_3162.class_3163.SPELLCASTING;
		} else {
			return this.method_14127() ? class_3162.class_3163.BOW_AND_ARROW : class_3162.class_3163.CROSSED;
		}
	}

	class class_3166 extends class_3168.class_3152 {
		private int field_15601;

		private class_3166() {
		}

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else if (IllusionIllagerEntity.this.getTarget() == null) {
				return false;
			} else {
				return IllusionIllagerEntity.this.getTarget().getEntityId() == this.field_15601
					? false
					: IllusionIllagerEntity.this.world.method_8482(new BlockPos(IllusionIllagerEntity.this)).method_15040((float)Difficulty.NORMAL.ordinal());
			}
		}

		@Override
		public void start() {
			super.start();
			this.field_15601 = IllusionIllagerEntity.this.getTarget().getEntityId();
		}

		@Override
		protected int method_14084() {
			return 20;
		}

		@Override
		protected int method_14085() {
			return 180;
		}

		@Override
		protected void method_14086() {
			IllusionIllagerEntity.this.getTarget().method_2654(new StatusEffectInstance(StatusEffects.BLINDNESS, 400));
		}

		@Override
		protected Sound method_14087() {
			return Sounds.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
		}

		@Override
		protected class_3168.class_3169 method_14139() {
			return class_3168.class_3169.BLINDNESS;
		}
	}

	class class_3167 extends class_3168.class_3152 {
		private class_3167() {
		}

		@Override
		public boolean canStart() {
			return !super.canStart() ? false : !IllusionIllagerEntity.this.hasStatusEffect(StatusEffects.INVISIBILITY);
		}

		@Override
		protected int method_14084() {
			return 20;
		}

		@Override
		protected int method_14085() {
			return 340;
		}

		@Override
		protected void method_14086() {
			IllusionIllagerEntity.this.method_2654(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
		}

		@Nullable
		@Override
		protected Sound method_14087() {
			return Sounds.ENTITY_ILLUSIONER_PREPARE_MIRROR;
		}

		@Override
		protected class_3168.class_3169 method_14139() {
			return class_3168.class_3169.DISAPPEAR;
		}
	}
}
