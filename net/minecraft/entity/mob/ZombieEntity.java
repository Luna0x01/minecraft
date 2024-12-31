package net.minecraft.entity.mob;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
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
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
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
	private static final TrackedData<Boolean> field_14785 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_14786 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_14788 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this);
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
		this.goals.add(7, new class_3133(this, 1.0));
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

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14785.equals(data)) {
			this.changeType(this.isBaby());
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void tickMovement() {
		if (this.world.isDay() && !this.world.isClient && !this.isBaby() && this.method_13605()) {
			float f = this.getBrightnessAtEyes(1.0F);
			if (f > 0.5F
				&& this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
				&& this.world.hasDirectSunlight(new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z))) {
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

	protected boolean method_13605() {
		return true;
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
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
			if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				target.setOnFireFor(2 * (int)f);
			}
		}

		return bl;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_ZOMBIE_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_ZOMBIE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ZOMBIE_DEATH;
	}

	protected Sound getStepSound() {
		return Sounds.ENTITY_ZOMBIE_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
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
		MobEntity.registerDataFixes(dataFixer, ZombieEntity.class);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isBaby()) {
			nbt.putBoolean("IsBaby", true);
		}

		nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.getBoolean("IsBaby")) {
			this.setBaby(true);
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
			ZombieVillagerEntity zombieVillagerEntity = new ZombieVillagerEntity(this.world);
			zombieVillagerEntity.copyPosition(villagerEntity);
			this.world.removeEntity(villagerEntity);
			zombieVillagerEntity.initialize(this.world.getLocalDifficulty(new BlockPos(zombieVillagerEntity)), new ZombieEntity.Data(false));
			zombieVillagerEntity.method_13606(villagerEntity.profession());
			zombieVillagerEntity.setBaby(villagerEntity.isBaby());
			zombieVillagerEntity.setAiDisabled(villagerEntity.hasNoAi());
			if (villagerEntity.hasCustomName()) {
				zombieVillagerEntity.setCustomName(villagerEntity.getCustomName());
				zombieVillagerEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
			}

			this.world.spawnEntity(zombieVillagerEntity);
			this.world.syncWorldEvent(null, 1026, new BlockPos(this), 0);
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
			data = new ZombieEntity.Data(this.world.random.nextFloat() < 0.05F);
		}

		if (data instanceof ZombieEntity.Data) {
			ZombieEntity.Data data2 = (ZombieEntity.Data)data;
			if (data2.field_15074) {
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
		if (this.getStack(EquipmentSlot.HEAD).isEmpty()) {
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
		return this.isBaby() ? 0.0 : -0.45;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getAttacker() instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)source.getAttacker();
			if (creeperEntity.method_3074() && creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				ItemStack itemStack = this.getSkull();
				if (!itemStack.isEmpty()) {
					this.dropItem(itemStack, 0.0F);
				}
			}
		}
	}

	protected ItemStack getSkull() {
		return new ItemStack(Items.SKULL, 1, 2);
	}

	class Data implements EntityData {
		public boolean field_15074;

		private Data(boolean bl) {
			this.field_15074 = bl;
		}
	}
}
