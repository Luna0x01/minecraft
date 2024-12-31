package net.minecraft;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
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
import net.minecraft.entity.ai.goal.class_2973;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class class_3146 extends HostileEntity implements RangedAttackMob {
	private static final TrackedData<Boolean> field_15531 = DataTracker.registerData(class_3146.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final class_2973<class_3146> field_15532 = new class_2973<>(this, 1.0, 20, 15.0F);
	private final MeleeAttackGoal field_15533 = new MeleeAttackGoal(this, 1.2, false) {
		@Override
		public void stop() {
			super.stop();
			class_3146.this.method_13246(false);
		}

		@Override
		public void start() {
			super.start();
			class_3146.this.method_13246(true);
		}
	};

	protected class_3146(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setBounds(0.6F, 1.99F);
		this.method_14058();
	}

	@Override
	protected void initGoals() {
		this.goals.add(2, new AvoidSunlightGoal(this));
		this.goals.add(3, new EscapeSunlightGoal(this, 1.0));
		this.goals.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.goals.add(5, new class_3133(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(6, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.field_16957));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15531, false);
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(this.method_14060(), 0.15F, 1.0F);
	}

	abstract Sound method_14060();

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16819;
	}

	@Override
	public void tickMovement() {
		boolean bl = this.method_15656();
		if (bl) {
			ItemStack itemStack = this.getStack(EquipmentSlot.HEAD);
			if (!itemStack.isEmpty()) {
				if (itemStack.isDamageable()) {
					itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
					if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
						this.method_6111(itemStack);
						this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
					}
				}

				bl = false;
			}

			if (bl) {
				this.setOnFireFor(8);
			}
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
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		entityData = super.initialize(difficulty, entityData, nbt);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		this.method_14058();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
		if (this.getStack(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
				this.field_14559[EquipmentSlot.HEAD.method_13032()] = 0.0F;
			}
		}

		return entityData;
	}

	public void method_14058() {
		if (this.world != null && !this.world.isClient) {
			this.goals.method_4497(this.field_15533);
			this.goals.method_4497(this.field_15532);
			ItemStack itemStack = this.getMainHandStack();
			if (itemStack.getItem() == Items.BOW) {
				int i = 20;
				if (this.world.method_16346() != Difficulty.HARD) {
					i = 40;
				}

				this.field_15532.method_13101(i);
				this.goals.add(4, this.field_15532);
			} else {
				this.goals.add(4, this.field_15533);
			}
		}
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		AbstractArrowEntity abstractArrowEntity = this.method_14056(pullProgress);
		double d = target.x - this.x;
		double e = target.getBoundingBox().minY + (double)(target.height / 3.0F) - abstractArrowEntity.y;
		double f = target.z - this.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		abstractArrowEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.world.method_16346().getId() * 4));
		this.playSound(Sounds.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.method_3686(abstractArrowEntity);
	}

	protected AbstractArrowEntity method_14056(float f) {
		ArrowEntity arrowEntity = new ArrowEntity(this.world, this);
		arrowEntity.applyEnchantmentEffects(this, f);
		return arrowEntity;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_14058();
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		super.equipStack(slot, stack);
		if (!this.world.isClient && slot == EquipmentSlot.MAINHAND) {
			this.method_14058();
		}
	}

	@Override
	public float getEyeHeight() {
		return 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return -0.6;
	}

	public boolean method_14059() {
		return this.dataTracker.get(field_15531);
	}

	@Override
	public void method_13246(boolean bl) {
		this.dataTracker.set(field_15531, bl);
	}
}
