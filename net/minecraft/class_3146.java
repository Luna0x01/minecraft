package net.minecraft;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.ai.goal.class_2973;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class class_3146 extends HostileEntity implements RangedAttackMob {
	private static final TrackedData<Boolean> field_15531 = DataTracker.registerData(class_3146.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final class_2973 field_15532 = new class_2973(this, 1.0, 20, 15.0F);
	private final MeleeAttackGoal field_15533 = new MeleeAttackGoal(this, 1.2, false) {
		@Override
		public void stop() {
			super.stop();
			class_3146.this.method_14057(false);
		}

		@Override
		public void start() {
			super.start();
			class_3146.this.method_14057(true);
		}
	};

	public class_3146(World world) {
		super(world);
		this.setBounds(0.6F, 1.99F);
		this.method_14058();
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new AvoidSunlightGoal(this));
		this.goals.add(3, new EscapeSunlightGoal(this, 1.0));
		this.goals.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.goals.add(5, new class_3133(this, 1.0));
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
		this.dataTracker.startTracking(field_15531, false);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(this.method_14060(), 0.15F, 1.0F);
	}

	abstract Sound method_14060();

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
		}
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
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		this.method_14058();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
		if (this.getStack(EquipmentSlot.HEAD).isEmpty()) {
			Calendar calendar = this.world.getCalenderInstance();
			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				this.field_14559[EquipmentSlot.HEAD.method_13032()] = 0.0F;
			}
		}

		return data;
	}

	public void method_14058() {
		if (this.world != null && !this.world.isClient) {
			this.goals.method_4497(this.field_15533);
			this.goals.method_4497(this.field_15532);
			ItemStack itemStack = this.getMainHandStack();
			if (itemStack.getItem() == Items.BOW) {
				int i = 20;
				if (this.world.getGlobalDifficulty() != Difficulty.HARD) {
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
		abstractArrowEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.world.getGlobalDifficulty().getId() * 4));
		this.playSound(Sounds.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(abstractArrowEntity);
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

	public void method_14057(boolean bl) {
		this.dataTracker.set(field_15531, bl);
	}
}
