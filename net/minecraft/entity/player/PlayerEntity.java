package net.minecraft.entity.player;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_2686;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.Schema;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MultipartEntityProvider;
import net.minecraft.entity.ParrotEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public abstract class PlayerEntity extends LivingEntity {
	private static final TrackedData<Float> field_14792 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> field_14793 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Byte> field_14796 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> field_14797 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<NbtCompound> field_15625 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	protected static final TrackedData<NbtCompound> field_15626 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	public PlayerInventory inventory = new PlayerInventory(this);
	protected EnderChestInventory enderChest = new EnderChestInventory();
	public ScreenHandler playerScreenHandler;
	public ScreenHandler openScreenHandler;
	protected HungerManager hungerManager = new HungerManager();
	protected int abilityResyncCountdown;
	public float prevStrideDistance;
	public float strideDistance;
	public int experiencePickUpDelay;
	public double capeX;
	public double capeY;
	public double capeZ;
	public double prevCapeX;
	public double prevCapeY;
	public double prevCapeZ;
	protected boolean inBed;
	public BlockPos pos;
	private int sleepTimer;
	public float field_3993;
	public float field_4009;
	public float field_3994;
	private BlockPos spawnPos;
	private boolean spawnForced;
	public PlayerAbilities abilities = new PlayerAbilities();
	public int experienceLevel;
	public int totalExperience;
	public float experienceProgress;
	protected int enchantmentTableSeed;
	protected float field_4006 = 0.02F;
	private int lastPlayedLevelUpSoundTime;
	private final GameProfile gameProfile;
	private boolean reducedDebugInfo;
	private ItemStack field_14794 = ItemStack.EMPTY;
	private final class_2686 field_14795 = this.method_13272();
	@Nullable
	public FishingBobberEntity fishHook;

	protected class_2686 method_13272() {
		return new class_2686();
	}

	public PlayerEntity(World world, GameProfile gameProfile) {
		super(world);
		this.setUuid(getUuidFromProfile(gameProfile));
		this.gameProfile = gameProfile;
		this.playerScreenHandler = new PlayerScreenHandler(this.inventory, !world.isClient, this);
		this.openScreenHandler = this.playerScreenHandler;
		BlockPos blockPos = world.getSpawnPos();
		this.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
		this.field_6776 = 180.0F;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1F);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_SPEED);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_LUCK);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14792, 0.0F);
		this.dataTracker.startTracking(field_14793, 0);
		this.dataTracker.startTracking(field_14796, (byte)0);
		this.dataTracker.startTracking(field_14797, (byte)1);
		this.dataTracker.startTracking(field_15625, new NbtCompound());
		this.dataTracker.startTracking(field_15626, new NbtCompound());
	}

	@Override
	public void tick() {
		this.noClip = this.isSpectator();
		if (this.isSpectator()) {
			this.onGround = false;
		}

		if (this.experiencePickUpDelay > 0) {
			this.experiencePickUpDelay--;
		}

		if (this.isSleeping()) {
			this.sleepTimer++;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.world.isClient) {
				if (!this.method_3213()) {
					this.awaken(true, true, false);
				} else if (this.world.isDay()) {
					this.awaken(false, true, true);
				}
			}
		} else if (this.sleepTimer > 0) {
			this.sleepTimer++;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		super.tick();
		if (!this.world.isClient && this.openScreenHandler != null && !this.openScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.openScreenHandler = this.playerScreenHandler;
		}

		if (this.isOnFire() && this.abilities.invulnerable) {
			this.extinguish();
		}

		this.method_13274();
		if (!this.world.isClient) {
			this.hungerManager.update(this);
			this.incrementStat(Stats.MINUTES_PLAYED);
			if (this.isAlive()) {
				this.incrementStat(Stats.TIME_SINCE_DEATH);
			}

			if (this.isSneaking()) {
				this.incrementStat(Stats.SNEAK_TIME);
			}
		}

		int i = 29999999;
		double d = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
		double e = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
		if (d != this.x || e != this.z) {
			this.updatePosition(d, this.y, e);
		}

		this.field_14542++;
		ItemStack itemStack = this.getMainHandStack();
		if (!ItemStack.equalsAll(this.field_14794, itemStack)) {
			if (!ItemStack.equals(this.field_14794, itemStack)) {
				this.method_13269();
			}

			this.field_14794 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
		}

		this.field_14795.method_11381();
		this.method_13266();
	}

	private void method_13274() {
		this.capeX = this.prevCapeX;
		this.capeY = this.prevCapeY;
		this.capeZ = this.prevCapeZ;
		double d = this.x - this.prevCapeX;
		double e = this.y - this.prevCapeY;
		double f = this.z - this.prevCapeZ;
		double g = 10.0;
		if (d > 10.0) {
			this.prevCapeX = this.x;
			this.capeX = this.prevCapeX;
		}

		if (f > 10.0) {
			this.prevCapeZ = this.z;
			this.capeZ = this.prevCapeZ;
		}

		if (e > 10.0) {
			this.prevCapeY = this.y;
			this.capeY = this.prevCapeY;
		}

		if (d < -10.0) {
			this.prevCapeX = this.x;
			this.capeX = this.prevCapeX;
		}

		if (f < -10.0) {
			this.prevCapeZ = this.z;
			this.capeZ = this.prevCapeZ;
		}

		if (e < -10.0) {
			this.prevCapeY = this.y;
			this.capeY = this.prevCapeY;
		}

		this.prevCapeX += d * 0.25;
		this.prevCapeZ += f * 0.25;
		this.prevCapeY += e * 0.25;
	}

	protected void method_13266() {
		float f;
		float g;
		if (this.method_13055()) {
			f = 0.6F;
			g = 0.6F;
		} else if (this.isSleeping()) {
			f = 0.2F;
			g = 0.2F;
		} else if (this.isSneaking()) {
			f = 0.6F;
			g = 1.65F;
		} else {
			f = 0.6F;
			g = 1.8F;
		}

		if (f != this.width || g != this.height) {
			Box box = this.getBoundingBox();
			box = new Box(box.minX, box.minY, box.minZ, box.minX + (double)f, box.minY + (double)g, box.minZ + (double)f);
			if (!this.world.method_11488(box)) {
				this.setBounds(f, g);
			}
		}
	}

	@Override
	public int getMaxNetherPortalTime() {
		return this.abilities.invulnerable ? 1 : 80;
	}

	@Override
	protected Sound method_12984() {
		return Sounds.ENTITY_PLAYER_SWIM;
	}

	@Override
	protected Sound method_12985() {
		return Sounds.ENTITY_PLAYER_SPLASH;
	}

	@Override
	public int getDefaultNetherPortalCooldown() {
		return 10;
	}

	@Override
	public void playSound(Sound event, float volume, float pitch) {
		this.world.playSound(this, this.x, this.y, this.z, event, this.getSoundCategory(), volume, pitch);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.PLAYERS;
	}

	@Override
	protected int getBurningDuration() {
		return 20;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 9) {
			this.method_3217();
		} else if (status == 23) {
			this.reducedDebugInfo = false;
		} else if (status == 22) {
			this.reducedDebugInfo = true;
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected boolean method_2610() {
		return this.getHealth() <= 0.0F || this.isSleeping();
	}

	protected void closeHandledScreen() {
		this.openScreenHandler = this.playerScreenHandler;
	}

	@Override
	public void tickRiding() {
		if (!this.world.isClient && this.isSneaking() && this.hasMount()) {
			this.stopRiding();
			this.setSneaking(false);
		} else {
			double d = this.x;
			double e = this.y;
			double f = this.z;
			float g = this.yaw;
			float h = this.pitch;
			super.tickRiding();
			this.prevStrideDistance = this.strideDistance;
			this.strideDistance = 0.0F;
			this.method_3212(this.x - d, this.y - e, this.z - f);
			if (this.getVehicle() instanceof PigEntity) {
				this.pitch = h;
				this.yaw = g;
				this.bodyYaw = ((PigEntity)this.getVehicle()).bodyYaw;
			}
		}
	}

	@Override
	public void afterSpawn() {
		this.setBounds(0.6F, 1.8F);
		super.afterSpawn();
		this.setHealth(this.getMaxHealth());
		this.deathTime = 0;
	}

	@Override
	protected void tickNewAi() {
		super.tickNewAi();
		this.tickHandSwing();
		this.headYaw = this.yaw;
	}

	@Override
	public void tickMovement() {
		if (this.abilityResyncCountdown > 0) {
			this.abilityResyncCountdown--;
		}

		if (this.world.getGlobalDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean("naturalRegeneration")) {
			if (this.getHealth() < this.getMaxHealth() && this.ticksAlive % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.hungerManager.isNotFull() && this.ticksAlive % 10 == 0) {
				this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
			}
		}

		this.inventory.updateItems();
		this.prevStrideDistance = this.strideDistance;
		super.tickMovement();
		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (!this.world.isClient) {
			entityAttributeInstance.setBaseValue((double)this.abilities.getWalkSpeed());
		}

		this.flyingSpeed = this.field_4006;
		if (this.isSprinting()) {
			this.flyingSpeed = (float)((double)this.flyingSpeed + (double)this.field_4006 * 0.3);
		}

		this.setMovementSpeed((float)entityAttributeInstance.getValue());
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		float g = (float)(Math.atan(-this.velocityY * 0.2F) * 15.0);
		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!this.onGround || this.getHealth() <= 0.0F) {
			f = 0.0F;
		}

		if (this.onGround || this.getHealth() <= 0.0F) {
			g = 0.0F;
		}

		this.strideDistance = this.strideDistance + (f - this.strideDistance) * 0.4F;
		this.field_6753 = this.field_6753 + (g - this.field_6753) * 0.8F;
		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			Box box;
			if (this.hasMount() && !this.getVehicle().removed) {
				box = this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0);
			} else {
				box = this.getBoundingBox().expand(1.0, 0.5, 1.0);
			}

			List<Entity> list = this.world.getEntitiesIn(this, box);

			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (!entity.removed) {
					this.collideWithEntity(entity);
				}
			}
		}

		this.method_14163(this.method_14158());
		this.method_14163(this.method_14159());
		if (!this.world.isClient && (this.fallDistance > 0.5F || this.isTouchingWater() || this.hasMount()) || this.abilities.flying) {
			this.method_14157();
		}
	}

	private void method_14163(@Nullable NbtCompound nbtCompound) {
		if (nbtCompound != null && !nbtCompound.contains("Silent") || !nbtCompound.getBoolean("Silent")) {
			String string = nbtCompound.getString("id");
			if (string.equals(EntityType.getId(ParrotEntity.class).toString())) {
				ParrotEntity.method_14102(this.world, this);
			}
		}
	}

	private void collideWithEntity(Entity entity) {
		entity.onPlayerCollision(this);
	}

	public int getScore() {
		return this.dataTracker.get(field_14793);
	}

	public void setScore(int score) {
		this.dataTracker.set(field_14793, score);
	}

	public void addScore(int score) {
		int i = this.getScore();
		this.dataTracker.set(field_14793, i + score);
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		this.setBounds(0.2F, 0.2F);
		this.updatePosition(this.x, this.y, this.z);
		this.velocityY = 0.1F;
		if ("Notch".equals(this.getTranslationKey())) {
			this.dropStack(new ItemStack(Items.APPLE, 1), true, false);
		}

		if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
			this.method_13618();
			this.inventory.dropAll();
		}

		if (source != null) {
			this.velocityX = (double)(-MathHelper.cos((this.knockbackVelocity + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F);
			this.velocityZ = (double)(-MathHelper.sin((this.knockbackVelocity + this.yaw) * (float) (Math.PI / 180.0)) * 0.1F);
		} else {
			this.velocityX = 0.0;
			this.velocityZ = 0.0;
		}

		this.incrementStat(Stats.DEATHS);
		this.method_11238(Stats.TIME_SINCE_DEATH);
		this.extinguish();
		this.setFlag(0, false);
	}

	protected void method_13618() {
		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.getInvStack(i);
			if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
				this.inventory.removeInvStack(i);
			}
		}
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		if (damageSource == DamageSource.ON_FIRE) {
			return Sounds.ENTITY_PLAYER_HURT_ON_FIRE;
		} else {
			return damageSource == DamageSource.DROWN ? Sounds.ENTITY_PLAYER_HURT_DROWN : Sounds.ENTITY_PLAYER_HURT;
		}
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_PLAYER_DEATH;
	}

	@Nullable
	public ItemEntity dropSelectedItem(boolean dropAll) {
		return this.dropStack(
			this.inventory
				.takeInvStack(this.inventory.selectedSlot, dropAll && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1),
			false,
			true
		);
	}

	@Nullable
	public ItemEntity dropItem(ItemStack stack, boolean bl) {
		return this.dropStack(stack, false, bl);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack stack, boolean bl, boolean incrementStats) {
		if (stack.isEmpty()) {
			return null;
		} else {
			double d = this.y - 0.3F + (double)this.getEyeHeight();
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, d, this.z, stack);
			itemEntity.setPickupDelay(40);
			if (incrementStats) {
				itemEntity.setThrower(this.getTranslationKey());
			}

			if (bl) {
				float f = this.random.nextFloat() * 0.5F;
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				itemEntity.velocityX = (double)(-MathHelper.sin(g) * f);
				itemEntity.velocityZ = (double)(MathHelper.cos(g) * f);
				itemEntity.velocityY = 0.2F;
			} else {
				float h = 0.3F;
				itemEntity.velocityX = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * h);
				itemEntity.velocityZ = (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * h);
				itemEntity.velocityY = (double)(-MathHelper.sin(this.pitch * (float) (Math.PI / 180.0)) * h + 0.1F);
				float i = this.random.nextFloat() * (float) (Math.PI * 2);
				h = 0.02F * this.random.nextFloat();
				itemEntity.velocityX = itemEntity.velocityX + Math.cos((double)i) * (double)h;
				itemEntity.velocityY = itemEntity.velocityY + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
				itemEntity.velocityZ = itemEntity.velocityZ + Math.sin((double)i) * (double)h;
			}

			ItemStack itemStack = this.method_3164(itemEntity);
			if (incrementStats) {
				if (!itemStack.isEmpty()) {
					this.incrementStat(Stats.dropped(itemStack.getItem()), stack.getCount());
				}

				this.incrementStat(Stats.DROPS);
			}

			return itemEntity;
		}
	}

	protected ItemStack method_3164(ItemEntity itemEntity) {
		this.world.spawnEntity(itemEntity);
		return itemEntity.getItemStack();
	}

	public float method_13261(BlockState blockState) {
		float f = this.inventory.method_13252(blockState);
		if (f > 1.0F) {
			int i = EnchantmentHelper.getEfficiency(this);
			ItemStack itemStack = this.getMainHandStack();
			if (i > 0 && !itemStack.isEmpty()) {
				f += (float)(i * i + 1);
			}
		}

		if (this.hasStatusEffect(StatusEffects.HASTE)) {
			f *= 1.0F + (float)(this.getEffectInstance(StatusEffects.HASTE).getAmplifier() + 1) * 0.2F;
		}

		if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
			float g;
			switch (this.getEffectInstance(StatusEffects.MINING_FATIGUE).getAmplifier()) {
				case 0:
					g = 0.3F;
					break;
				case 1:
					g = 0.09F;
					break;
				case 2:
					g = 0.0027F;
					break;
				case 3:
				default:
					g = 8.1E-4F;
			}

			f *= g;
		}

		if (this.isSubmergedIn(Material.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
			f /= 5.0F;
		}

		if (!this.onGround) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean method_13265(BlockState blockState) {
		return this.inventory.method_13255(blockState);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.PLAYER, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				DataFixerFactory.updateItemList(dataFixer, tag, dataVersion, "Inventory");
				DataFixerFactory.updateItemList(dataFixer, tag, dataVersion, "EnderItems");
				if (tag.contains("ShoulderEntityLeft", 10)) {
					tag.put("ShoulderEntityLeft", dataFixer.update(LevelDataType.ENTITY, tag.getCompound("ShoulderEntityLeft"), dataVersion));
				}

				if (tag.contains("ShoulderEntityRight", 10)) {
					tag.put("ShoulderEntityRight", dataFixer.update(LevelDataType.ENTITY, tag.getCompound("ShoulderEntityRight"), dataVersion));
				}

				return tag;
			}
		});
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setUuid(getUuidFromProfile(this.gameProfile));
		NbtList nbtList = nbt.getList("Inventory", 10);
		this.inventory.deserialize(nbtList);
		this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
		this.inBed = nbt.getBoolean("Sleeping");
		this.sleepTimer = nbt.getShort("SleepTimer");
		this.experienceProgress = nbt.getFloat("XpP");
		this.experienceLevel = nbt.getInt("XpLevel");
		this.totalExperience = nbt.getInt("XpTotal");
		this.enchantmentTableSeed = nbt.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(nbt.getInt("Score"));
		if (this.inBed) {
			this.pos = new BlockPos(this);
			this.awaken(true, true, false);
		}

		if (nbt.contains("SpawnX", 99) && nbt.contains("SpawnY", 99) && nbt.contains("SpawnZ", 99)) {
			this.spawnPos = new BlockPos(nbt.getInt("SpawnX"), nbt.getInt("SpawnY"), nbt.getInt("SpawnZ"));
			this.spawnForced = nbt.getBoolean("SpawnForced");
		}

		this.hungerManager.deserialize(nbt);
		this.abilities.deserialize(nbt);
		if (nbt.contains("EnderItems", 9)) {
			NbtList nbtList2 = nbt.getList("EnderItems", 10);
			this.enderChest.readNbtList(nbtList2);
		}

		if (nbt.contains("ShoulderEntityLeft", 10)) {
			this.method_14161(nbt.getCompound("ShoulderEntityLeft"));
		}

		if (nbt.contains("ShoulderEntityRight", 10)) {
			this.method_14162(nbt.getCompound("ShoulderEntityRight"));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("DataVersion", 1343);
		nbt.put("Inventory", this.inventory.serialize(new NbtList()));
		nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		nbt.putBoolean("Sleeping", this.inBed);
		nbt.putShort("SleepTimer", (short)this.sleepTimer);
		nbt.putFloat("XpP", this.experienceProgress);
		nbt.putInt("XpLevel", this.experienceLevel);
		nbt.putInt("XpTotal", this.totalExperience);
		nbt.putInt("XpSeed", this.enchantmentTableSeed);
		nbt.putInt("Score", this.getScore());
		if (this.spawnPos != null) {
			nbt.putInt("SpawnX", this.spawnPos.getX());
			nbt.putInt("SpawnY", this.spawnPos.getY());
			nbt.putInt("SpawnZ", this.spawnPos.getZ());
			nbt.putBoolean("SpawnForced", this.spawnForced);
		}

		this.hungerManager.serialize(nbt);
		this.abilities.serialize(nbt);
		nbt.put("EnderItems", this.enderChest.toNbtList());
		if (!this.method_14158().isEmpty()) {
			nbt.put("ShoulderEntityLeft", this.method_14158());
		}

		if (!this.method_14159().isEmpty()) {
			nbt.put("ShoulderEntityRight", this.method_14159());
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.abilities.invulnerable && !source.isOutOfWorld()) {
			return false;
		} else {
			this.despawnCounter = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else {
				if (this.isSleeping() && !this.world.isClient) {
					this.awaken(true, true, false);
				}

				this.method_14157();
				if (source.isScaledWithDifficulty()) {
					if (this.world.getGlobalDifficulty() == Difficulty.PEACEFUL) {
						amount = 0.0F;
					}

					if (this.world.getGlobalDifficulty() == Difficulty.EASY) {
						amount = Math.min(amount / 2.0F + 1.0F, amount);
					}

					if (this.world.getGlobalDifficulty() == Difficulty.HARD) {
						amount = amount * 3.0F / 2.0F;
					}
				}

				return amount == 0.0F ? false : super.damage(source, amount);
			}
		}
	}

	@Override
	protected void method_13947(LivingEntity livingEntity) {
		super.method_13947(livingEntity);
		if (livingEntity.getMainHandStack().getItem() instanceof AxeItem) {
			this.method_13619(true);
		}
	}

	public boolean shouldDamagePlayer(PlayerEntity player) {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		AbstractTeam abstractTeam2 = player.getScoreboardTeam();
		if (abstractTeam == null) {
			return true;
		} else {
			return !abstractTeam.isEqual(abstractTeam2) ? true : abstractTeam.isFriendlyFireAllowed();
		}
	}

	@Override
	protected void damageArmor(float value) {
		this.inventory.damageArmor(value);
	}

	@Override
	protected void method_13072(float f) {
		if (f >= 3.0F && this.field_14546.getItem() == Items.SHIELD) {
			int i = 1 + MathHelper.floor(f);
			this.field_14546.damage(i, this);
			if (this.field_14546.isEmpty()) {
				Hand hand = this.method_13062();
				if (hand == Hand.MAIN_HAND) {
					this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				} else {
					this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}

				this.field_14546 = ItemStack.EMPTY;
				this.playSound(Sounds.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
			}
		}
	}

	public float method_4575() {
		int i = 0;

		for (ItemStack itemStack : this.inventory.field_15083) {
			if (!itemStack.isEmpty()) {
				i++;
			}
		}

		return (float)i / (float)this.inventory.field_15083.size();
	}

	@Override
	protected void applyDamage(DamageSource source, float damage) {
		if (!this.isInvulnerableTo(source)) {
			damage = this.applyArmorDamage(source, damage);
			damage = this.applyEnchantmentsToDamage(source, damage);
			float var7 = Math.max(damage - this.getAbsorption(), 0.0F);
			this.setAbsorption(this.getAbsorption() - (damage - var7));
			if (var7 != 0.0F) {
				this.addExhaustion(source.getExhaustion());
				float g = this.getHealth();
				this.setHealth(this.getHealth() - var7);
				this.getDamageTracker().onDamage(source, g, var7);
				if (var7 < 3.4028235E37F) {
					this.incrementStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
				}
			}
		}
	}

	public void openEditSignScreen(SignBlockEntity sign) {
	}

	public void openCommandBlockScreen(CommandBlockExecutor executor) {
	}

	public void method_13260(CommandBlockBlockEntity commandBlockBlockEntity) {
	}

	public void method_13565(StructureBlockEntity structureBlockEntity) {
	}

	public void openTradingScreen(Trader trader) {
	}

	public void openInventory(Inventory inventory) {
	}

	public void method_6317(AbstractHorseEntity abstractHorseEntity, Inventory inventory) {
	}

	public void openHandledScreen(NamedScreenHandlerFactory screenHandlerFactory) {
	}

	public void method_3201(ItemStack stack, Hand hand) {
	}

	public ActionResult method_13616(Entity entity, Hand hand) {
		if (this.isSpectator()) {
			if (entity instanceof Inventory) {
				this.openInventory((Inventory)entity);
			}

			return ActionResult.PASS;
		} else {
			ItemStack itemStack = this.getStackInHand(hand);
			ItemStack itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
			if (entity.interact(this, hand)) {
				if (this.abilities.creativeMode && itemStack == this.getStackInHand(hand) && itemStack.getCount() < itemStack2.getCount()) {
					itemStack.setCount(itemStack2.getCount());
				}

				return ActionResult.SUCCESS;
			} else {
				if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					if (itemStack.method_6329(this, (LivingEntity)entity, hand)) {
						if (itemStack.isEmpty() && !this.abilities.creativeMode) {
							this.equipStack(hand, ItemStack.EMPTY);
						}

						return ActionResult.SUCCESS;
					}
				}

				return ActionResult.PASS;
			}
		}
	}

	@Override
	public double getHeightOffset() {
		return -0.35;
	}

	@Override
	public void stopRiding() {
		super.stopRiding();
		this.ridingCooldown = 0;
	}

	public void attack(Entity entity) {
		if (entity.isAttackable()) {
			if (!entity.handleAttack(this)) {
				float f = (float)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
				float g;
				if (entity instanceof LivingEntity) {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)entity).getGroup());
				} else {
					g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
				}

				float i = this.method_13275(0.5F);
				f *= 0.2F + i * i * 0.8F;
				g *= i;
				this.method_13269();
				if (f > 0.0F || g > 0.0F) {
					boolean bl = i > 0.9F;
					boolean bl2 = false;
					int j = 0;
					j += EnchantmentHelper.getKnockback(this);
					if (this.isSprinting() && bl) {
						this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
						j++;
						bl2 = true;
					}

					boolean bl3 = bl
						&& this.fallDistance > 0.0F
						&& !this.onGround
						&& !this.isClimbing()
						&& !this.isTouchingWater()
						&& !this.hasStatusEffect(StatusEffects.BLINDNESS)
						&& !this.hasMount()
						&& entity instanceof LivingEntity;
					bl3 = bl3 && !this.isSprinting();
					if (bl3) {
						f *= 1.5F;
					}

					f += g;
					boolean bl4 = false;
					double d = (double)(this.horizontalSpeed - this.prevHorizontalSpeed);
					if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed()) {
						ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
						if (itemStack.getItem() instanceof SwordItem) {
							bl4 = true;
						}
					}

					float k = 0.0F;
					boolean bl5 = false;
					int l = EnchantmentHelper.getFireAspect(this);
					if (entity instanceof LivingEntity) {
						k = ((LivingEntity)entity).getHealth();
						if (l > 0 && !entity.isOnFire()) {
							bl5 = true;
							entity.setOnFireFor(1);
						}
					}

					double e = entity.velocityX;
					double m = entity.velocityY;
					double n = entity.velocityZ;
					boolean bl6 = entity.damage(DamageSource.player(this), f);
					if (bl6) {
						if (j > 0) {
							if (entity instanceof LivingEntity) {
								((LivingEntity)entity)
									.method_6109(
										this, (float)j * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
							} else {
								entity.addVelocity(
									(double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * (float)j * 0.5F),
									0.1,
									(double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * (float)j * 0.5F)
								);
							}

							this.velocityX *= 0.6;
							this.velocityZ *= 0.6;
							this.setSprinting(false);
						}

						if (bl4) {
							float o = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;

							for (LivingEntity livingEntity : this.world.getEntitiesInBox(LivingEntity.class, entity.getBoundingBox().expand(1.0, 0.25, 1.0))) {
								if (livingEntity != this && livingEntity != entity && !this.isTeammate(livingEntity) && this.squaredDistanceTo(livingEntity) < 9.0) {
									livingEntity.method_6109(
										this, 0.4F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
									);
									livingEntity.damage(DamageSource.player(this), o);
								}
							}

							this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
							this.method_13267();
						}

						if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
							((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
							entity.velocityModified = false;
							entity.velocityX = e;
							entity.velocityY = m;
							entity.velocityZ = n;
						}

						if (bl3) {
							this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
							this.addCritParticles(entity);
						}

						if (!bl3 && !bl4) {
							if (bl) {
								this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
							} else {
								this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
							}
						}

						if (g > 0.0F) {
							this.addEnchantedHitParticles(entity);
						}

						this.method_6150(entity);
						if (entity instanceof LivingEntity) {
							EnchantmentHelper.onUserDamaged((LivingEntity)entity, this);
						}

						EnchantmentHelper.onTargetDamaged(this, entity);
						ItemStack itemStack2 = this.getMainHandStack();
						Entity entity2 = entity;
						if (entity instanceof EnderDragonPart) {
							MultipartEntityProvider multipartEntityProvider = ((EnderDragonPart)entity).owner;
							if (multipartEntityProvider instanceof LivingEntity) {
								entity2 = (LivingEntity)multipartEntityProvider;
							}
						}

						if (!itemStack2.isEmpty() && entity2 instanceof LivingEntity) {
							itemStack2.onEntityHit((LivingEntity)entity2, this);
							if (itemStack2.isEmpty()) {
								this.equipStack(Hand.MAIN_HAND, ItemStack.EMPTY);
							}
						}

						if (entity instanceof LivingEntity) {
							float p = k - ((LivingEntity)entity).getHealth();
							this.incrementStat(Stats.DAMAGE_DEALT, Math.round(p * 10.0F));
							if (l > 0) {
								entity.setOnFireFor(l * 4);
							}

							if (this.world instanceof ServerWorld && p > 2.0F) {
								int q = (int)((double)p * 0.5);
								((ServerWorld)this.world)
									.addParticle(ParticleType.DAMAGE_INDICATOR, entity.x, entity.y + (double)(entity.height * 0.5F), entity.z, q, 0.1, 0.0, 0.1, 0.2);
							}
						}

						this.addExhaustion(0.1F);
					} else {
						this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
						if (bl5) {
							entity.extinguish();
						}
					}
				}
			}
		}
	}

	public void method_13619(boolean bl) {
		float f = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
		if (bl) {
			f += 0.75F;
		}

		if (this.random.nextFloat() < f) {
			this.getItemCooldownManager().method_11384(Items.SHIELD, 100);
			this.method_13053();
			this.world.sendEntityStatus(this, (byte)30);
		}
	}

	public void addCritParticles(Entity target) {
	}

	public void addEnchantedHitParticles(Entity target) {
	}

	public void method_13267() {
		double d = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)));
		double e = (double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).addParticle(ParticleType.SWEEP_ATTACK, this.x + d, this.y + (double)this.height * 0.5, this.z + e, 0, d, 0.0, e, 0.0);
		}
	}

	public void requestRespawn() {
	}

	@Override
	public void remove() {
		super.remove();
		this.playerScreenHandler.close(this);
		if (this.openScreenHandler != null) {
			this.openScreenHandler.close(this);
		}
	}

	@Override
	public boolean isInsideWall() {
		return !this.inBed && super.isInsideWall();
	}

	public boolean isMainPlayer() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public PlayerEntity.SleepStatus attemptSleep(BlockPos pos) {
		Direction direction = this.world.getBlockState(pos).get(HorizontalFacingBlock.DIRECTION);
		if (!this.world.isClient) {
			if (this.isSleeping() || !this.isAlive()) {
				return PlayerEntity.SleepStatus.OTHER;
			}

			if (!this.world.dimension.canPlayersSleep()) {
				return PlayerEntity.SleepStatus.NOT_POSSIBLE_HERE;
			}

			if (this.world.isDay()) {
				return PlayerEntity.SleepStatus.NOT_POSSIBLE_NOW;
			}

			if (!this.method_13615(pos, direction)) {
				return PlayerEntity.SleepStatus.TOO_FAR_AWAY;
			}

			double d = 8.0;
			double e = 5.0;
			List<HostileEntity> list = this.world
				.getEntitiesInBox(
					HostileEntity.class,
					new Box(
						(double)pos.getX() - 8.0,
						(double)pos.getY() - 5.0,
						(double)pos.getZ() - 8.0,
						(double)pos.getX() + 8.0,
						(double)pos.getY() + 5.0,
						(double)pos.getZ() + 8.0
					),
					new PlayerEntity.class_3174(this)
				);
			if (!list.isEmpty()) {
				return PlayerEntity.SleepStatus.NOT_SAFE;
			}
		}

		if (this.hasMount()) {
			this.stopRiding();
		}

		this.method_14157();
		this.setBounds(0.2F, 0.2F);
		if (this.world.blockExists(pos)) {
			float f = 0.5F + (float)direction.getOffsetX() * 0.4F;
			float g = 0.5F + (float)direction.getOffsetZ() * 0.4F;
			this.method_11237(direction);
			this.updatePosition((double)((float)pos.getX() + f), (double)((float)pos.getY() + 0.6875F), (double)((float)pos.getZ() + g));
		} else {
			this.updatePosition((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.6875F), (double)((float)pos.getZ() + 0.5F));
		}

		this.inBed = true;
		this.sleepTimer = 0;
		this.pos = pos;
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		if (!this.world.isClient) {
			this.world.updateSleepingStatus();
		}

		return PlayerEntity.SleepStatus.OK;
	}

	private boolean method_13615(BlockPos blockPos, Direction direction) {
		if (Math.abs(this.x - (double)blockPos.getX()) <= 3.0
			&& Math.abs(this.y - (double)blockPos.getY()) <= 2.0
			&& Math.abs(this.z - (double)blockPos.getZ()) <= 3.0) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
			return Math.abs(this.x - (double)blockPos2.getX()) <= 3.0
				&& Math.abs(this.y - (double)blockPos2.getY()) <= 2.0
				&& Math.abs(this.z - (double)blockPos2.getZ()) <= 3.0;
		}
	}

	private void method_11237(Direction direction) {
		this.field_3993 = -1.8F * (float)direction.getOffsetX();
		this.field_3994 = -1.8F * (float)direction.getOffsetZ();
	}

	public void awaken(boolean bl, boolean bl2, boolean setSpawn) {
		this.setBounds(0.6F, 1.8F);
		BlockState blockState = this.world.getBlockState(this.pos);
		if (this.pos != null && blockState.getBlock() == Blocks.BED) {
			this.world.setBlockState(this.pos, blockState.with(BedBlock.OCCUPIED, false), 4);
			BlockPos blockPos = BedBlock.findSpawnablePos(this.world, this.pos, 0);
			if (blockPos == null) {
				blockPos = this.pos.up();
			}

			this.updatePosition((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.1F), (double)((float)blockPos.getZ() + 0.5F));
		}

		this.inBed = false;
		if (!this.world.isClient && bl2) {
			this.world.updateSleepingStatus();
		}

		this.sleepTimer = bl ? 0 : 100;
		if (setSpawn) {
			this.setPlayerSpawn(this.pos, false);
		}
	}

	private boolean method_3213() {
		return this.world.getBlockState(this.pos).getBlock() == Blocks.BED;
	}

	@Nullable
	public static BlockPos findRespawnPosition(World world, BlockPos pos, boolean bl) {
		Block block = world.getBlockState(pos).getBlock();
		if (block != Blocks.BED) {
			if (!bl) {
				return null;
			} else {
				boolean bl2 = block.canMobSpawnInside();
				boolean bl3 = world.getBlockState(pos.up()).getBlock().canMobSpawnInside();
				return bl2 && bl3 ? pos : null;
			}
		} else {
			return BedBlock.findSpawnablePos(world, pos, 0);
		}
	}

	public float method_3183() {
		if (this.pos != null) {
			Direction direction = this.world.getBlockState(this.pos).get(HorizontalFacingBlock.DIRECTION);
			switch (direction) {
				case SOUTH:
					return 90.0F;
				case WEST:
					return 0.0F;
				case NORTH:
					return 270.0F;
				case EAST:
					return 180.0F;
			}
		}

		return 0.0F;
	}

	@Override
	public boolean isSleeping() {
		return this.inBed;
	}

	public boolean isSleepingLongEnough() {
		return this.inBed && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void sendMessage(Text text, boolean actionBar) {
	}

	public BlockPos getSpawnPosition() {
		return this.spawnPos;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void setPlayerSpawn(BlockPos pos, boolean spawnForced) {
		if (pos != null) {
			this.spawnPos = pos;
			this.spawnForced = spawnForced;
		} else {
			this.spawnPos = null;
			this.spawnForced = false;
		}
	}

	public void incrementStat(Stat stat) {
		this.incrementStat(stat, 1);
	}

	public void incrementStat(Stat stat, int amount) {
	}

	public void method_11238(Stat stat) {
	}

	public void method_14154(List<RecipeType> recipes) {
	}

	public void method_14155(Identifier[] identifiers) {
	}

	public void method_14156(List<RecipeType> list) {
	}

	@Override
	public void jump() {
		super.jump();
		this.incrementStat(Stats.JUMPS);
		if (this.isSprinting()) {
			this.addExhaustion(0.2F);
		} else {
			this.addExhaustion(0.05F);
		}
	}

	@Override
	public void method_2657(float f, float g, float h) {
		double d = this.x;
		double e = this.y;
		double i = this.z;
		if (this.abilities.flying && !this.hasMount()) {
			double j = this.velocityY;
			float k = this.flyingSpeed;
			this.flyingSpeed = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.method_2657(f, g, h);
			this.velocityY = j * 0.6;
			this.flyingSpeed = k;
			this.fallDistance = 0.0F;
			this.setFlag(7, false);
		} else {
			super.method_2657(f, g, h);
		}

		this.method_3209(this.x - d, this.y - e, this.z - i);
	}

	@Override
	public float getMovementSpeed() {
		return (float)this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue();
	}

	public void method_3209(double d, double e, double f) {
		if (!this.hasMount()) {
			if (this.isSubmergedIn(Material.WATER)) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.incrementStat(Stats.CM_DIVED, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isTouchingWater()) {
				int j = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (j > 0) {
					this.incrementStat(Stats.CM_SWUM, j);
					this.addExhaustion(0.01F * (float)j * 0.01F);
				}
			} else if (this.isClimbing()) {
				if (e > 0.0) {
					this.incrementStat(Stats.CM_CLIMB, (int)Math.round(e * 100.0));
				}
			} else if (this.onGround) {
				int k = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (k > 0) {
					if (this.isSprinting()) {
						this.incrementStat(Stats.CM_SPRINTED, k);
						this.addExhaustion(0.1F * (float)k * 0.01F);
					} else if (this.isSneaking()) {
						this.incrementStat(Stats.CM_SNEAKED, k);
						this.addExhaustion(0.0F * (float)k * 0.01F);
					} else {
						this.incrementStat(Stats.CM_WALKED, k);
						this.addExhaustion(0.0F * (float)k * 0.01F);
					}
				}
			} else if (this.method_13055()) {
				int l = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				this.incrementStat(Stats.AVIATE_ONE_CM, l);
			} else {
				int m = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (m > 25) {
					this.incrementStat(Stats.CM_FLOWN, m);
				}
			}
		}
	}

	private void method_3212(double d, double e, double f) {
		if (this.hasMount()) {
			int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
			if (i > 0) {
				if (this.getVehicle() instanceof AbstractMinecartEntity) {
					this.incrementStat(Stats.CM_MINECART, i);
				} else if (this.getVehicle() instanceof BoatEntity) {
					this.incrementStat(Stats.CM_SAILED, i);
				} else if (this.getVehicle() instanceof PigEntity) {
					this.incrementStat(Stats.CM_PIG, i);
				} else if (this.getVehicle() instanceof AbstractHorseEntity) {
					this.incrementStat(Stats.CM_HORSE, i);
				}
			}
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (!this.abilities.allowFlying) {
			if (fallDistance >= 2.0F) {
				this.incrementStat(Stats.CM_FALLEN, (int)Math.round((double)fallDistance * 100.0));
			}

			super.handleFallDamage(fallDistance, damageMultiplier);
		}
	}

	@Override
	protected void onSwimmingStart() {
		if (!this.isSpectator()) {
			super.onSwimmingStart();
		}
	}

	@Override
	protected Sound getLandSound(int height) {
		return height > 4 ? Sounds.ENTITY_PLAYER_BIG_FALL : Sounds.ENTITY_PLAYER_SMALL_FALL;
	}

	@Override
	public void onKilledOther(LivingEntity other) {
		EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)EntityType.SPAWN_EGGS.get(EntityType.getId(other));
		if (spawnEggData != null) {
			this.incrementStat(spawnEggData.killEntityStat);
		}
	}

	@Override
	public void setInLava() {
		if (!this.abilities.flying) {
			super.setInLava();
		}
	}

	public void addExperience(int experience) {
		this.addScore(experience);
		int i = Integer.MAX_VALUE - this.totalExperience;
		if (experience > i) {
			experience = i;
		}

		this.experienceProgress = this.experienceProgress + (float)experience / (float)this.getNextLevelExperience();

		for (this.totalExperience += experience;
			this.experienceProgress >= 1.0F;
			this.experienceProgress = this.experienceProgress / (float)this.getNextLevelExperience()
		) {
			this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getNextLevelExperience();
			this.incrementXp(1);
		}
	}

	public int getEnchantmentTableSeed() {
		return this.enchantmentTableSeed;
	}

	public void method_3172(ItemStack itemStack, int i) {
		this.experienceLevel -= i;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		this.enchantmentTableSeed = this.random.nextInt();
	}

	public void incrementXp(int xp) {
		this.experienceLevel += xp;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		if (xp > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.ticksAlive - 100.0F) {
			float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
			this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
			this.lastPlayedLevelUpSoundTime = this.ticksAlive;
		}
	}

	public int getNextLevelExperience() {
		if (this.experienceLevel >= 30) {
			return 112 + (this.experienceLevel - 30) * 9;
		} else {
			return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
		}
	}

	public void addExhaustion(float exhaustion) {
		if (!this.abilities.invulnerable) {
			if (!this.world.isClient) {
				this.hungerManager.addExhaustion(exhaustion);
			}
		}
	}

	public HungerManager getHungerManager() {
		return this.hungerManager;
	}

	public boolean canConsume(boolean ignoreHunger) {
		return (ignoreHunger || this.hungerManager.isNotFull()) && !this.abilities.invulnerable;
	}

	public boolean canFoodHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

	public boolean canModifyWorld() {
		return this.abilities.allowModifyWorld;
	}

	public boolean canModify(BlockPos pos, Direction direction, ItemStack stack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else if (stack.isEmpty()) {
			return false;
		} else {
			BlockPos blockPos = pos.offset(direction.getOpposite());
			Block block = this.world.getBlockState(blockPos).getBlock();
			return stack.canPlaceOn(block) || stack.hasSubTypes();
		}
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
			int i = this.experienceLevel * 7;
			return i > 100 ? 100 : i;
		} else {
			return 0;
		}
	}

	@Override
	protected boolean shouldAlwaysDropXp() {
		return true;
	}

	@Override
	public boolean shouldRenderName() {
		return true;
	}

	@Override
	protected boolean canClimb() {
		return !this.abilities.flying;
	}

	public void sendAbilitiesUpdate() {
	}

	public void method_3170(GameMode gamemode) {
	}

	@Override
	public String getTranslationKey() {
		return this.gameProfile.getName();
	}

	public EnderChestInventory getEnderChestInventory() {
		return this.enderChest;
	}

	@Override
	public ItemStack getStack(EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND) {
			return this.inventory.getMainHandStack();
		} else if (slot == EquipmentSlot.OFFHAND) {
			return this.inventory.field_15084.get(0);
		} else {
			return slot.getType() == EquipmentSlot.Type.ARMOR ? this.inventory.field_15083.get(slot.method_13032()) : ItemStack.EMPTY;
		}
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			this.method_13045(stack);
			this.inventory.field_15082.set(this.inventory.selectedSlot, stack);
		} else if (slot == EquipmentSlot.OFFHAND) {
			this.method_13045(stack);
			this.inventory.field_15084.set(0, stack);
		} else if (slot.getType() == EquipmentSlot.Type.ARMOR) {
			this.method_13045(stack);
			this.inventory.field_15083.set(slot.method_13032(), stack);
		}
	}

	public boolean method_13617(ItemStack stack) {
		this.method_13045(stack);
		return this.inventory.insertStack(stack);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return Lists.newArrayList(new ItemStack[]{this.getMainHandStack(), this.getOffHandStack()});
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.inventory.field_15083;
	}

	public boolean method_14160(NbtCompound nbtCompound) {
		if (this.hasMount() || !this.onGround || this.isTouchingWater()) {
			return false;
		} else if (this.method_14158().isEmpty()) {
			this.method_14161(nbtCompound);
			return true;
		} else if (this.method_14159().isEmpty()) {
			this.method_14162(nbtCompound);
			return true;
		} else {
			return false;
		}
	}

	protected void method_14157() {
		this.method_14164(this.method_14158());
		this.method_14161(new NbtCompound());
		this.method_14164(this.method_14159());
		this.method_14162(new NbtCompound());
	}

	private void method_14164(@Nullable NbtCompound nbtCompound) {
		if (!this.world.isClient && !nbtCompound.isEmpty()) {
			Entity entity = EntityType.createInstanceFromNbt(nbtCompound, this.world);
			if (entity instanceof TameableEntity) {
				((TameableEntity)entity).method_13092(this.playerUuid);
			}

			entity.updatePosition(this.x, this.y + 0.7F, this.z);
			this.world.spawnEntity(entity);
		}
	}

	@Override
	public boolean isInvisibleTo(PlayerEntity player) {
		if (!this.isInvisible()) {
			return false;
		} else if (player.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam == null || player == null || player.getScoreboardTeam() != abstractTeam || !abstractTeam.shouldShowFriendlyInvisibles();
		}
	}

	public abstract boolean isSpectator();

	public abstract boolean isCreative();

	@Override
	public boolean canFly() {
		return !this.abilities.flying;
	}

	public Scoreboard getScoreboard() {
		return this.world.getScoreboard();
	}

	@Override
	public AbstractTeam getScoreboardTeam() {
		return this.getScoreboard().getPlayerTeam(this.getTranslationKey());
	}

	@Override
	public Text getName() {
		Text text = new LiteralText(Team.decorateName(this.getScoreboardTeam(), this.getTranslationKey()));
		text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getTranslationKey() + " "));
		text.getStyle().setHoverEvent(this.getHoverEvent());
		text.getStyle().setInsertion(this.getTranslationKey());
		return text;
	}

	@Override
	public float getEyeHeight() {
		float f = 1.62F;
		if (this.isSleeping()) {
			f = 0.2F;
		} else if (this.isSneaking() || this.height == 1.65F) {
			f -= 0.08F;
		} else if (this.method_13055() || this.height == 0.6F) {
			f = 0.4F;
		}

		return f;
	}

	@Override
	public void setAbsorption(float absorption) {
		if (absorption < 0.0F) {
			absorption = 0.0F;
		}

		this.getDataTracker().set(field_14792, absorption);
	}

	@Override
	public float getAbsorption() {
		return this.getDataTracker().get(field_14792);
	}

	public static UUID getUuidFromProfile(GameProfile profile) {
		UUID uUID = profile.getId();
		if (uUID == null) {
			uUID = getOfflinePlayerUuid(profile.getName());
		}

		return uUID;
	}

	public static UUID getOfflinePlayerUuid(String nickname) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
	}

	public boolean isScreenLocked(ScreenHandlerLock screenLock) {
		if (screenLock.hasLock()) {
			return true;
		} else {
			ItemStack itemStack = this.getMainHandStack();
			return !itemStack.isEmpty() && itemStack.hasCustomName() ? itemStack.getCustomName().equals(screenLock.getKey()) : false;
		}
	}

	public boolean isPartVisible(PlayerModelPart modelPart) {
		return (this.getDataTracker().get(field_14796) & modelPart.getBitFlag()) == modelPart.getBitFlag();
	}

	@Override
	public boolean sendCommandFeedback() {
		return this.getMinecraftServer().worlds[0].getGameRules().getBoolean("sendCommandFeedback");
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot >= 0 && slot < this.inventory.field_15082.size()) {
			this.inventory.setInvStack(slot, item);
			return true;
		} else {
			EquipmentSlot equipmentSlot;
			if (slot == 100 + EquipmentSlot.HEAD.method_13032()) {
				equipmentSlot = EquipmentSlot.HEAD;
			} else if (slot == 100 + EquipmentSlot.CHEST.method_13032()) {
				equipmentSlot = EquipmentSlot.CHEST;
			} else if (slot == 100 + EquipmentSlot.LEGS.method_13032()) {
				equipmentSlot = EquipmentSlot.LEGS;
			} else if (slot == 100 + EquipmentSlot.FEET.method_13032()) {
				equipmentSlot = EquipmentSlot.FEET;
			} else {
				equipmentSlot = null;
			}

			if (slot == 98) {
				this.equipStack(EquipmentSlot.MAINHAND, item);
				return true;
			} else if (slot == 99) {
				this.equipStack(EquipmentSlot.OFFHAND, item);
				return true;
			} else if (equipmentSlot == null) {
				int i = slot - 200;
				if (i >= 0 && i < this.enderChest.getInvSize()) {
					this.enderChest.setInvStack(i, item);
					return true;
				} else {
					return false;
				}
			} else {
				if (!item.isEmpty()) {
					if (!(item.getItem() instanceof ArmorItem) && !(item.getItem() instanceof ElytraItem)) {
						if (equipmentSlot != EquipmentSlot.HEAD) {
							return false;
						}
					} else if (MobEntity.method_13083(item) != equipmentSlot) {
						return false;
					}
				}

				this.inventory.setInvStack(equipmentSlot.method_13032() + this.inventory.field_15082.size(), item);
				return true;
			}
		}
	}

	public boolean getReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	public void setReducedDebugInfo(boolean reducedDebugInfo) {
		this.reducedDebugInfo = reducedDebugInfo;
	}

	@Override
	public HandOption getDurability() {
		return this.dataTracker.get(field_14797) == 0 ? HandOption.LEFT : HandOption.RIGHT;
	}

	public void method_13264(HandOption handOption) {
		this.dataTracker.set(field_14797, (byte)(handOption == HandOption.LEFT ? 0 : 1));
	}

	public NbtCompound method_14158() {
		return this.dataTracker.get(field_15625);
	}

	protected void method_14161(NbtCompound nbtCompound) {
		this.dataTracker.set(field_15625, nbtCompound);
	}

	public NbtCompound method_14159() {
		return this.dataTracker.get(field_15626);
	}

	protected void method_14162(NbtCompound nbtCompound) {
		this.dataTracker.set(field_15626, nbtCompound);
	}

	public float method_13268() {
		return (float)(1.0 / this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_SPEED).getValue() * 20.0);
	}

	public float method_13275(float f) {
		return MathHelper.clamp(((float)this.field_14542 + f) / this.method_13268(), 0.0F, 1.0F);
	}

	public void method_13269() {
		this.field_14542 = 0;
	}

	public class_2686 getItemCooldownManager() {
		return this.field_14795;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.isSleeping()) {
			super.pushAwayFrom(entity);
		}
	}

	public float method_13271() {
		return (float)this.initializeAttribute(EntityAttributes.GENERIC_LUCK).getValue();
	}

	public boolean method_13567() {
		return this.abilities.creativeMode && this.canUseCommand(2, "");
	}

	public static enum ChatVisibilityType {
		FULL(0, "options.chat.visibility.full"),
		SYSTEM(1, "options.chat.visibility.system"),
		HIDDEN(2, "options.chat.visibility.hidden");

		private static final PlayerEntity.ChatVisibilityType[] TYPES = new PlayerEntity.ChatVisibilityType[values().length];
		private final int id;
		private final String name;

		private ChatVisibilityType(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		public static PlayerEntity.ChatVisibilityType getById(int id) {
			return TYPES[id % TYPES.length];
		}

		public String getName() {
			return this.name;
		}

		static {
			for (PlayerEntity.ChatVisibilityType chatVisibilityType : values()) {
				TYPES[chatVisibilityType.id] = chatVisibilityType;
			}
		}
	}

	public static enum SleepStatus {
		OK,
		NOT_POSSIBLE_HERE,
		NOT_POSSIBLE_NOW,
		TOO_FAR_AWAY,
		OTHER,
		NOT_SAFE;
	}

	static class class_3174 implements Predicate<HostileEntity> {
		private final PlayerEntity field_15627;

		private class_3174(PlayerEntity playerEntity) {
			this.field_15627 = playerEntity;
		}

		public boolean apply(@Nullable HostileEntity hostileEntity) {
			return hostileEntity.method_14129(this.field_15627);
		}
	}
}
