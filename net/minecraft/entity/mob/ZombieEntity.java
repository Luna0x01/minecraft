package net.minecraft.entity.mob;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieEntity extends HostileEntity {
	protected static final EntityAttribute REINFORCEMENTS_ATTRIBUTE = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
		.setName("Spawn Reinforcements Chance");
	private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier BABY_SPEED_BOOST_MODIFIER = new AttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, 1);
	private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this);
	private int ticksUntilConversion;
	private boolean canBreakDoors = false;
	private float boundWidth = -1.0F;
	private float boundHeight;

	public ZombieEntity(World world) {
		super(world);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(2, new MeleeAttackGoal(this, PlayerEntity.class, 1.0, false));
		this.goals.add(5, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(7, new WanderAroundGoal(this, 1.0));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.initCustomGoals();
		this.setBounds(0.6F, 1.95F);
	}

	protected void initCustomGoals() {
		this.goals.add(4, new MeleeAttackGoal(this, VillagerEntity.class, 1.0, true));
		this.goals.add(4, new MeleeAttackGoal(this, IronGolemEntity.class, 1.0, true));
		this.goals.add(6, new MoveThroughVillageGoal(this, 1.0, false));
		this.attackGoals.add(1, new RevengeGoal(this, true, ZombiePigmanEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(2, new FollowTargetGoal(this, VillagerEntity.class, false));
		this.attackGoals.add(2, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(35.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23F);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0);
		this.getAttributeContainer().register(REINFORCEMENTS_ATTRIBUTE).setBaseValue(this.random.nextDouble() * 0.1F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().track(12, (byte)0);
		this.getDataTracker().track(13, (byte)0);
		this.getDataTracker().track(14, (byte)0);
	}

	@Override
	public int getArmorProtectionValue() {
		int i = super.getArmorProtectionValue() + 2;
		if (i > 20) {
			i = 20;
		}

		return i;
	}

	public boolean canBreakDoors() {
		return this.canBreakDoors;
	}

	public void setCanBreakDoors(boolean canBreakDoors) {
		if (this.canBreakDoors != canBreakDoors) {
			this.canBreakDoors = canBreakDoors;
			if (canBreakDoors) {
				this.goals.add(1, this.breakDoorsGoal);
			} else {
				this.goals.method_4497(this.breakDoorsGoal);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().getByte(12) == 1;
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.isBaby()) {
			this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
		}

		return super.getXpToDrop(player);
	}

	public void setBaby(boolean baby) {
		this.getDataTracker().setProperty(12, (byte)(baby ? 1 : 0));
		if (this.world != null && !this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.method_6193(BABY_SPEED_BOOST_MODIFIER);
			if (baby) {
				entityAttributeInstance.addModifier(BABY_SPEED_BOOST_MODIFIER);
			}
		}

		this.changeType(baby);
	}

	public boolean isVillager() {
		return this.getDataTracker().getByte(13) == 1;
	}

	public void setVillager(boolean bl) {
		this.getDataTracker().setProperty(13, (byte)(bl ? 1 : 0));
	}

	@Override
	public void tickMovement() {
		if (this.world.isDay() && !this.world.isClient && !this.isBaby()) {
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

		if (this.hasVehicle() && this.getTarget() != null && this.vehicle instanceof ChickenEntity) {
			((MobEntity)this.vehicle).getNavigation().startMovingAlong(this.getNavigation().getCurrentPath(), 1.5);
		}

		super.tickMovement();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (super.damage(source, amount)) {
			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
				livingEntity = (LivingEntity)source.getAttacker();
			}

			if (livingEntity != null
				&& this.world.getGlobalDifficulty() == Difficulty.HARD
				&& (double)this.random.nextFloat() < this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).getValue()) {
				int i = MathHelper.floor(this.x);
				int j = MathHelper.floor(this.y);
				int k = MathHelper.floor(this.z);
				ZombieEntity zombieEntity = new ZombieEntity(this.world);

				for (int l = 0; l < 50; l++) {
					int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					if (World.isOpaque(this.world, new BlockPos(m, n - 1, o)) && this.world.getLightLevelWithNeighbours(new BlockPos(m, n, o)) < 10) {
						zombieEntity.updatePosition((double)m, (double)n, (double)o);
						if (!this.world.isPlayerInRange((double)m, (double)n, (double)o, 7.0)
							&& this.world.hasEntityIn(zombieEntity.getBoundingBox(), zombieEntity)
							&& this.world.doesBoxCollide(zombieEntity, zombieEntity.getBoundingBox()).isEmpty()
							&& !this.world.containsFluid(zombieEntity.getBoundingBox())) {
							this.world.spawnEntity(zombieEntity);
							zombieEntity.setTarget(livingEntity);
							zombieEntity.initialize(this.world.getLocalDifficulty(new BlockPos(zombieEntity)), null);
							this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05F, 0));
							zombieEntity.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05F, 0));
							break;
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.getConversionType()) {
			int i = this.method_4564();
			this.ticksUntilConversion -= i;
			if (this.ticksUntilConversion <= 0) {
				this.convertInWater();
			}
		}

		super.tick();
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			int i = this.world.getGlobalDifficulty().getId();
			if (this.getStackInHand() == null && this.isOnFire() && this.random.nextFloat() < (float)i * 0.3F) {
				target.setOnFireFor(2 * i);
			}
		}

		return bl;
	}

	@Override
	protected String getAmbientSound() {
		return "mob.zombie.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.zombie.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.zombie.death";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.zombie.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.ROTTEN_FLESH;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected void method_4473() {
		switch (this.random.nextInt(3)) {
			case 0:
				this.dropItem(Items.IRON_INGOT, 1);
				break;
			case 1:
				this.dropItem(Items.CARROT, 1);
				break;
			case 2:
				this.dropItem(Items.POTATO, 1);
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		if (this.random.nextFloat() < (this.world.getGlobalDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.random.nextInt(3);
			if (i == 0) {
				this.setArmorSlot(0, new ItemStack(Items.IRON_SWORD));
			} else {
				this.setArmorSlot(0, new ItemStack(Items.IRON_SHOVEL));
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isBaby()) {
			nbt.putBoolean("IsBaby", true);
		}

		if (this.isVillager()) {
			nbt.putBoolean("IsVillager", true);
		}

		nbt.putInt("ConversionTime", this.getConversionType() ? this.ticksUntilConversion : -1);
		nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.getBoolean("IsBaby")) {
			this.setBaby(true);
		}

		if (nbt.getBoolean("IsVillager")) {
			this.setVillager(true);
		}

		if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
			this.setConversionTime(nbt.getInt("ConversionTime"));
		}

		this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));
	}

	@Override
	public void onKilledOther(LivingEntity other) {
		super.onKilledOther(other);
		if ((this.world.getGlobalDifficulty() == Difficulty.NORMAL || this.world.getGlobalDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity) {
			if (this.world.getGlobalDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
				return;
			}

			MobEntity mobEntity = (MobEntity)other;
			ZombieEntity zombieEntity = new ZombieEntity(this.world);
			zombieEntity.copyPosition(other);
			this.world.removeEntity(other);
			zombieEntity.initialize(this.world.getLocalDifficulty(new BlockPos(zombieEntity)), null);
			zombieEntity.setVillager(true);
			if (other.isBaby()) {
				zombieEntity.setBaby(true);
			}

			zombieEntity.setAiDisabled(mobEntity.hasNoAi());
			if (mobEntity.hasCustomName()) {
				zombieEntity.setCustomName(mobEntity.getCustomName());
				zombieEntity.setCustomNameVisible(mobEntity.isCustomNameVisible());
			}

			this.world.spawnEntity(zombieEntity);
			this.world.syncWorldEvent(null, 1016, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
		}
	}

	@Override
	public float getEyeHeight() {
		float f = 1.74F;
		if (this.isBaby()) {
			f = (float)((double)f - 0.81);
		}

		return f;
	}

	@Override
	protected boolean canPickupItem(ItemStack stack) {
		return stack.getItem() == Items.EGG && this.isBaby() && this.hasVehicle() ? false : super.canPickupItem(stack);
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		data = super.initialize(difficulty, data);
		float f = difficulty.getClampedLocalDifficulty();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
		if (data == null) {
			data = new ZombieEntity.Data(this.world.random.nextFloat() < 0.05F, this.world.random.nextFloat() < 0.05F);
		}

		if (data instanceof ZombieEntity.Data) {
			ZombieEntity.Data data2 = (ZombieEntity.Data)data;
			if (data2.field_6929) {
				this.setVillager(true);
			}

			if (data2.field_6928) {
				this.setBaby(true);
				if ((double)this.world.random.nextFloat() < 0.05) {
					List<ChickenEntity> list = this.world.getEntitiesInBox(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicate.NOT_MOUNTED);
					if (!list.isEmpty()) {
						ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
						chickenEntity.setHasJockey(true);
						this.startRiding(chickenEntity);
					}
				} else if ((double)this.world.random.nextFloat() < 0.05) {
					ChickenEntity chickenEntity2 = new ChickenEntity(this.world);
					chickenEntity2.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					chickenEntity2.initialize(difficulty, null);
					chickenEntity2.setHasJockey(true);
					this.world.spawnEntity(chickenEntity2);
					this.startRiding(chickenEntity2);
				}
			}
		}

		this.setCanBreakDoors(this.random.nextFloat() < f * 0.1F);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		if (this.getMainSlot(4) == null) {
			Calendar calendar = this.world.getCalenderInstance();
			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
				this.setArmorSlot(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				this.armorDropChances[4] = 0.0F;
			}
		}

		this.initializeAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
			.addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, 0));
		double d = this.random.nextDouble() * 1.5 * (double)f;
		if (d > 1.0) {
			this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", d, 2));
		}

		if (this.random.nextFloat() < f * 0.05F) {
			this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, 0));
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH)
				.addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, 2));
			this.setCanBreakDoors(true);
		}

		return data;
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.getMainHandStack();
		if (itemStack != null
			&& itemStack.getItem() == Items.GOLDEN_APPLE
			&& itemStack.getData() == 0
			&& this.isVillager()
			&& this.hasStatusEffect(StatusEffect.WEAKNESS)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.count--;
			}

			if (itemStack.count <= 0) {
				playerEntity.inventory.setInvStack(playerEntity.inventory.selectedSlot, null);
			}

			if (!this.world.isClient) {
				this.setConversionTime(this.random.nextInt(2401) + 3600);
			}

			return true;
		} else {
			return false;
		}
	}

	protected void setConversionTime(int ticks) {
		this.ticksUntilConversion = ticks;
		this.getDataTracker().setProperty(14, (byte)1);
		this.method_6149(StatusEffect.WEAKNESS.id);
		this.addStatusEffect(new StatusEffectInstance(StatusEffect.STRENGTH.id, ticks, Math.min(this.world.getGlobalDifficulty().getId() - 1, 0)));
		this.world.sendEntityStatus(this, (byte)16);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 16) {
			if (!this.isSilent()) {
				this.world
					.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, "mob.zombie.remedy", 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
			}
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return !this.getConversionType();
	}

	public boolean getConversionType() {
		return this.getDataTracker().getByte(14) == 1;
	}

	protected void convertInWater() {
		VillagerEntity villagerEntity = new VillagerEntity(this.world);
		villagerEntity.copyPosition(this);
		villagerEntity.initialize(this.world.getLocalDifficulty(new BlockPos(villagerEntity)), null);
		villagerEntity.method_4567();
		if (this.isBaby()) {
			villagerEntity.setAge(-24000);
		}

		this.world.removeEntity(this);
		villagerEntity.setAiDisabled(this.hasNoAi());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(villagerEntity);
		villagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffect.NAUSEA.id, 200, 0));
		this.world.syncWorldEvent(null, 1017, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected int method_4564() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = (int)this.x - 4; k < (int)this.x + 4 && j < 14; k++) {
				for (int l = (int)this.y - 4; l < (int)this.y + 4 && j < 14; l++) {
					for (int m = (int)this.z - 4; m < (int)this.z + 4 && j < 14; m++) {
						Block block = this.world.getBlockState(mutable.setPosition(k, l, m)).getBlock();
						if (block == Blocks.IRON_BARS || block == Blocks.BED) {
							if (this.random.nextFloat() < 0.3F) {
								i++;
							}

							j++;
						}
					}
				}
			}
		}

		return i;
	}

	public void changeType(boolean isBaby) {
		this.increaseBounds(isBaby ? 0.5F : 1.0F);
	}

	@Override
	protected final void setBounds(float width, float height) {
		boolean bl = this.boundWidth > 0.0F && this.boundHeight > 0.0F;
		this.boundWidth = width;
		this.boundHeight = height;
		if (!bl) {
			this.increaseBounds(1.0F);
		}
	}

	protected final void increaseBounds(float multi) {
		super.setBounds(this.boundWidth * multi, this.boundHeight * multi);
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? 0.0 : -0.35;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getAttacker() instanceof CreeperEntity
			&& !(this instanceof ZombiePigmanEntity)
			&& ((CreeperEntity)source.getAttacker()).method_3074()
			&& ((CreeperEntity)source.getAttacker()).shouldDropHead()) {
			((CreeperEntity)source.getAttacker()).onHeadDropped();
			this.dropItem(new ItemStack(Items.SKULL, 1, 2), 0.0F);
		}
	}

	class Data implements EntityData {
		public boolean field_6928 = false;
		public boolean field_6929 = false;

		private Data(boolean bl, boolean bl2) {
			this.field_6928 = bl;
			this.field_6929 = bl2;
		}
	}
}
