package net.minecraft.entity.mob;

import java.util.Calendar;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
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
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheNetherDimension;

public class SkeletonEntity extends HostileEntity implements RangedAttackMob {
	private ProjectileAttackGoal projectileAttackGoal = new ProjectileAttackGoal(this, 1.0, 20, 60, 15.0F);
	private MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, PlayerEntity.class, 1.2, false);

	public SkeletonEntity(World world) {
		super(world);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new AvoidSunlightGoal(this));
		this.goals.add(3, new EscapeSunlightGoal(this, 1.0));
		this.goals.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.goals.add(4, new WanderAroundGoal(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(6, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		if (world != null && !world.isClient) {
			this.updateAttackType();
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(13, new Byte((byte)0));
	}

	@Override
	protected String getAmbientSound() {
		return "mob.skeleton.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.skeleton.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.skeleton.death";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.skeleton.step", 0.15F, 1.0F);
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (super.tryAttack(target)) {
			if (this.getType() == 1 && target instanceof LivingEntity) {
				((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffect.WITHER.id, 200));
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
			BlockPos blockPos = new BlockPos(this.x, (double)Math.round(this.y), this.z);
			if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.hasDirectSunlight(blockPos)) {
				boolean bl = true;
				ItemStack itemStack = this.getMainSlot(4);
				if (itemStack != null) {
					if (itemStack.isDamageable()) {
						itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
						if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
							this.method_6111(itemStack);
							this.setArmorSlot(4, null);
						}
					}

					bl = false;
				}

				if (bl) {
					this.setOnFireFor(8);
				}
			}
		}

		if (this.world.isClient && this.getType() == 1) {
			this.setBounds(0.72F, 2.535F);
		}

		super.tickMovement();
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		if (this.vehicle instanceof PathAwareEntity) {
			PathAwareEntity pathAwareEntity = (PathAwareEntity)this.vehicle;
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
			this.dropItem(new ItemStack(Items.SKULL, 1, this.getType() == 1 ? 1 : 0), 0.0F);
		}
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.ARROW;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		if (this.getType() == 1) {
			int i = this.random.nextInt(3 + lootingMultiplier) - 1;

			for (int j = 0; j < i; j++) {
				this.dropItem(Items.COAL, 1);
			}
		} else {
			int k = this.random.nextInt(3 + lootingMultiplier);

			for (int l = 0; l < k; l++) {
				this.dropItem(Items.ARROW, 1);
			}
		}

		int m = this.random.nextInt(3 + lootingMultiplier);

		for (int n = 0; n < m; n++) {
			this.dropItem(Items.BONE, 1);
		}
	}

	@Override
	protected void method_4473() {
		if (this.getType() == 1) {
			this.dropItem(new ItemStack(Items.SKULL, 1, 1), 0.0F);
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		this.setArmorSlot(0, new ItemStack(Items.BOW));
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		data = super.initialize(difficulty, data);
		if (this.world.dimension instanceof TheNetherDimension && this.getRandom().nextInt(5) > 0) {
			this.goals.add(4, this.meleeAttackGoal);
			this.setType(1);
			this.setArmorSlot(0, new ItemStack(Items.STONE_SWORD));
			this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
		} else {
			this.goals.add(4, this.projectileAttackGoal);
			this.initEquipment(difficulty);
			this.updateEnchantments(difficulty);
		}

		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
		if (this.getMainSlot(4) == null) {
			Calendar calendar = this.world.getCalenderInstance();
			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
				this.setArmorSlot(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				this.armorDropChances[4] = 0.0F;
			}
		}

		return data;
	}

	public void updateAttackType() {
		this.goals.method_4497(this.meleeAttackGoal);
		this.goals.method_4497(this.projectileAttackGoal);
		ItemStack itemStack = this.getStackInHand();
		if (itemStack != null && itemStack.getItem() == Items.BOW) {
			this.goals.add(4, this.projectileAttackGoal);
		} else {
			this.goals.add(4, this.meleeAttackGoal);
		}
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		AbstractArrowEntity abstractArrowEntity = new AbstractArrowEntity(this.world, this, target, 1.6F, (float)(14 - this.world.getGlobalDifficulty().getId() * 4));
		int i = EnchantmentHelper.getLevel(Enchantment.POWER.id, this.getStackInHand());
		int j = EnchantmentHelper.getLevel(Enchantment.PUNCH.id, this.getStackInHand());
		abstractArrowEntity.setDamage(
			(double)(pullProgress * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.getGlobalDifficulty().getId() * 0.11F)
		);
		if (i > 0) {
			abstractArrowEntity.setDamage(abstractArrowEntity.getDamage() + (double)i * 0.5 + 0.5);
		}

		if (j > 0) {
			abstractArrowEntity.setPunch(j);
		}

		if (EnchantmentHelper.getLevel(Enchantment.FLAME.id, this.getStackInHand()) > 0 || this.getType() == 1) {
			abstractArrowEntity.setOnFireFor(100);
		}

		this.playSound("random.bow", 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(abstractArrowEntity);
	}

	public int getType() {
		return this.dataTracker.getByte(13);
	}

	public void setType(int type) {
		this.dataTracker.setProperty(13, (byte)type);
		this.isFireImmune = type == 1;
		if (type == 1) {
			this.setBounds(0.72F, 2.535F);
		} else {
			this.setBounds(0.6F, 1.95F);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("SkeletonType", 99)) {
			int i = nbt.getByte("SkeletonType");
			this.setType(i);
		}

		this.updateAttackType();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("SkeletonType", (byte)this.getType());
	}

	@Override
	public void setArmorSlot(int armorSlot, ItemStack item) {
		super.setArmorSlot(armorSlot, item);
		if (!this.world.isClient && armorSlot == 0) {
			this.updateAttackType();
		}
	}

	@Override
	public float getEyeHeight() {
		return this.getType() == 1 ? super.getEyeHeight() : 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? 0.0 : -0.35;
	}
}
