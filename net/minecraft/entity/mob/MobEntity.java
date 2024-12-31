package net.minecraft.entity.mob;

import java.util.UUID;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class MobEntity extends LivingEntity {
	public int ambientSoundChance;
	protected int experiencePoints;
	private LookControl lookControl;
	protected MoveControl entityMotionHelper;
	protected JumpControl jumpControl;
	private BodyControl bodyControl;
	protected EntityNavigation navigation;
	protected final GoalSelector goals;
	protected final GoalSelector attackGoals;
	private LivingEntity attackTarget;
	private MobVisibilityCache visibilityCache;
	private ItemStack[] armorInventory = new ItemStack[5];
	protected float[] armorDropChances = new float[5];
	private boolean pickUpLoot;
	private boolean persistent;
	private boolean leashed;
	private Entity leashOwner;
	private NbtCompound leash;

	public MobEntity(World world) {
		super(world);
		this.goals = new GoalSelector(world != null && world.profiler != null ? world.profiler : null);
		this.attackGoals = new GoalSelector(world != null && world.profiler != null ? world.profiler : null);
		this.lookControl = new LookControl(this);
		this.entityMotionHelper = new MoveControl(this);
		this.jumpControl = new JumpControl(this);
		this.bodyControl = new BodyControl(this);
		this.navigation = this.createNavigation(world);
		this.visibilityCache = new MobVisibilityCache(this);

		for (int i = 0; i < this.armorDropChances.length; i++) {
			this.armorDropChances[i] = 0.085F;
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(16.0);
	}

	protected EntityNavigation createNavigation(World world) {
		return new MobNavigation(this, world);
	}

	public LookControl getLookControl() {
		return this.lookControl;
	}

	public MoveControl getMotionHelper() {
		return this.entityMotionHelper;
	}

	public JumpControl getJumpControl() {
		return this.jumpControl;
	}

	public EntityNavigation getNavigation() {
		return this.navigation;
	}

	public MobVisibilityCache getVisibilityCache() {
		return this.visibilityCache;
	}

	public LivingEntity getTarget() {
		return this.attackTarget;
	}

	public void setTarget(LivingEntity target) {
		this.attackTarget = target;
	}

	public boolean canAttackEntity(Class<? extends LivingEntity> clazz) {
		return clazz != GhastEntity.class;
	}

	public void onEatingGrass() {
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(15, (byte)0);
	}

	public int getMinAmbientSoundDelay() {
		return 80;
	}

	public void playAmbientSound() {
		String string = this.getAmbientSound();
		if (string != null) {
			this.playSound(string, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.world.profiler.push("mobBaseTick");
		if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
			this.ambientSoundChance = -this.getMinAmbientSoundDelay();
			this.playAmbientSound();
		}

		this.world.profiler.pop();
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.experiencePoints > 0) {
			int i = this.experiencePoints;
			ItemStack[] itemStacks = this.getArmorStacks();

			for (int j = 0; j < itemStacks.length; j++) {
				if (itemStacks[j] != null && this.armorDropChances[j] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			return i;
		} else {
			return this.experiencePoints;
		}
	}

	public void playSpawnEffects() {
		if (this.world.isClient) {
			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				double g = 10.0;
				this.world
					.addParticle(
						ParticleType.EXPLOSION,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width - d * g,
						this.y + (double)(this.random.nextFloat() * this.height) - e * g,
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width - f * g,
						d,
						e,
						f
					);
			}
		} else {
			this.world.sendEntityStatus(this, (byte)20);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 20) {
			this.playSpawnEffects();
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient) {
			this.updateLeash();
		}
	}

	@Override
	protected float turnHead(float bodyRotation, float headRotation) {
		this.bodyControl.tick();
		return headRotation;
	}

	protected String getAmbientSound() {
		return null;
	}

	protected Item getDefaultDrop() {
		return null;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		Item item = this.getDefaultDrop();
		if (item != null) {
			int i = this.random.nextInt(3);
			if (lootingMultiplier > 0) {
				i += this.random.nextInt(lootingMultiplier + 1);
			}

			for (int j = 0; j < i; j++) {
				this.dropItem(item, 1);
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("CanPickUpLoot", this.canPickUpLoot());
		nbt.putBoolean("PersistenceRequired", this.persistent);
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.armorInventory.length; i++) {
			NbtCompound nbtCompound = new NbtCompound();
			if (this.armorInventory[i] != null) {
				this.armorInventory[i].toNbt(nbtCompound);
			}

			nbtList.add(nbtCompound);
		}

		nbt.put("Equipment", nbtList);
		NbtList nbtList2 = new NbtList();

		for (int j = 0; j < this.armorDropChances.length; j++) {
			nbtList2.add(new NbtFloat(this.armorDropChances[j]));
		}

		nbt.put("DropChances", nbtList2);
		nbt.putBoolean("Leashed", this.leashed);
		if (this.leashOwner != null) {
			NbtCompound nbtCompound2 = new NbtCompound();
			if (this.leashOwner instanceof LivingEntity) {
				nbtCompound2.putLong("UUIDMost", this.leashOwner.getUuid().getMostSignificantBits());
				nbtCompound2.putLong("UUIDLeast", this.leashOwner.getUuid().getLeastSignificantBits());
			} else if (this.leashOwner instanceof AbstractDecorationEntity) {
				BlockPos blockPos = ((AbstractDecorationEntity)this.leashOwner).getTilePos();
				nbtCompound2.putInt("X", blockPos.getX());
				nbtCompound2.putInt("Y", blockPos.getY());
				nbtCompound2.putInt("Z", blockPos.getZ());
			}

			nbt.put("Leash", nbtCompound2);
		}

		if (this.hasNoAi()) {
			nbt.putBoolean("NoAI", this.hasNoAi());
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("CanPickUpLoot", 1)) {
			this.setCanPickUpLoot(nbt.getBoolean("CanPickUpLoot"));
		}

		this.persistent = nbt.getBoolean("PersistenceRequired");
		if (nbt.contains("Equipment", 9)) {
			NbtList nbtList = nbt.getList("Equipment", 10);

			for (int i = 0; i < this.armorInventory.length; i++) {
				this.armorInventory[i] = ItemStack.fromNbt(nbtList.getCompound(i));
			}
		}

		if (nbt.contains("DropChances", 9)) {
			NbtList nbtList2 = nbt.getList("DropChances", 5);

			for (int j = 0; j < nbtList2.size(); j++) {
				this.armorDropChances[j] = nbtList2.getFloat(j);
			}
		}

		this.leashed = nbt.getBoolean("Leashed");
		if (this.leashed && nbt.contains("Leash", 10)) {
			this.leash = nbt.getCompound("Leash");
		}

		this.setAiDisabled(nbt.getBoolean("NoAI"));
	}

	public void setForwardSpeed(float forwardSpeed) {
		this.forwardSpeed = forwardSpeed;
	}

	@Override
	public void setMovementSpeed(float movementSpeed) {
		super.setMovementSpeed(movementSpeed);
		this.setForwardSpeed(movementSpeed);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.world.profiler.push("looting");
		if (!this.world.isClient && this.canPickUpLoot() && !this.dead && this.world.getGameRules().getBoolean("mobGriefing")) {
			for (ItemEntity itemEntity : this.world.getEntitiesInBox(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0))) {
				if (!itemEntity.removed && itemEntity.getItemStack() != null && !itemEntity.cannotPickup()) {
					this.loot(itemEntity);
				}
			}
		}

		this.world.profiler.pop();
	}

	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getItemStack();
		int i = getEquipableSlot(itemStack);
		if (i > -1) {
			boolean bl = true;
			ItemStack itemStack2 = this.getMainSlot(i);
			if (itemStack2 != null) {
				if (i == 0) {
					if (itemStack.getItem() instanceof SwordItem && !(itemStack2.getItem() instanceof SwordItem)) {
						bl = true;
					} else if (itemStack.getItem() instanceof SwordItem && itemStack2.getItem() instanceof SwordItem) {
						SwordItem swordItem = (SwordItem)itemStack.getItem();
						SwordItem swordItem2 = (SwordItem)itemStack2.getItem();
						if (swordItem.getAttackDamage() != swordItem2.getAttackDamage()) {
							bl = swordItem.getAttackDamage() > swordItem2.getAttackDamage();
						} else {
							bl = itemStack.getData() > itemStack2.getData() || itemStack.hasNbt() && !itemStack2.hasNbt();
						}
					} else if (itemStack.getItem() instanceof BowItem && itemStack2.getItem() instanceof BowItem) {
						bl = itemStack.hasNbt() && !itemStack2.hasNbt();
					} else {
						bl = false;
					}
				} else if (itemStack.getItem() instanceof ArmorItem && !(itemStack2.getItem() instanceof ArmorItem)) {
					bl = true;
				} else if (itemStack.getItem() instanceof ArmorItem && itemStack2.getItem() instanceof ArmorItem) {
					ArmorItem armorItem = (ArmorItem)itemStack.getItem();
					ArmorItem armorItem2 = (ArmorItem)itemStack2.getItem();
					if (armorItem.protection != armorItem2.protection) {
						bl = armorItem.protection > armorItem2.protection;
					} else {
						bl = itemStack.getData() > itemStack2.getData() || itemStack.hasNbt() && !itemStack2.hasNbt();
					}
				} else {
					bl = false;
				}
			}

			if (bl && this.canPickupItem(itemStack)) {
				if (itemStack2 != null && this.random.nextFloat() - 0.1F < this.armorDropChances[i]) {
					this.dropItem(itemStack2, 0.0F);
				}

				if (itemStack.getItem() == Items.DIAMOND && item.getThrower() != null) {
					PlayerEntity playerEntity = this.world.getPlayerByName(item.getThrower());
					if (playerEntity != null) {
						playerEntity.incrementStat(AchievementsAndCriterions.DIAMONDS_TO_YOU);
					}
				}

				this.setArmorSlot(i, itemStack);
				this.armorDropChances[i] = 2.0F;
				this.persistent = true;
				this.sendPickup(item, 1);
				item.remove();
			}
		}
	}

	protected boolean canPickupItem(ItemStack stack) {
		return true;
	}

	protected boolean canImmediatelyDespawn() {
		return true;
	}

	protected void checkDespawn() {
		if (this.persistent) {
			this.despawnCounter = 0;
		} else {
			Entity entity = this.world.getClosestPlayer(this, -1.0);
			if (entity != null) {
				double d = entity.x - this.x;
				double e = entity.y - this.y;
				double f = entity.z - this.z;
				double g = d * d + e * e + f * f;
				if (this.canImmediatelyDespawn() && g > 16384.0) {
					this.remove();
				}

				if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && g > 1024.0 && this.canImmediatelyDespawn()) {
					this.remove();
				} else if (g < 1024.0) {
					this.despawnCounter = 0;
				}
			}
		}
	}

	@Override
	protected final void tickNewAi() {
		this.despawnCounter++;
		this.world.profiler.push("checkDespawn");
		this.checkDespawn();
		this.world.profiler.pop();
		this.world.profiler.push("sensing");
		this.visibilityCache.clear();
		this.world.profiler.pop();
		this.world.profiler.push("targetSelector");
		this.attackGoals.tick();
		this.world.profiler.pop();
		this.world.profiler.push("goalSelector");
		this.goals.tick();
		this.world.profiler.pop();
		this.world.profiler.push("navigation");
		this.navigation.tick();
		this.world.profiler.pop();
		this.world.profiler.push("mob tick");
		this.mobTick();
		this.world.profiler.pop();
		this.world.profiler.push("controls");
		this.world.profiler.push("move");
		this.entityMotionHelper.updateMovement();
		this.world.profiler.swap("look");
		this.lookControl.tick();
		this.world.profiler.swap("jump");
		this.jumpControl.tick();
		this.world.profiler.pop();
		this.world.profiler.pop();
	}

	protected void mobTick() {
	}

	public int getLookPitchSpeed() {
		return 40;
	}

	public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
		double d = targetEntity.x - this.x;
		double e = targetEntity.z - this.z;
		double f;
		if (targetEntity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)targetEntity;
			f = livingEntity.y + (double)livingEntity.getEyeHeight() - (this.y + (double)this.getEyeHeight());
		} else {
			f = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - (this.y + (double)this.getEyeHeight());
		}

		double h = (double)MathHelper.sqrt(d * d + e * e);
		float i = (float)(MathHelper.atan2(e, d) * 180.0 / (float) Math.PI) - 90.0F;
		float j = (float)(-(MathHelper.atan2(f, h) * 180.0 / (float) Math.PI));
		this.pitch = this.changeAngle(this.pitch, j, maxPitchChange);
		this.yaw = this.changeAngle(this.yaw, i, maxYawChange);
	}

	private float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
		float f = MathHelper.wrapDegrees(newAngle - oldAngle);
		if (f > maxChangeInAngle) {
			f = maxChangeInAngle;
		}

		if (f < -maxChangeInAngle) {
			f = -maxChangeInAngle;
		}

		return oldAngle + f;
	}

	public boolean canSpawn() {
		return true;
	}

	public boolean hasNoSpawnCollisions() {
		return this.world.hasEntityIn(this.getBoundingBox(), this)
			&& this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()
			&& !this.world.containsFluid(this.getBoundingBox());
	}

	public float method_2638() {
		return 1.0F;
	}

	public int getLimitPerChunk() {
		return 4;
	}

	@Override
	public int getSafeFallDistance() {
		if (this.getTarget() == null) {
			return 3;
		} else {
			int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
			i -= (3 - this.world.getGlobalDifficulty().getId()) * 4;
			if (i < 0) {
				i = 0;
			}

			return i + 3;
		}
	}

	@Override
	public ItemStack getStackInHand() {
		return this.armorInventory[0];
	}

	@Override
	public ItemStack getMainSlot(int slot) {
		return this.armorInventory[slot];
	}

	@Override
	public ItemStack getArmorSlot(int i) {
		return this.armorInventory[i + 1];
	}

	@Override
	public void setArmorSlot(int armorSlot, ItemStack item) {
		this.armorInventory[armorSlot] = item;
	}

	@Override
	public ItemStack[] getArmorStacks() {
		return this.armorInventory;
	}

	@Override
	protected void method_4472(boolean bl, int i) {
		for (int j = 0; j < this.getArmorStacks().length; j++) {
			ItemStack itemStack = this.getMainSlot(j);
			boolean bl2 = this.armorDropChances[j] > 1.0F;
			if (itemStack != null && (bl || bl2) && this.random.nextFloat() - (float)i * 0.01F < this.armorDropChances[j]) {
				if (!bl2 && itemStack.isDamageable()) {
					int k = Math.max(itemStack.getMaxDamage() - 25, 1);
					int l = itemStack.getMaxDamage() - this.random.nextInt(this.random.nextInt(k) + 1);
					if (l > k) {
						l = k;
					}

					if (l < 1) {
						l = 1;
					}

					itemStack.setDamage(l);
				}

				this.dropItem(itemStack, 0.0F);
			}
		}
	}

	protected void initEquipment(LocalDifficulty difficulty) {
		if (this.random.nextFloat() < 0.15F * difficulty.getClampedLocalDifficulty()) {
			int i = this.random.nextInt(2);
			float f = this.world.getGlobalDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			for (int j = 3; j >= 0; j--) {
				ItemStack itemStack = this.getArmorSlot(j);
				if (j < 3 && this.random.nextFloat() < f) {
					break;
				}

				if (itemStack == null) {
					Item item = getArmorItem(j + 1, i);
					if (item != null) {
						this.setArmorSlot(j + 1, new ItemStack(item));
					}
				}
			}
		}
	}

	public static int getEquipableSlot(ItemStack stack) {
		if (stack.getItem() == Item.fromBlock(Blocks.PUMPKIN) || stack.getItem() == Items.SKULL) {
			return 4;
		} else {
			if (stack.getItem() instanceof ArmorItem) {
				switch (((ArmorItem)stack.getItem()).slot) {
					case 0:
						return 4;
					case 1:
						return 3;
					case 2:
						return 2;
					case 3:
						return 1;
				}
			}

			return 0;
		}
	}

	public static Item getArmorItem(int part, int material) {
		switch (part) {
			case 4:
				if (material == 0) {
					return Items.LEATHER_HELMET;
				} else if (material == 1) {
					return Items.GOLDEN_HELMET;
				} else if (material == 2) {
					return Items.CHAINMAIL_HELMET;
				} else if (material == 3) {
					return Items.IRON_HELMET;
				} else if (material == 4) {
					return Items.DIAMOND_HELMET;
				}
			case 3:
				if (material == 0) {
					return Items.LEATHER_CHESTPLATE;
				} else if (material == 1) {
					return Items.GOLDEN_CHESTPLATE;
				} else if (material == 2) {
					return Items.CHAINMAIL_CHESTPLATE;
				} else if (material == 3) {
					return Items.IRON_CHESTPLATE;
				} else if (material == 4) {
					return Items.DIAMOND_CHESTPLATE;
				}
			case 2:
				if (material == 0) {
					return Items.LEATHER_LEGGINGS;
				} else if (material == 1) {
					return Items.GOLDEN_LEGGINGS;
				} else if (material == 2) {
					return Items.CHAINMAIL_LEGGINGS;
				} else if (material == 3) {
					return Items.IRON_LEGGINGS;
				} else if (material == 4) {
					return Items.DIAMOND_LEGGINGS;
				}
			case 1:
				if (material == 0) {
					return Items.LEATHER_BOOTS;
				} else if (material == 1) {
					return Items.GOLDEN_BOOTS;
				} else if (material == 2) {
					return Items.CHAINMAIL_BOOTS;
				} else if (material == 3) {
					return Items.IRON_BOOTS;
				} else if (material == 4) {
					return Items.DIAMOND_BOOTS;
				}
			default:
				return null;
		}
	}

	protected void updateEnchantments(LocalDifficulty difficulty) {
		float f = difficulty.getClampedLocalDifficulty();
		if (this.getStackInHand() != null && this.random.nextFloat() < 0.25F * f) {
			EnchantmentHelper.addRandomEnchantment(this.random, this.getStackInHand(), (int)(5.0F + f * (float)this.random.nextInt(18)));
		}

		for (int i = 0; i < 4; i++) {
			ItemStack itemStack = this.getArmorSlot(i);
			if (itemStack != null && this.random.nextFloat() < 0.5F * f) {
				EnchantmentHelper.addRandomEnchantment(this.random, itemStack, (int)(5.0F + f * (float)this.random.nextInt(18)));
			}
		}
	}

	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE)
			.addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, 1));
		return data;
	}

	public boolean canBeControlledByRider() {
		return false;
	}

	public void setPersistent() {
		this.persistent = true;
	}

	public void method_5388(int i, float f) {
		this.armorDropChances[i] = f;
	}

	public boolean canPickUpLoot() {
		return this.pickUpLoot;
	}

	public void setCanPickUpLoot(boolean pickUpLoot) {
		this.pickUpLoot = pickUpLoot;
	}

	public boolean isPersistent() {
		return this.persistent;
	}

	@Override
	public final boolean openInventory(PlayerEntity player) {
		if (this.isLeashed() && this.getLeashOwner() == player) {
			this.detachLeash(true, !player.abilities.creativeMode);
			return true;
		} else {
			ItemStack itemStack = player.inventory.getMainHandStack();
			if (itemStack != null && itemStack.getItem() == Items.LEAD && this.isTameable()) {
				if (!(this instanceof TameableEntity) || !((TameableEntity)this).isTamed()) {
					this.attachLeash(player, true);
					itemStack.count--;
					return true;
				}

				if (((TameableEntity)this).isOwner(player)) {
					this.attachLeash(player, true);
					itemStack.count--;
					return true;
				}
			}

			return this.method_2537(player) ? true : super.openInventory(player);
		}
	}

	protected boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}

	protected void updateLeash() {
		if (this.leash != null) {
			this.method_6163();
		}

		if (this.leashed) {
			if (!this.isAlive()) {
				this.detachLeash(true, true);
			}

			if (this.leashOwner == null || this.leashOwner.removed) {
				this.detachLeash(true, true);
			}
		}
	}

	public void detachLeash(boolean sendPacket, boolean bl) {
		if (this.leashed) {
			this.leashed = false;
			this.leashOwner = null;
			if (!this.world.isClient && bl) {
				this.dropItem(Items.LEAD, 1);
			}

			if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
				((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityAttachS2CPacket(1, this, null));
			}
		}
	}

	public boolean isTameable() {
		return !this.isLeashed() && !(this instanceof Monster);
	}

	public boolean isLeashed() {
		return this.leashed;
	}

	public Entity getLeashOwner() {
		return this.leashOwner;
	}

	public void attachLeash(Entity entity, boolean bl) {
		this.leashed = true;
		this.leashOwner = entity;
		if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityAttachS2CPacket(1, this, this.leashOwner));
		}
	}

	private void method_6163() {
		if (this.leashed && this.leash != null) {
			if (this.leash.contains("UUIDMost", 4) && this.leash.contains("UUIDLeast", 4)) {
				UUID uUID = new UUID(this.leash.getLong("UUIDMost"), this.leash.getLong("UUIDLeast"));

				for (LivingEntity livingEntity : this.world.getEntitiesInBox(LivingEntity.class, this.getBoundingBox().expand(10.0, 10.0, 10.0))) {
					if (livingEntity.getUuid().equals(uUID)) {
						this.leashOwner = livingEntity;
						break;
					}
				}
			} else if (this.leash.contains("X", 99) && this.leash.contains("Y", 99) && this.leash.contains("Z", 99)) {
				BlockPos blockPos = new BlockPos(this.leash.getInt("X"), this.leash.getInt("Y"), this.leash.getInt("Z"));
				LeashKnotEntity leashKnotEntity = LeashKnotEntity.getOrCreate(this.world, blockPos);
				if (leashKnotEntity == null) {
					leashKnotEntity = LeashKnotEntity.create(this.world, blockPos);
				}

				this.leashOwner = leashKnotEntity;
			} else {
				this.detachLeash(false, true);
			}
		}

		this.leash = null;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		int i;
		if (slot == 99) {
			i = 0;
		} else {
			i = slot - 100 + 1;
			if (i < 0 || i >= this.armorInventory.length) {
				return false;
			}
		}

		if (item != null && getEquipableSlot(item) != i && (i != 4 || !(item.getItem() instanceof BlockItem))) {
			return false;
		} else {
			this.setArmorSlot(i, item);
			return true;
		}
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && !this.hasNoAi();
	}

	public void setAiDisabled(boolean value) {
		this.dataTracker.setProperty(15, Byte.valueOf((byte)(value ? 1 : 0)));
	}

	public boolean hasNoAi() {
		return this.dataTracker.getByte(15) != 0;
	}

	public static enum Location {
		ON_GROUND,
		IN_AIR,
		IN_WATER;
	}
}
