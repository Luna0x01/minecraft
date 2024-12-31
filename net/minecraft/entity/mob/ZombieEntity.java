package net.minecraft.entity.mob;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3040;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.class_2974;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
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
import net.minecraft.world.biome.DesertBiome;

public class ZombieEntity extends HostileEntity {
	protected static final EntityAttribute REINFORCEMENTS_ATTRIBUTE = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
		.setName("Spawn Reinforcements Chance");
	private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier BABY_SPEED_BOOST_MODIFIER = new AttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, 1);
	private static final TrackedData<Boolean> field_14785 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_14786 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_14787 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_14788 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this);
	private int ticksUntilConversion;
	private boolean canBreakDoors;
	private float boundWidth = -1.0F;
	private float boundHeight;

	public ZombieEntity(World world) {
		super(world);
		this.setBounds(0.6F, 1.95F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(2, new class_2974(this, 1.0, false));
		this.goals.add(5, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(7, new WanderAroundGoal(this, 1.0));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.goals.add(6, new MoveThroughVillageGoal(this, 1.0, false));
		this.attackGoals.add(1, new RevengeGoal(this, true, ZombiePigmanEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, VillagerEntity.class, false));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(35.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23F);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0);
		this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).setBaseValue(2.0);
		this.getAttributeContainer().register(REINFORCEMENTS_ATTRIBUTE).setBaseValue(this.random.nextDouble() * 0.1F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(field_14785, false);
		this.getDataTracker().startTracking(field_14786, 0);
		this.getDataTracker().startTracking(field_14787, false);
		this.getDataTracker().startTracking(field_14788, false);
	}

	public void method_13246(boolean bl) {
		this.getDataTracker().set(field_14788, bl);
	}

	public boolean method_13247() {
		return this.getDataTracker().get(field_14788);
	}

	public boolean canBreakDoors() {
		return this.canBreakDoors;
	}

	public void setCanBreakDoors(boolean canBreakDoors) {
		if (this.canBreakDoors != canBreakDoors) {
			this.canBreakDoors = canBreakDoors;
			((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
			if (canBreakDoors) {
				this.goals.add(1, this.breakDoorsGoal);
			} else {
				this.goals.method_4497(this.breakDoorsGoal);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(field_14785);
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.isBaby()) {
			this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
		}

		return super.getXpToDrop(player);
	}

	public void setBaby(boolean baby) {
		this.getDataTracker().set(field_14785, baby);
		if (this.world != null && !this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.method_6193(BABY_SPEED_BOOST_MODIFIER);
			if (baby) {
				entityAttributeInstance.addModifier(BABY_SPEED_BOOST_MODIFIER);
			}
		}

		this.changeType(baby);
	}

	public class_3040 method_13552() {
		return class_3040.method_13554(this.getDataTracker().get(field_14786));
	}

	public boolean isVillager() {
		return this.method_13552().method_13555();
	}

	public int method_13248() {
		return this.method_13552().method_13557();
	}

	public void method_13550(class_3040 arg) {
		this.getDataTracker().set(field_14786, arg.method_13553());
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14785.equals(data)) {
			this.changeType(this.isBaby());
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void tickMovement() {
		if (this.world.isDay() && !this.world.isClient && !this.isBaby() && this.method_13552().method_13559()) {
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
				&& (double)this.random.nextFloat() < this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).getValue()
				&& this.world.getGameRules().getBoolean("doMobSpawning")) {
				int i = MathHelper.floor(this.x);
				int j = MathHelper.floor(this.y);
				int k = MathHelper.floor(this.z);
				ZombieEntity zombieEntity = new ZombieEntity(this.world);

				for (int l = 0; l < 50; l++) {
					int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					if (this.world.getBlockState(new BlockPos(m, n - 1, o)).method_11739() && this.world.getLightLevelWithNeighbours(new BlockPos(m, n, o)) < 10) {
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
			float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
			if (this.getMainHandStack() == null) {
				if (this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
					target.setOnFireFor(2 * (int)f);
				}

				if (this.method_13552() == class_3040.HUSK && target instanceof LivingEntity) {
					((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f));
				}
			}
		}

		return bl;
	}

	@Override
	protected Sound ambientSound() {
		return this.method_13552().method_13560();
	}

	@Override
	protected Sound method_13048() {
		return this.method_13552().method_13561();
	}

	@Override
	protected Sound deathSound() {
		return this.method_13552().method_13562();
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		Sound sound = this.method_13552().method_13563();
		this.playSound(sound, 0.15F, 1.0F);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ZOMBIE_ENTITIE;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		if (this.random.nextFloat() < (this.world.getGlobalDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.random.nextInt(3);
			if (i == 0) {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			} else {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
			}
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Zombie");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isBaby()) {
			nbt.putBoolean("IsBaby", true);
		}

		nbt.putInt("ZombieType", this.method_13552().method_13553());
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
			if (nbt.contains("VillagerProfession", 99)) {
				this.method_13550(class_3040.method_13554(nbt.getInt("VillagerProfession") + 1));
			} else {
				this.method_13550(class_3040.method_13554(this.world.random.nextInt(5) + 1));
			}
		}

		if (nbt.contains("ZombieType")) {
			this.method_13550(class_3040.method_13554(nbt.getInt("ZombieType")));
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

			VillagerEntity villagerEntity = (VillagerEntity)other;
			ZombieEntity zombieEntity = new ZombieEntity(this.world);
			zombieEntity.copyPosition(other);
			this.world.removeEntity(other);
			zombieEntity.initialize(this.world.getLocalDifficulty(new BlockPos(zombieEntity)), new ZombieEntity.Data(false, true));
			zombieEntity.method_13550(class_3040.method_13554(villagerEntity.profession() + 1));
			zombieEntity.setBaby(other.isBaby());
			zombieEntity.setAiDisabled(villagerEntity.hasNoAi());
			if (villagerEntity.hasCustomName()) {
				zombieEntity.setCustomName(villagerEntity.getCustomName());
				zombieEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
			}

			this.world.spawnEntity(zombieEntity);
			this.world.syncWorldEvent(null, 1026, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
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
		return stack.getItem() == Items.EGG && this.isBaby() && this.hasMount() ? false : super.canPickupItem(stack);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		float f = difficulty.getClampedLocalDifficulty();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
		if (data == null) {
			data = new ZombieEntity.Data(this.world.random.nextFloat() < 0.05F, this.world.random.nextFloat() < 0.05F);
		}

		if (data instanceof ZombieEntity.Data) {
			ZombieEntity.Data data2 = (ZombieEntity.Data)data;
			boolean bl = false;
			Biome biome = this.world.getBiome(new BlockPos(this));
			if (biome instanceof DesertBiome && this.world.hasDirectSunlight(new BlockPos(this)) && this.random.nextInt(5) != 0) {
				this.method_13550(class_3040.HUSK);
				bl = true;
			}

			if (!bl && data2.field_6929) {
				this.method_13550(class_3040.method_13554(this.random.nextInt(5) + 1));
			}

			if (data2.field_6928) {
				this.setBaby(true);
				if ((double)this.world.random.nextFloat() < 0.05) {
					List<ChickenEntity> list = this.world.getEntitiesInBox(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicate.NOT_MOUNTED);
					if (!list.isEmpty()) {
						ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
						chickenEntity.setHasJockey(true);
						this.ride(chickenEntity);
					}
				} else if ((double)this.world.random.nextFloat() < 0.05) {
					ChickenEntity chickenEntity2 = new ChickenEntity(this.world);
					chickenEntity2.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					chickenEntity2.initialize(difficulty, null);
					chickenEntity2.setHasJockey(true);
					this.world.spawnEntity(chickenEntity2);
					this.ride(chickenEntity2);
				}
			}
		}

		this.setCanBreakDoors(this.random.nextFloat() < f * 0.1F);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		if (this.getStack(EquipmentSlot.HEAD) == null) {
			Calendar calendar = this.world.getCalenderInstance();
			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				this.field_14559[EquipmentSlot.HEAD.method_13032()] = 0.0F;
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
	public boolean method_13079(PlayerEntity playerEntity, Hand hand, @Nullable ItemStack itemStack) {
		if (itemStack != null
			&& itemStack.getItem() == Items.GOLDEN_APPLE
			&& itemStack.getData() == 0
			&& this.isVillager()
			&& this.hasStatusEffect(StatusEffects.WEAKNESS)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.count--;
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
		this.getDataTracker().set(field_14787, true);
		this.removeStatusEffect(StatusEffects.WEAKNESS);
		this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, ticks, Math.min(this.world.getGlobalDifficulty().getId() - 1, 0)));
		this.world.sendEntityStatus(this, (byte)16);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 16) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						Sounds.ENTITY_ZOMBIE_VILLAGER_CURE,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
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
		return this.getDataTracker().get(field_14787);
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
		villagerEntity.setProfession(this.method_13248());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(villagerEntity);
		villagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
		this.world.syncWorldEvent(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
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

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.getCustomName() : this.method_13552().method_13558().asUnformattedString();
	}

	class Data implements EntityData {
		public boolean field_6928;
		public boolean field_6929;

		private Data(boolean bl, boolean bl2) {
			this.field_6928 = bl;
			this.field_6929 = bl2;
		}
	}
}
