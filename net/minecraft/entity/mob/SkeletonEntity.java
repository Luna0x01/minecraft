package net.minecraft.entity.mob;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.minecraft.class_3039;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.class_2973;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IceBiome;
import net.minecraft.world.dimension.TheNetherDimension;

public class SkeletonEntity extends HostileEntity implements RangedAttackMob {
	private static final TrackedData<Integer> field_14777 = DataTracker.registerData(SkeletonEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_14778 = DataTracker.registerData(SkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final class_2973 field_14779 = new class_2973(this, 1.0, 20, 15.0F);
	private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false) {
		@Override
		public void stop() {
			super.stop();
			SkeletonEntity.this.method_13237(false);
		}

		@Override
		public void start() {
			super.start();
			SkeletonEntity.this.method_13237(true);
		}
	};

	public SkeletonEntity(World world) {
		super(world);
		this.updateAttackType();
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new AvoidSunlightGoal(this));
		this.goals.add(3, new EscapeSunlightGoal(this, 1.0));
		this.goals.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.goals.add(5, new WanderAroundGoal(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(6, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14777, class_3039.NORMAL.method_13540());
		this.dataTracker.startTracking(field_14778, false);
	}

	@Override
	protected Sound ambientSound() {
		return this.method_13539().method_13543();
	}

	@Override
	protected Sound method_13048() {
		return this.method_13539().method_13544();
	}

	@Override
	protected Sound deathSound() {
		return this.method_13539().method_13545();
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		Sound sound = this.method_13539().method_13546();
		this.playSound(sound, 0.15F, 1.0F);
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (super.tryAttack(target)) {
			if (this.method_13539() == class_3039.WITHER && target instanceof LivingEntity) {
				((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	public void tickMovement() {
		if (this.world.isDay() && !this.world.isClient) {
			float f = this.getBrightnessAtEyes(1.0F);
			BlockPos blockPos = this.getVehicle() instanceof BoatEntity
				? new BlockPos(this.x, (double)Math.round(this.y), this.z).up()
				: new BlockPos(this.x, (double)Math.round(this.y), this.z);
			if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.hasDirectSunlight(blockPos)) {
				boolean bl = true;
				ItemStack itemStack = this.getStack(EquipmentSlot.HEAD);
				if (itemStack != null) {
					if (itemStack.isDamageable()) {
						itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
						if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
							this.method_6111(itemStack);
							this.equipStack(EquipmentSlot.HEAD, null);
						}
					}

					bl = false;
				}

				if (bl) {
					this.setOnFireFor(8);
				}
			}
		}

		if (this.world.isClient) {
			this.method_13538(this.method_13539());
		}

		super.tickMovement();
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		if (this.getVehicle() instanceof PathAwareEntity) {
			PathAwareEntity pathAwareEntity = (PathAwareEntity)this.getVehicle();
			this.bodyYaw = pathAwareEntity.bodyYaw;
		}
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getSource() instanceof AbstractArrowEntity && source.getAttacker() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)source.getAttacker();
			double d = playerEntity.x - this.x;
			double e = playerEntity.z - this.z;
			if (d * d + e * e >= 2500.0) {
				playerEntity.incrementStat(AchievementsAndCriterions.SNIPE_SKELETON);
			}
		} else if (source.getAttacker() instanceof CreeperEntity
			&& ((CreeperEntity)source.getAttacker()).method_3074()
			&& ((CreeperEntity)source.getAttacker()).shouldDropHead()) {
			((CreeperEntity)source.getAttacker()).onHeadDropped();
			this.dropItem(new ItemStack(Items.SKULL, 1, this.method_13539() == class_3039.WITHER ? 1 : 0), 0.0F);
		}
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return this.method_13539().method_13542();
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		if (this.world.dimension instanceof TheNetherDimension && this.getRandom().nextInt(5) > 0) {
			this.goals.add(4, this.meleeAttackGoal);
			this.method_13536(class_3039.WITHER);
			this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
			this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
		} else {
			Biome biome = this.world.getBiome(new BlockPos(this));
			if (biome instanceof IceBiome && this.world.hasDirectSunlight(new BlockPos(this)) && this.random.nextInt(5) != 0) {
				this.method_13536(class_3039.STRAY);
			}

			this.goals.add(4, this.field_14779);
			this.initEquipment(difficulty);
			this.updateEnchantments(difficulty);
		}

		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
		if (this.getStack(EquipmentSlot.HEAD) == null) {
			Calendar calendar = this.world.getCalenderInstance();
			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				this.field_14559[EquipmentSlot.HEAD.method_13032()] = 0.0F;
			}
		}

		return data;
	}

	public void updateAttackType() {
		if (this.world != null && !this.world.isClient) {
			this.goals.method_4497(this.meleeAttackGoal);
			this.goals.method_4497(this.field_14779);
			ItemStack itemStack = this.getMainHandStack();
			if (itemStack != null && itemStack.getItem() == Items.BOW) {
				int i = 20;
				if (this.world.getGlobalDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.field_14779.method_13101(i);
				this.goals.add(4, this.field_14779);
			} else {
				this.goals.add(4, this.meleeAttackGoal);
			}
		}
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		ArrowEntity arrowEntity = new ArrowEntity(this.world, this);
		double d = target.x - this.x;
		double e = target.getBoundingBox().minY + (double)(target.height / 3.0F) - arrowEntity.y;
		double f = target.z - this.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		arrowEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.world.getGlobalDifficulty().getId() * 4));
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.POWER, this);
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.PUNCH, this);
		LocalDifficulty localDifficulty = this.world.getLocalDifficulty(new BlockPos(this));
		arrowEntity.setDamage((double)(pullProgress * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.getGlobalDifficulty().getId() * 0.11F));
		if (i > 0) {
			arrowEntity.setDamage(arrowEntity.getDamage() + (double)i * 0.5 + 0.5);
		}

		if (j > 0) {
			arrowEntity.setPunch(j);
		}

		boolean bl = this.isOnFire() && localDifficulty.method_13484() && this.random.nextBoolean() || this.method_13539() == class_3039.WITHER;
		bl = bl || EnchantmentHelper.getEquipmentLevel(Enchantments.FLAME, this) > 0;
		if (bl) {
			arrowEntity.setOnFireFor(100);
		}

		ItemStack itemStack = this.getStackInHand(Hand.OFF_HAND);
		if (itemStack != null && itemStack.getItem() == Items.TIPPED_ARROW) {
			arrowEntity.initFromStack(itemStack);
		} else if (this.method_13539() == class_3039.STRAY) {
			arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600));
		}

		this.playSound(Sounds.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(arrowEntity);
	}

	public class_3039 method_13539() {
		return class_3039.method_13541(this.dataTracker.get(field_14777));
	}

	public void method_13536(class_3039 arg) {
		this.dataTracker.set(field_14777, arg.method_13540());
		this.isFireImmune = arg == class_3039.WITHER;
		this.method_13538(arg);
	}

	private void method_13538(class_3039 arg) {
		if (arg == class_3039.WITHER) {
			this.setBounds(0.7F, 2.4F);
		} else {
			this.setBounds(0.6F, 1.99F);
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Skeleton");
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("SkeletonType", 99)) {
			int i = nbt.getByte("SkeletonType");
			this.method_13536(class_3039.method_13541(i));
		}

		this.updateAttackType();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("SkeletonType", (byte)this.method_13539().method_13540());
	}

	@Override
	public void equipStack(EquipmentSlot slot, @Nullable ItemStack stack) {
		super.equipStack(slot, stack);
		if (!this.world.isClient && slot == EquipmentSlot.MAINHAND) {
			this.updateAttackType();
		}
	}

	@Override
	public float getEyeHeight() {
		return this.method_13539() == class_3039.WITHER ? 2.1F : 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return -0.35;
	}

	public boolean method_13239() {
		return this.dataTracker.get(field_14778);
	}

	public void method_13237(boolean bl) {
		this.dataTracker.set(field_14778, bl);
	}
}
